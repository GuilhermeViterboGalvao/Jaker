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
import com.br.jaker.exception.BookExpection;
import com.br.jaker.model.Book;
import com.br.jaker.model.Edition;
import com.br.jaker.util.Messages;
import com.br.jaker.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditionsListActivity extends Activity implements OnClickListener {

	private JakerApp jakerApp;
	
	private LinearLayout layoutMain;
	
	private AlertDialog.Builder alertDialog;
	
	private List<AsyncBookDownloader> booksDownloader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		layoutMain = (LinearLayout)findViewById(R.editionsList.layoutMain);
				
		booksDownloader = new ArrayList<AsyncBookDownloader>();
		
		jakerApp = (JakerApp)getApplication();
		
		for (Edition edition : jakerApp.getEditions()) {
			RelativeLayout wrapper = new RelativeLayout(this);
			wrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			TextView txt = new TextView(this);			
			txt.setOnClickListener(this);
			if (edition.isNewEdition()) {
				txt.setText(edition.getTitle() + " Nº " + edition.getNumber() + " - New Edition");
				txt.setTag(edition);
			} else {
				txt.setText(edition.getTitle() + " Nº " + edition.getNumber());
				txt.setTag(null);
			}
			wrapper.addView(txt);
			layoutMain.addView(wrapper);
			checkEditionFolder(edition);
		}
		
		setContentView(layoutMain);
	}
	
	@Override
	public void finish() {
		if (booksDownloader != null) {
			for (AsyncBookDownloader bookDownloader : booksDownloader) {
				if (!bookDownloader.isCancelled()) {
					bookDownloader.cancel(true);
				}
			}
		}
		super.finish();
	}
	
	@Override
	public void onClick(View v) {		
		if (v.getTag() != null && v.getTag() instanceof Edition) {
			ProgressBar progressBar = new ProgressBar(this);
			((RelativeLayout)v.getParent()).addView(progressBar);
			AsyncBookDownloader bookDownloader = new AsyncBookDownloader(progressBar, v);
			bookDownloader.execute((Edition)v.getTag());
			booksDownloader.add(bookDownloader);
		}
	}
	
	private void checkEditionFolder(Edition edition) {
		File editionPath = new File(jakerApp.getRootPath(), Integer.toString(edition.getNumber()));
		if (editionPath.exists() && editionPath.isDirectory()) {
			File book = new File(editionPath, "book.json");
			if (book.exists() && book.isFile()) {
				try {
					jakerApp.getBooks().add(JSONBookParser.parseBook(new FileInputStream(book)));
				} catch (FileNotFoundException e) {
					alertDialog = new AlertDialog.Builder(this);
					alertDialog.setMessage("The file " + book.getAbsolutePath() + " was not found.");
				} catch (BookExpection e) {
					alertDialog = new AlertDialog.Builder(this);
					alertDialog.setMessage("The file " + book.getAbsolutePath() + " was is bad format.");
				} catch (Exception e) {
					alertDialog = new AlertDialog.Builder(this);
					alertDialog.setMessage("The " + jakerApp.getAppName() + " stopped because: " + e.getMessage());
				} finally {
					if (alertDialog != null) {
						alertDialog.setTitle(jakerApp.getAppName());
						alertDialog.create();
						alertDialog.show();
					}
				}
			}
		}
	}
	
	public class AsyncBookDownloader extends AsyncTask<Edition, Integer, Book> {

		public AsyncBookDownloader(ProgressBar progressBar, View target) {
			this.progressBar = progressBar;
			this.target = target;
		}
		
		private AlertDialog.Builder alertDialog;
		
		private ProgressBar progressBar;		
		
		private String message;
		
		private View target;
		
		private File zip;
		
		@Override
		protected Book doInBackground(Edition... editions) {
			Edition edition = editions[0];
			
			if (edition != null && target != null && progressBar != null) {	
				HttpGet requestGet = new HttpGet(edition.getDownloadUrl());
				requestGet.setHeader("Accept", "text/plain");
				
				HttpResponse response = null;
				
				try {
					response = Utils.getHTTPClient().execute(requestGet);
				} catch (ClientProtocolException cpe) {
					message = Messages.problemConnectingToTheServer;
					Log.i("EditionsListActivity.AsyncBookDownloader.doInBackground", "Execution stoped: " + cpe.getMessage());
					return null;
				} catch (IOException ioe) {
					message = Messages.problemConnectingToTheServer;
					Log.i("EditionsListActivity.AsyncBookDownloader.doInBackground", "Execution stoped: " + ioe.getMessage());
					return null;
				} catch (Exception e) {
					message = Messages.problemConnectingToTheServer;
					Log.i("EditionsListActivity.AsyncBookDownloader.doInBackground", "Execution stoped: " + e.getMessage());
					return null;
				}
				
				int statusCode = response.getStatusLine().getStatusCode();
				
				if (statusCode != HttpStatus.SC_OK) {
					try {
						response.getEntity().getContent().close();
					} catch (IOException ioe) {						
						Log.i("EditionsListActivity.AsyncBookDownloader.doInBackground", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
					} catch (Exception e) {
						Log.i("EditionsListActivity.AsyncBookDownloader.doInBackground", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
					}
					message = Messages.serverReturnStats + Integer.toString(statusCode);
					return null;
				}
				
				long fileSize  = response.getEntity().getContentLength();
		        long totalRead = 0;        
		        int read = 0;
		        byte buffer[] = new byte[1024];
		        InputStream in = null;
		        OutputStream out = null;
		        zip = new File(jakerApp.getRootPath(), edition.getNumber() + ".zip");
		        
				try {
					
		            in = new BufferedInputStream(response.getEntity().getContent());
		            out = new FileOutputStream(zip);
		            
		            while ( ((read = in.read(buffer)) != -1) && !isCancelled() && jakerApp.isConnected()) {
		            	totalRead += read;                
		                out.write(buffer, 0, read);
		                publishProgress((int)fileSize, (int)(totalRead * 100 / fileSize));
		            }
		            
				} catch (IllegalStateException ise) {
					if (zip != null && zip.exists())zip.delete();
					message = Messages.problemToDownloadTheFile;
					Log.e("EditionsListActivity.AsyncBookDownloader.doInBackground", ise.getMessage(), ise);
				} catch (IOException ioe) {
					if (zip != null && zip.exists())zip.delete();
					message = Messages.problemToDownloadTheFile;
					Log.e("EditionsListActivity.AsyncBookDownloader.doInBackground", ioe.getMessage(), ioe);
				} catch (Exception e) {
					if (zip != null && zip.exists())zip.delete();
					message = Messages.problemToDownloadTheFile;
					Log.e("EditionsListActivity.AsyncBookDownloader.doInBackground", e.getMessage(), e);					
				} finally {
					if(in  != null) try { in.close();  } catch (Exception e) {}
					if(out != null) try { out.close(); } catch (Exception e) {}
				}
				
				if (zip != null && zip.exists() && zip.length() == fileSize) {
					try {
						Utils.unzip(zip, jakerApp.getRootPath());						
					} catch (Exception e) {
						message = Messages.problemOnUnzipFile;
						Log.e("EditionsListActivity.AsyncBookDownloader.doInBackground", e.getMessage(), e);
					}
					
					zip.delete();
					Book book = null;
					
					try {
						book = JSONBookParser.parseBook(new FileInputStream(new File(jakerApp.getRootPath(), edition.getNumber() + "/book.json")));
					} catch (Exception e) {
						message = Messages.problemOnReadJsonBookFile;
						Log.e("EditionsListActivity.AsyncBookDownloader.doInBackground", e.getMessage(), e);
					}
					
					return book;
				} else {
					if (isCancelled() && zip != null && zip.exists()) zip.delete();
					if (!jakerApp.isConnected()) message = Messages.internetConnectionProblem;
				}
			} else {
				if (edition == null) 		  message = Messages.editionIsNull;
				else if (progressBar == null) message = Messages.progressBarIsNull;
				else if (target == null) 	  message = Messages.targetIsNull;
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (values.length == 2) {
				progressBar.setMax(values[0]);
				progressBar.setProgress(values[1]);
			}
		}
		
		@Override
		protected void onCancelled() {
			if (progressBar.isShown()) progressBar.setVisibility(0);
		}
		
		@Override
		protected void onPostExecute(Book book) {
			if (progressBar.isShown()) progressBar.setVisibility(0);
			if (book != null) {
				jakerApp.getBooks().add(book);
				target.setOnClickListener(null);
				//TODO remover o texto NEW
			} else {
				alertDialog = new AlertDialog.Builder(EditionsListActivity.this);
				alertDialog.setTitle(jakerApp.getAppName());
				alertDialog.setMessage(message);
				alertDialog.create();
				alertDialog.show();
			}
		}
	}//AsyncBookDownloader
}