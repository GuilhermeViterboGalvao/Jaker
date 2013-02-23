package com.br.jaker.view;

import com.br.jaker.model.Book;
import android.app.Application;

public class JakerApp extends Application {

	private Book book;
	
	public Book getBook() {
		return book;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		
		
	}	
}