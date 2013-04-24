package br.com.jaker.custom.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import br.com.jaker.model.Book;
import br.com.jaker.view.JakerApp;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class SlidePageAdapter extends FragmentStatePagerAdapter implements Serializable {

	private static final long serialVersionUID = -4953818757800767840L;

	public SlidePageAdapter(Context context, FragmentManager fm, Book book, JakerApp jakerApp) {
		super(fm);		
		this.book     = book;
		this.context  = context;
		this.jakerApp = jakerApp;
		fragments     = new ArrayList<JakerFragment>(contents != null ? contents.size() : 0);
		contents      = new ArrayList<String>(book != null ? book.getContents() : new ArrayList<String>());
		init();
	}
	
	private Book book;
	
	private JakerApp jakerApp;
	
	private Context context;
	
	private List<String> contents;
	
	private List<JakerFragment> fragments;
	
	private void init() {
		JakerFragment jakerFragment = null;
		for (String content : contents) {
			jakerFragment = (JakerFragment)Fragment.instantiate(context, JakerFragment.class.getName());
			jakerFragment.setUrl("file://" + jakerApp.getRootPath() + "/" + book.getEdition().getNumber() + "/" + content);
			fragments.add(jakerFragment);
		}
	}
	
	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
}