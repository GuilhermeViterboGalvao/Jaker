package com.br.jaker.util;

import java.util.ArrayList;
import java.util.List;

import com.br.jaker.model.Book;
import com.br.jaker.view.JakerApp;
import com.br.jaker.view.JakerFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SlidePageAdapter extends FragmentStatePagerAdapter {

	public SlidePageAdapter(FragmentManager fm, Book book, JakerApp jakerApp) {
		super(fm);
		this.book = book;
		contents  = new ArrayList<String>(book != null ? book.getContents() : new ArrayList<String>());
		fragments = new ArrayList<JakerFragment>(contents != null ? contents.size() : 0);
	}
	
	private Book book;
	
	private JakerApp app;
	
	private List<String> contents;
	
	private List<JakerFragment> fragments;
	
	@Override
	public Fragment getItem(int position) {
		return fragments.get(position) != null ? 
				   fragments.get(position) 
			   :
				   new JakerFragment("file://" + app.getRootPath() + "/" + book.getEdition().getNumber() + "/" + contents.get(position));
	}

	@Override
	public int getCount() {
		return fragments != null ? fragments.size() : 0;
	}

}
