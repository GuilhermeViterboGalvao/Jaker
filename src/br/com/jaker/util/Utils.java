package br.com.jaker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import android.util.Log;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public final class Utils {
	
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
	
	public static AbstractHttpClient getHTTPClient() {
		return httpClient;
	}
	
	/**
	 * Execute the get method.
	 * @param url - The url that should run the post method.
	 * @return The contents of the response
	 * */
	public static synchronized InputStream doGet(String url) {
		Log.i("Utils.doGet", "url: " + url);
		
		HttpGet requestGet = new HttpGet(url);
		requestGet.setHeader("Accept", "text/plain");
		
		HttpResponse response = null;
		
		try {
			response = httpClient.execute(requestGet);
		} catch (ClientProtocolException cpe) {
			Log.i("Utils.doGet", "Execution stoped: " + cpe.getMessage());
			return null;
		} catch (IOException ioe) {
			Log.i("Utils.doGet", "Execution stoped: " + ioe.getMessage());
			return null;
		} catch (Exception e) {
			Log.i("Utils.doGet", "Execution stoped: " + e.getMessage());
			return null;
		}
		
		int statusCode = response.getStatusLine().getStatusCode();
		
		if (statusCode != HttpStatus.SC_OK) {
			try {
				response.getEntity().getContent().close();
			} catch (IOException ioe) {
				Log.i("Utils.doGet", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
			} catch (Exception e) {
				Log.i("Utils.doGet", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
			}
			return null;
		}
		
		InputStream in = null;
		
		try {
			in = response.getEntity().getContent();
		} catch (IllegalStateException ise) {
			in = null;
			Log.e("Utils.doGet", "Error on read InputStream (response.getEntity().getContent()): " + ise.getMessage(), ise);
		} catch (IOException ioe) {
			in = null;
			Log.e("Utils.doGet", "Error on read InputStream (response.getEntity().getContent()): " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			in = null;
			Log.e("Utils.doGet", "Error on read InputStream (response.getEntity().getContent()): " + e.getMessage(), e);
		}
		
		return in;
	}
	
	/**
	 * Execute the post method with the parameters.
	 * @param url - The url that should run the post method.
	 * @param names - The names of the values.
	 * @param values - The values of the names.
	 * @return The contents of the response
	 * */
	public static synchronized InputStream doPost(String url, String[] names, String[] values) {
		Log.i("Utils.doPost", "url: " + url);
		
		HttpPost requestPost = new HttpPost(url);
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
				Log.e("Utils.doPost", "Error on 'requestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs))': " + usfee.getMessage(), usfee);
			} catch (Exception e) {
				Log.e("Utils.doPost", "Error on 'requestPost.setEntity(new UrlEncodedFormEntity(nameValuePairs))': " + e.getMessage(), e);
			}
	        
	        HttpResponse response = null;
	        
			try {
				response = httpClient.execute(requestPost);
			} catch (ClientProtocolException cpe) {
				Log.i("Utils.doPost", "Execution stoped: " + cpe.getMessage());
			} catch (IOException ioe) {
				Log.i("Utils.doPost", "Execution stoped: " + ioe.getMessage());
			} catch (Exception e) {
				Log.i("Utils.doPost", "Execution stoped: " + e.getMessage());
			}
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != HttpStatus.SC_OK) {
				try {
					response.getEntity().getContent().close();
				} catch (IOException ioe) {
					Log.i("Utils.doPost", "Error on close InputStream (response.getEntity().getContent().close()): " + ioe.getMessage());
				} catch (Exception e) {
					Log.i("Utils.doPost", "Error on close InputStream (response.getEntity().getContent().close()): " + e.getMessage());
				}
				return null;
			}
			
			try {
				in = response.getEntity().getContent();
			} catch (IllegalStateException ise) {
				in = null;
				Log.e("Utils.doPost", "Error on 'response.getEntity().getContent()': " + ise.getMessage(), ise);
			} catch (IOException ioe) {
				in = null;
				Log.e("Utils.doPost", "Error on 'response.getEntity().getContent()': " + ioe.getMessage(), ioe);
			} catch (Exception e) {
				in = null;
				Log.e("Utils.doPost", "Error on 'response.getEntity().getContent()': " + e.getMessage(), e);
			}
		}
		
		return in;
	}
	
	/**
	 * This method creates a zip file in the directory reported by the "dir" parameter.
	 * @param zip - The InputStream of zip file.
	 * @param dir - The directory where the zip file will be created.
	 * */
	public static File writeZip(InputStream zip, File dir) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(dir);
			int read = 0;
			byte[] bytes = new byte[1024];		 
			while ((read = zip.read(bytes)) != -1) out.write(bytes, 0, read);		
		} catch (IOException ioe) {
			Log.e("Utils.writeZip", "Problem o write zip file: " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			Log.e("Utils.writeZip", "Problem o write zip file: " + e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();	
				} catch (Exception e) {	}
			}
		}
		return dir;
	}
	
	/**
	 * This method creates a file in the directory reported by the "dir" parameter.
	 * @param file - The InputStream of file.
	 * @param dir - The directory where the file will be created.
	 * */
	public static File write(InputStream in, File dir) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(dir);
			int read = 0;
			byte[] bytes = new byte[1024];		 
			while ((read = in.read(bytes)) != -1) out.write(bytes, 0, read);		
		} catch (IOException ioe) {
			Log.e("Utils.write", "Problem o write file: " + ioe.getMessage(), ioe);
		} catch (Exception e) {
			Log.e("Utils.write", "Problem o write file: " + e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();	
				} catch (Exception e) {	}
			}
		}
		return dir;
	}	
	
	/**
	 * This method extracts the zip file in the directory informed by parameter "dir".
	 * @param zipFile - The zip file.
	 * @param dir - The directory where you extracted the zip file.
	 * @exception If the file is not a zip or there is any problem in extracting the file this exception is thrown.
	 * */
	public static void unzip(File zipFile, File dir) throws IOException {
		
		ZipFile zip = null;
		File arquivo = null;
		InputStream is = null;
		OutputStream os = null;
		byte[] buffer = new byte[1024];
		
		try {
			
			// create directory informed if there is no
			if (!dir.exists()) dir.mkdirs();
			if (!dir.exists() || !dir.isDirectory()) throw new IOException("The directory " + dir.getName() + " is not a valid directory");
			
			zip = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> entries = zip.entries();
			
			while (entries.hasMoreElements()) {
				ZipEntry entrada = (ZipEntry)entries.nextElement();
				arquivo = new File(dir, entrada.getName());
				
				// if nonexistent directory, creates the structure and jump to next entry
				if (entrada.isDirectory() && !arquivo.exists()) {
					arquivo.mkdirs();	
					continue;
				}
				
				// if the directory structure does not exist, creates
				if (!arquivo.getParentFile().exists()) arquivo.getParentFile().mkdirs();
				
				try {
					// reads the zip file and burn to disc
					is = zip.getInputStream(entrada);
					os = new FileOutputStream(arquivo);
					int bytesLidos = 0;
					
					if (is == null) throw new ZipException("Error reading the zip entry: " + entrada.getName());
					
					while ((bytesLidos = is.read(buffer)) > 0) {
						os.write(buffer, 0, bytesLidos);
					}
				} finally {
					if (is != null) try { is.close(); } catch (Exception ex) { }
					if (os != null) try { os.close(); } catch (Exception ex) { }
				}
			}
		} finally {
			if (zip != null) try { zip.close(); } catch (Exception e) { }
		}
	}
	
	public static void delete(File f) throws IOException {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				delete(c);
			}			      
		}
		if (!f.delete()) {
			throw new FileNotFoundException("Failed to delete file: " + f);  
		}		    
	}
}