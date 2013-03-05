package com.br.jaker.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.br.jaker.control.JSONBookParser;
import com.br.jaker.control.SAXEditionsParser;
import com.br.jaker.control.SAXEnterpriseParser;
import com.br.jaker.exception.BookExpection;
import com.br.jaker.exception.EditionException;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Book;
import com.br.jaker.model.Edition;
import com.br.jaker.model.Enterprise;
import com.br.jaker.util.Messages;
import com.br.jaker.util.Utils;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class JakerApp extends Application {
	
	private String appName;
	
	public String getAppName() {
		return appName;
	}
	
	private ConnectivityManager connectivityManager;
	
	private AsyncEditionsDownloader editionsDownloader;
	
	private Enterprise enterprise;
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
	private List<Edition> editions;
	
	public List<Edition> getEditions() {
		return editions;
	}
	
	private List<Book> books;
	
	public List<Book> getBooks() {
		return books;
	}
	
	private File rootPath;
	
	public File getRootPath() {
		return rootPath;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();		

		connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		try {
			enterprise = SAXEnterpriseParser.parseEnterprise(getAssets().open("Enterprise.xml"));
		} catch (EnterpriseException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		
		if (enterprise != null) {
			appName = enterprise.getName() != null && !enterprise.getName().equals("") ? enterprise.getName() : "Jaker";
			this.rootPath = new File(rootPath, appName);
			if (!this.rootPath.exists()) this.rootPath.mkdirs();			
			processingEditions();
		}
	}
	
	private void processingEditions() {	
		editionsDownloader = new AsyncEditionsDownloader();
		editionsDownloader.execute(enterprise.getUrlJsonEdtions());		
	}
	
	private void processBooks(List<Edition> editions) {
		this.editions = editions;
		books = new ArrayList<Book>(editions.size());		
		File[] booksPaths = this.rootPath.listFiles();
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
		for (Edition edition : editions) {
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
		
	public synchronized boolean isConnected(){
		NetworkInfo infoWIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (infoWIFI != null && infoWIFI.isConnected()) return true;
		
		NetworkInfo infoMOBILE = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (infoMOBILE != null && infoMOBILE.isConnected()) return true;
		
		return false;
	}
	
	public class AsyncEditionsDownloader extends AsyncTask<String, Void, List<Edition>> {

		private AlertDialog.Builder alertDialog;
		
		private ProgressDialog progressDialog;		
		
		private String errorMessage;
		
		@Override
		protected void onPreExecute() {
			if (isConnected()) {
				progressDialog = new ProgressDialog(JakerApp.this);
				progressDialog.setTitle(appName);
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
				showAlertDialog();
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
					errorMessage = "The " + appName + " app stopped working because: " + e.getMessage();
					showAlertDialog();
				} finally {
					if (editions == null) {
						errorMessage = "The " + appName + " app stopped working!";
						showAlertDialog();
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
			if (editions != null) processBooks(editions);
		}
		
		private void showAlertDialog() {
			cancelProgressDialog();
			alertDialog = new AlertDialog.Builder(JakerApp.this);
			alertDialog.setTitle(appName);
			alertDialog.setMessage(errorMessage);
			alertDialog.create();
			alertDialog.show();
		}
		
		private void cancelProgressDialog() {
			if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
		}
	}
	
	public class AsyncBookDownloader extends AsyncTask<Edition, Void, Book> {

		@Override
		protected Book doInBackground(Edition... editions) {
			try {
				Utils.unzip(Utils.writeZipInputStream(Utils.doGet(editions[0].getDownloadUrl()), new File(rootPath, editions[0].getNumber() + ".zip")) , rootPath);
			} catch (IOException e) {
				//TODO
			}
			Book book = null;
			try {
				book = JSONBookParser.parseBook(new FileInputStream(new File(rootPath, editions[0].getNumber() + "/book.json")));
			} catch (Exception e) {
				//TODO
			}
			return book;
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
		
		@Override
		protected void onPostExecute(Book book) {
			books.add(book);
		}		
	}
}