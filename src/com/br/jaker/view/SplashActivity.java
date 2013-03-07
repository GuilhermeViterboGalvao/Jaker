package com.br.jaker.view;

import java.io.BufferedInputStream; 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import com.br.jaker.control.JSONBookParser;
import com.br.jaker.control.SAXEditionsParser;
import com.br.jaker.control.SAXEnterpriseParser;
import com.br.jaker.exception.BookExpection;
import com.br.jaker.exception.EditionException;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Book;
import com.br.jaker.model.Edition;
import com.br.jaker.util.Messages;
import com.br.jaker.util.Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

//TODO 
/*
 * Quando cancelar o Download excluir o arquivo .zip;
 * Após extrair o arquivo remover o .zip;
 * Só fazer o download caso seja novo;
 * Problema no JSONParser dos Book. Problema no formato da data (2013-09-08);
 * */
public class SplashActivity extends Activity {

	private JakerApp jakerApp;
	
	private AsyncEditionsDownloader editionsDownloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		jakerApp = (JakerApp)getApplication();
						
		try {
			jakerApp.setEnterprise(SAXEnterpriseParser.parseEnterprise(getAssets().open("Enterprise.xml")));
		} catch (EnterpriseException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
		if (jakerApp.getEnterprise() != null) {
			jakerApp.setAppName(jakerApp.getEnterprise().getName() != null && !jakerApp.getEnterprise().getName().equals("") ? jakerApp.getEnterprise().getName() : "Jaker");
			
			File rootPath = new File(Environment.getExternalStorageDirectory(), jakerApp.getAppName());
			if (!rootPath.exists()) rootPath.mkdirs();
			jakerApp.setRootPath(rootPath);
			
			editionsDownloader = new AsyncEditionsDownloader();
			editionsDownloader.execute(jakerApp.getEnterprise().getUrlJsonEdtions());
		}		
	}
	
	private void processBooks() {
		List<Book> books = new ArrayList<Book>(jakerApp.getEditions().size());		
		File[] booksPaths = jakerApp.getRootPath().listFiles();
		for (File bookPath : booksPaths) {
			if (bookPath.isDirectory()) {
				File bookJson = new File(bookPath, "book.json");
				if (bookJson.exists() && bookJson.isFile()) {
					try {
						books.add(JSONBookParser.parseBook(new FileInputStream(bookJson)));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch blocks
					} catch (BookExpection e) {
						// TODO Auto-generated catch block
					}
				}
			}
		}		
		for (Edition edition : jakerApp.getEditions()) {
			if (edition.isNewEdition()) {
				boolean hasBook = false;
				for (Book book : books) {
					if (edition.getTitle().equals(book.getTitle())) {
						hasBook = true;
						break;
					}
				}
				if (!hasBook) new AsyncBookDownloader().execute(edition);
			}
		}		
	}
	
	private void proceed() {
		startActivity(new Intent(this, HomeActivity.class));
	}
	
	/***********************************************************************************************************************************************/
	public class AsyncEditionsDownloader extends AsyncTask<String, Void, List<Edition>> {

		private AlertDialog.Builder alertDialog;
		
		private ProgressDialog progressDialog;		
		
		private String errorMessage;
		
		@Override
		protected void onPreExecute() {
			if (jakerApp.isConnected()) {
				progressDialog = new ProgressDialog(SplashActivity.this);
				progressDialog.setTitle(jakerApp.getAppName());
				progressDialog.setMessage("Loading Editions...");
				progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!editionsDownloader.isCancelled()) editionsDownloader.cancel(true);
					}
				});   
				progressDialog.show();
				errorMessage = "";
			} else {
				errorMessage = Messages.notConnected;
				cancel(true);
			}			
		}
		
		@Override
		protected List<Edition> doInBackground(String... params) {
			if (params == null) 	  return null;
			if (params[0].equals("")) return null;
			
			String url = params[0];
			List<Edition> editions = null;
			
			if (!isCancelled()) {
				try {
					editions = SAXEditionsParser.parseEdition(Utils.doGet(url));
				} catch (EditionException e) {					
					Log.e("JakerApp.AsyncEditionsDownloader.doInBackground", "Problem on \"SAXEditionsParser.parseEdition(doGet(url))\": " + e.getMessage(), e);
					errorMessage = "The " + jakerApp.getAppName() + " app stopped working because: " + e.getMessage();
				} finally {
					if (editions == null && errorMessage != null && errorMessage.equals("")) {
						errorMessage = "The " + jakerApp.getAppName() + " app stopped working!";
					}
				}
			}
			
			return editions;
		}
		
		@Override
		protected void onCancelled() {
			cancelProgressDialog();
			if (errorMessage != null && !errorMessage.equals("")) showAlertDialog();
		}
		
		@Override
		protected void onPostExecute(List<Edition> editions) {
			cancelProgressDialog();
			if (editions != null) {
				jakerApp.setEditions(editions);
				processBooks();
			} else {
				showAlertDialog();
			}
		}
		
		private void showAlertDialog() {
			cancelProgressDialog();
			alertDialog = new AlertDialog.Builder(SplashActivity.this);
			alertDialog.setTitle(jakerApp.getAppName());
			alertDialog.setMessage(errorMessage);
			alertDialog.create();
			alertDialog.show();
		}
		
		private void cancelProgressDialog() {
			if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
		}
	}
	/***********************************************************************************************************************************************/
	
	/***********************************************************************************************************************************************/
	public class AsyncBookDownloader extends AsyncTask<Edition, Integer, Book> {

		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(SplashActivity.this);
			progressDialog.setTitle(jakerApp.getAppName());
			progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (!AsyncBookDownloader.this.isCancelled()) AsyncBookDownloader.this.cancel(true);
				}
			});
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.show();
		}
		
		@Override
		protected Book doInBackground(Edition... editions) {
			Edition edition = editions[0];
			
			if (edition != null) {				
				HttpGet requestGet = new HttpGet(edition.getDownloadUrl());
				requestGet.setHeader("Accept", "text/plain");
				
				HttpResponse response = null;				
				try {
					response = Utils.getHTTPClient().execute(requestGet);
				} catch (ClientProtocolException cpe) {
					Log.i("SplashActivity.AsyncBookDownloader.doInBackground", "Execution stoped: " + cpe.getMessage());
					return null;
				} catch (IOException ioe) {
					Log.i("SplashActivity.AsyncBookDownloader.doInBackground", "Execution stoped: " + ioe.getMessage());
					return null;
				} catch (Exception e) {
					Log.i("SplashActivity.AsyncBookDownloader.doInBackground", "Execution stoped: " + e.getMessage());
					return null;
				}
				
				int statusCode = response.getStatusLine().getStatusCode();				
				if (statusCode != HttpStatus.SC_OK) {
					try {
						response.getEntity().getContent().close();
					} catch (IOException ioe) {
						Log.i("SplashActivity.AsyncBookDownloader.doInBackground", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
					} catch (Exception e) {
						Log.i("SplashActivity.AsyncBookDownloader.doInBackground", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
					}
					return null;
				}
				
				long fileSize  = response.getEntity().getContentLength();
		        long totalRead = 0;        
		        int read = 0;
		        byte buffer[] = new byte[1024];
		        InputStream in = null;
		        OutputStream out = null;
		        File zip = new File(jakerApp.getRootPath(), edition.getNumber() + ".zip");				
				try {			
		            in = new BufferedInputStream(response.getEntity().getContent());
		            out = new FileOutputStream(zip);
		            while ( ((read = in.read(buffer)) != -1) && !isCancelled() ) {
		            	totalRead += read;                
		                out.write(buffer, 0, read);
		                publishProgress((int)fileSize, (int)(totalRead * 100 / fileSize));
		            }
				} catch (IllegalStateException ise) {
					Log.e("SplashActivity.AsyncBookDownloader.doInBackground", ise.getMessage(), ise);
				} catch (IOException ioe) {
					Log.e("SplashActivity.AsyncBookDownloader.doInBackground", ioe.getMessage(), ioe);
				} catch (Exception e) {
					Log.e("SplashActivity.AsyncBookDownloader.doInBackground", e.getMessage(), e);
				} finally {
					if(in  != null) try { in.close();  } catch (Exception e) {}
					if(out != null) try { out.close(); } catch (Exception e) {}
				}
				
				try {
					Utils.unzip(zip, jakerApp.getRootPath());
				} catch (IOException e) {
					//TODO
				}
				
				Book book = null;
				try {
					book = JSONBookParser.parseBook(new FileInputStream(new File(jakerApp.getRootPath(), edition.getNumber() + "/book.json")));
				} catch (Exception e) {
					//TODO
				}
				return book;
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values.length == 2) {
				progressDialog.setMax(values[0]);
				progressDialog.setProgress(values[1]);
			}
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		
		@Override
		protected void onPostExecute(Book book) {
			if (progressDialog.isShowing()) progressDialog.dismiss();
			jakerApp.getBooks().add(book);
			proceed();
		}		
	}
	/***********************************************************************************************************************************************/
}