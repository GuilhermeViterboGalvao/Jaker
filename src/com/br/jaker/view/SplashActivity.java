package com.br.jaker.view;
 
import java.io.File;
import java.io.IOException;
import java.util.List;
import com.br.jaker.control.SAXEditionsParser;
import com.br.jaker.control.SAXEnterpriseParser;
import com.br.jaker.exception.EditionException;
import com.br.jaker.exception.EnterpriseException;
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
				startActivity(new Intent(SplashActivity.this, EditionsActivity.class));
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
}