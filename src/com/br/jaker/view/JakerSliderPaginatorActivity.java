package com.br.jaker.view;

import com.br.jaker.model.Book;
import com.br.jaker.util.SlidePageAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

public class JakerSliderPaginatorActivity extends FragmentActivity {

	private ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slider_paginator);
		
		JakerApp jakerApp = (JakerApp)getApplication();
		
		Book book = (Book)getIntent().getExtras().get("book");
		
		viewPager = (ViewPager)findViewById(R.sliderPaginator.pager);
		viewPager.setAdapter(new SlidePageAdapter(getSupportFragmentManager(), book, jakerApp));
	}
	
	@Override
	public void onBackPressed() {
		if (viewPager.getCurrentItem() == 0) super.onBackPressed();	
		else viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
	}
}
