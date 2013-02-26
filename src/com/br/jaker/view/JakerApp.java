package com.br.jaker.view;

import java.io.IOException;
import java.util.List;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import com.br.jaker.control.AppManager;
import com.br.jaker.exception.EditionException;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Book;
import android.app.Application;
import android.os.Environment;
import android.util.Log;

public class JakerApp extends Application {

	private static final HttpRequestRetryHandler retryHandler;
	
	private static final AbstractHttpClient httpClient;
	
	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));		
		HttpParams clientParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(clientParams, 10 * 1000);
		HttpConnectionParams.setSoTimeout(clientParams, 10 * 1000);
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(clientParams, schemeRegistry);	
		httpClient = new DefaultHttpClient(cm, null);
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Jaker App 1.0");
		retryHandler = new DefaultHttpRequestRetryHandler(5, false) {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				Log.i("http-exception", exception.getClass().getSimpleName() + " : " + exception.getMessage());
				
				boolean result = super.retryRequest(exception, executionCount, context);
				
				if (result == false) return false;
				
				if (exception instanceof NoHttpResponseException) return true;
				
				try { Thread.sleep(2000); } catch (InterruptedException e) { }
				
				return true;
			}
		};
		httpClient.setHttpRequestRetryHandler(retryHandler);
	}
	
	private AppManager appManager;
	
	public AppManager getAppManager() {
		return appManager;
	}
	
	private List<Book> book;
	
	public List<Book> getBook() {
		return book;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			appManager = new AppManager(httpClient, getAssets(), Environment.getExternalStorageDirectory());
		} catch (EnterpriseException e) {
			
		} catch (EditionException e) {
			
		} catch (IOException e) {

		} catch (Exception e) {

		}
	}
}