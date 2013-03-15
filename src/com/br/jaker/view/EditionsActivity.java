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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

//TODO override onRestoreInstanceState and onSaveInstanceState
//Ler o xml das edições, salvá-lo no celular, caso haja um nova edição baixá - la e depois disso
//alterar o arquivo no celular, notificado que aquela edição já foi baixada.

//Sempre que o app for ligado, verificar por atualizações, caso o usuário não tiver conexão com a 
//internet ignorar e seguir como base o xml salvo no celular.

//Caso alguma edição seja perdida ignorar, caso o usuário deseje ver aquela edição notificar a perda e
//perguntar se ele quer baixa-la novamente.
/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class EditionsActivity extends Activity implements OnClickListener {

	private JakerApp jakerApp;
	
	private LinearLayout layoutMain;
	
	private AlertDialog.Builder alertDialog;
	
	private List<AsyncEditionZipDownloader> editionsDownloader;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editions);
		
		layoutMain = (LinearLayout)findViewById(R.editions.layoutMain);
				
		editionsDownloader = new ArrayList<AsyncEditionZipDownloader>();
		
		jakerApp = (JakerApp)getApplication();
		
		for (Edition edition : jakerApp.getEditions()) {
			Button btn = new Button(this);			
			btn.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			btn.setOnClickListener(this);
			if (edition.isNewEdition() && !checkIfExists(edition)) {
				btn.setText(edition.getTitle() + " Nº " + edition.getNumber() + " - New Edition");
				btn.setTag(edition);
			} else {
				btn.setText(edition.getTitle() + " Nº " + edition.getNumber());
				btn.setTag(null);
			}
			layoutMain.addView(btn);			
		}
	}
	
	@Override
	public void finish() {
		if (editionsDownloader != null) {
			for (AsyncEditionZipDownloader bookDownloader : editionsDownloader) {
				if (!bookDownloader.isCancelled()) {
					bookDownloader.cancel(true);
				}
			}
		}
		super.finish();
	}
	
	@Override
	public void onClick(View v) {
		if (v instanceof Button) {
			if (v.getTag() != null && v.getTag() instanceof Edition) {
				Edition edition = (Edition)v.getTag();
				ProgressDialog progressDialog = new ProgressDialog(this);
				progressDialog.setTitle(jakerApp.getAppName());
				progressDialog.setMessage("Downloading edition number " + edition.getNumber() + ".");
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				AsyncEditionZipDownloader editionDownloader = new AsyncEditionZipDownloader(progressDialog, (Button)v, true);
				editionDownloader.execute(edition);
				editionsDownloader.add(editionDownloader);
			} else if (v.getTag() != null && v.getTag() instanceof Book) {
				Intent intent = new Intent(this, HomeActivity.class);
				intent.putExtra("book", (Book)v.getTag());
				startActivity(intent);								
			}
		}
	}
	
	private boolean checkIfExists(Edition edition) {
		boolean exists = false;		
		File editionPath = new File(jakerApp.getRootPath(), Integer.toString(edition.getNumber()));
		if (editionPath.exists() && editionPath.isDirectory()) {
			File bookFile = new File(editionPath, "book.json");
			if (bookFile.exists() && bookFile.isFile()) {				
				try {
					Book book = JSONBookParser.parseBook(new FileInputStream(bookFile));
					book.setEdition(edition);
					edition.setBook(book);
					exists = true;
				} catch (FileNotFoundException e) {
					alertDialog = new AlertDialog.Builder(this);
					alertDialog.setMessage("The file " + bookFile.getAbsolutePath() + " was not found.");
				} catch (BookExpection e) {
					alertDialog = new AlertDialog.Builder(this);
					alertDialog.setMessage("The file " + bookFile.getAbsolutePath() + " was is bad format.");
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
		return exists;
	}
	
	public class AsyncEditionZipDownloader extends AsyncTask<Edition, Integer, Book> {

		public AsyncEditionZipDownloader(ProgressDialog progressDialog, Button target, boolean cancelable) {
			this.progressDialog = progressDialog;
			this.cancelable = cancelable;
			this.target = target;
		}
		
		private AlertDialog.Builder alertDialog;
		
		private ProgressDialog progressDialog;		
		
		private boolean cancelable;
		
		private Edition edition;
		
		private String message;
		
		private Button target;
		
		private File zip;
		
		@Override
		protected void onPreExecute() {
			if (progressDialog != null) {
				if (cancelable) {
					progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {					
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (AsyncEditionZipDownloader.this.isCancelled()) AsyncEditionZipDownloader.this.cancel(true);
						}
					});	
				} 
				progressDialog.show();
			}
		}
		
		@Override
		protected Book doInBackground(Edition... editions) {
			edition = editions[0];
			
			if (edition != null && target != null && progressDialog != null) {	
				HttpGet requestGet = new HttpGet(edition.getDownloadUrl());
				requestGet.setHeader("Accept", "text/plain");
				
				HttpResponse response = null;
				
				try {
					response = Utils.getHTTPClient().execute(requestGet);
				} catch (ClientProtocolException cpe) {
					message = Messages.problemConnectingToTheServer;
					Log.i("EditionsActivity.AsyncEditionZipDownloader.doInBackground", "Execution stoped: " + cpe.getMessage());
					return null;
				} catch (IOException ioe) {
					message = Messages.problemConnectingToTheServer;
					Log.i("EditionsActivity.AsyncEditionZipDownloader.doInBackground", "Execution stoped: " + ioe.getMessage());
					return null;
				} catch (Exception e) {
					message = Messages.problemConnectingToTheServer;
					Log.i("EditionsActivity.AsyncEditionZipDownloader.doInBackground", "Execution stoped: " + e.getMessage());
					return null;
				}
				
				int statusCode = response.getStatusLine().getStatusCode();
				
				if (statusCode != HttpStatus.SC_OK) {
					try {
						response.getEntity().getContent().close();
					} catch (IOException ioe) {						
						Log.i("EditionsActivity.AsyncEditionZipDownloader.doInBackground", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
					} catch (Exception e) {
						Log.i("EditionsActivity.AsyncEditionZipDownloader.doInBackground", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
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
		                publishProgress(100, (int)(totalRead * 100 / fileSize));
		            }
		            
				} catch (IllegalStateException ise) {
					if (zip != null && zip.exists())zip.delete();
					message = Messages.problemToDownloadTheFile;
					Log.e("EditionsActivity.AsyncEditionZipDownloader.doInBackground", ise.getMessage(), ise);
				} catch (IOException ioe) {
					if (zip != null && zip.exists())zip.delete();
					message = Messages.problemToDownloadTheFile;
					Log.e("EditionsActivity.AsyncEditionZipDownloader.doInBackground", ioe.getMessage(), ioe);
				} catch (Exception e) {
					if (zip != null && zip.exists())zip.delete();
					message = Messages.problemToDownloadTheFile;
					Log.e("EditionsActivity.AsyncEditionZipDownloader.doInBackground", e.getMessage(), e);					
				} finally {
					if(in  != null) try { in.close();  } catch (Exception e) {}
					if(out != null) try { out.close(); } catch (Exception e) {}
				}
				
				if (zip != null && zip.exists() && zip.length() == fileSize) {
					try {
						Utils.unzip(zip, jakerApp.getRootPath());						
					} catch (Exception e) {
						message = Messages.problemOnUnzipFile;
						Log.e("EditionsActivity.AsyncEditionZipDownloader.doInBackground", e.getMessage(), e);
					}
					
					zip.delete();
					Book book = null;
					
					try {
						book = JSONBookParser.parseBook(new FileInputStream(new File(jakerApp.getRootPath(), edition.getNumber() + "/book.json")));
						book.setEdition(edition);
						edition.setBook(book);
					} catch (Exception e) {
						message = Messages.problemOnReadJsonBookFile;
						Log.e("EditionsActivity.AsyncEditionZipDownloader.doInBackground", e.getMessage(), e);
					}
					
					return book;
				} else {
					if (!jakerApp.isConnected()) message = Messages.internetConnectionProblem;
				}
			} else {
				if (edition == null) 		     message = Messages.editionIsNull;
				else if (progressDialog == null) message = Messages.progressBarIsNull;
				else if (target == null) 	     message = Messages.targetIsNull;
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
			if (isCancelled() && zip != null && zip.exists()) zip.delete();
			if (progressDialog.isShowing()) progressDialog.dismiss();
		}
		
		@Override
		protected void onPostExecute(Book book) {
			if (progressDialog.isShowing()) progressDialog.dismiss();
			if (book != null) {				
				target.setText(edition.getTitle() + " Nº " + edition.getNumber());
				target.setTag(book);
			} else {
				alertDialog = new AlertDialog.Builder(EditionsActivity.this);
				alertDialog.setTitle(jakerApp.getAppName());
				alertDialog.setMessage(message);
				alertDialog.create();
				alertDialog.show();
			}
		}
	}//AsyncEditionZipDownloader
}