package com.br.jaker.view;

import java.io.IOException;
import java.util.List;

import com.br.jaker.control.AppManager;
import com.br.jaker.exception.EnterpriseException;
import com.br.jaker.model.Book;
import android.app.Application;

public class JakerApp extends Application {

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
			appManager = new AppManager(getAssets());
		} catch (EnterpriseException e) {

		} catch (IOException e) {

		}
	}
}