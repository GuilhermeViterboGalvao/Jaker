package com.br.jaker.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.res.AssetManager;
import android.util.Log;

import com.br.jaker.exception.BookExpection;
import com.br.jaker.exception.EditionException;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Book;
import com.br.jaker.model.Edition;
import com.br.jaker.model.Enterprise;

public class AppManager {
	
	private HttpGet requestGet;
	
	private HttpPost requestPost;
	
	private HttpResponse response;
	
	private HttpClient httpClient;
	
	private Enterprise enterprise;
	
	private List<Edition> editions;
	
	private List<Book> books;
	
	private File rootPath;	
	
	public AppManager(HttpClient httpClient, AssetManager assetManager, File rootPath) throws EnterpriseException, IOException, EditionException, BookExpection {
		this.httpClient = httpClient;
		enterprise = SAXEnterpriseParser.parseEnterprise(assetManager.open("Enterprise.xml"));
		
		if (enterprise != null) {
			
			editions = SAXEditionsParser.parseEdition(doGet(enterprise.getUrlJsonEdtions()));
			books = new ArrayList<Book>(editions.size());
			
			this.rootPath = new File(rootPath, "jaker/" + enterprise.getName());
			if (!this.rootPath.exists()) this.rootPath.mkdirs();
			
			File[] booksPaths = this.rootPath.listFiles();
			for (File bookPath : booksPaths) {
				if (bookPath.isDirectory()) {
					File bookJson = new File(bookPath, "book.json");
					if (bookJson.exists() && bookJson.isFile()) {
						books.add(JSONBookParser.parseBook(new FileInputStream(bookJson)));
					}
				}
			}
			
			//Verificar no diretório pricipal/nomedarevista os arquivos que lá tem.
			//Depois baixar os que são novos.
		}
	}
	
	public Book getBook(InputStream jsonBook) throws BookExpection {
		return JSONBookParser.parseBook(jsonBook);
	}
	
	public synchronized InputStream doGet(String url) {
		Log.i("AppManager.doGet", "url: " + url);
		
		requestGet = new HttpGet(url);
		requestGet.setHeader("Accept", "text/plain");
		
		try {
			response = httpClient.execute(requestGet);
		} catch (ClientProtocolException cpe) {
			Log.i("AppManager.doGet", "Execution stoped: " + cpe.getMessage());
			return null;
		} catch (IOException ioe) {
			Log.i("AppManager.doGet", "Execution stoped: " + ioe.getMessage());
			return null;
		} catch (Exception e) {
			Log.i("AppManager.doGet", "Execution stoped: " + e.getMessage());
			return null;
		}
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		if (statusCode != HttpStatus.SC_OK) {
			try {
				response.getEntity().getContent().close();
			} catch (IOException ioe) {
				Log.i("AppManager.doGet", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
			} catch (Exception e) {
				Log.i("AppManager.doGet", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
			}
			return null;
		}
		
		InputStream in = null;
		
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException ise) {
			in = null;
			Log.e("AppManager.doGet", "Error on read InputStream (response.getEntity().getContent()): " + ise.getMessage(), ise);
		} catch (IOException ioe) {
			in = null;
			Log.e("AppManager.doGet", "Error on read InputStream (response.getEntity().getContent()): " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			in = null;
			Log.e("AppManager.doGet", "Error on read InputStream (response.getEntity().getContent()): " + e.getMessage(), e);
		}
		
		return in;
	}
	
	public synchronized InputStream doPost(String url, String[] names, String[] values) {
		Log.i("AppManager.doPost", "url: " + url);
		
		requestPost = new HttpPost(url);
		requestPost.setHeader("Accept", "text/plain");
		
		InputStream in = null;
		
		if (values != null && names != null && values.length == names.length) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(values.length);
			
			for (int i = 0; i < names.length; i++) {
				nameValuePairs.add(new BasicNameValuePair(names[i], values[i]));		
			}
			
	        try {
				requestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException usfee) {
				Log.e("AppManager.doPost", "Error on 'requestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs))': " + usfee.getMessage(), usfee);
			} catch (Exception e) {
				Log.e("AppManager.doPost", "Error on 'requestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs))': " + e.getMessage(), e);
			}
	        
			try {
				response = httpClient.execute(requestPost);
			} catch (ClientProtocolException cpe) {
				Log.i("AppManager.doPost", "Execution stoped: " + cpe.getMessage());
			} catch (IOException ioe) {
				Log.i("AppManager.doPost", "Execution stoped: " + ioe.getMessage());
			} catch (Exception e) {
				Log.i("AppManager.doPost", "Execution stoped: " + e.getMessage());
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {
				try {
					response.getEntity().getContent().close();
				} catch (IOException ioe) {
					Log.i("AppManager.doPost", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
				} catch (Exception e) {
					Log.i("AppManager.doPost", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
				}
				return null;
			}
			
			try {
				in = response.getEntity().getContent();
			} catch (IllegalStateException ise) {
				in = null;
				Log.e("AppManager.doPost", "Error on 'response.getEntity().getContent()': " + ise.getMessage(), ise);
			} catch (IOException ioe) {
				in = null;
				Log.e("AppManager.doPost", "Error on 'response.getEntity().getContent()': " + ioe.getMessage(), ioe);
			} catch (Exception e) {
				in = null;
				Log.e("AppManager.doPost", "Error on 'response.getEntity().getContent()': " + e.getMessage(), e);
			}
		}
		
		return in;
	}
}