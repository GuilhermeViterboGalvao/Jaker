package com.br.jaker.view;

import java.util.List;
import com.br.jaker.view.R;
import com.br.jaker.component.HorizontalListView;
import com.br.jaker.model.Book;
import com.br.jaker.model.Edition;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

@SuppressLint("SetJavaScriptEnabled")
public class HomeActivity extends Activity {
		
	private JakerApp jakerApp;
	
	private int width;
	
	private int height;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		LinearLayout homeLayout = (LinearLayout)findViewById(R.home.linearLayout);
		
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		
		jakerApp = (JakerApp)getApplication();
						
		Book book = jakerApp.getBooks().get(0);
		Edition edition = jakerApp.getEditions().get(0);
		
		List<String> contents = book.getContents();
		
		HorizontalListView horizontalListView = new HorizontalListView(this, contents.size(), width);
		homeLayout.addView(horizontalListView);
		
		LinearLayout wrapper = new LinearLayout(this);		
		wrapper.setLayoutParams(new LayoutParams(width, height));
		horizontalListView.addView(wrapper);
		
		for (String content : contents) {
			WebView webView = new WebView(this);
			webView.setWebViewClient(new WebViewClient());
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setLayoutParams(new LayoutParams(width, height));
			webView.loadUrl("file://" + jakerApp.getRootPath() + "/" + edition.getNumber() + "/" + content);
			wrapper.addView(webView);
		}
		
		setContentView(homeLayout);
	}
}