package com.br.jaker.view;

 import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.br.jaker.model.Edition;
import com.br.jaker.model.Enterprise;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class JakerApp extends Application {
	
	private String appName;
	
	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getAppName() {
		return appName;
	}
	
	private Enterprise enterprise;
	
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}
	
	private List<Edition> editions;
	
	public void setEditions(List<Edition> editions) {
		this.editions = editions;
	}
	
	public List<Edition> getEditions() {
		return editions == null ? new ArrayList<Edition>() : editions;
	}
	
	private File rootPath;
	
	public void setRootPath(File rootPath) {
		this.rootPath = rootPath;		
	}
	
	public File getRootPath() {
		return rootPath;
	}
	
	private File editionsXML;
	
	public File getEditionsXML() {
		return editionsXML;
	}
	
	public void setEditionsXML(File editionsXML) {
		this.editionsXML = editionsXML;
	}
	
	private ConnectivityManager connectivityManager;
	
	@Override
	public void onCreate() {
		super.onCreate();
		connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public synchronized boolean isConnected(){
		NetworkInfo infoWIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (infoWIFI != null && infoWIFI.isConnected()) return true;
		
		NetworkInfo infoMOBILE = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		if (infoMOBILE != null && infoMOBILE.isConnected()) return true;
		
		return false;
	}
}