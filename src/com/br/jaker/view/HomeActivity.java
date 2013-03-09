package com.br.jaker.view;

import java.util.List;
import com.br.jaker.view.R;
import com.br.jaker.component.HorizontalListView;
import com.br.jaker.model.Book;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
@SuppressLint("SetJavaScriptEnabled")
public class HomeActivity extends Activity {
		
	private JakerApp jakerApp;
	
	private int width;
	
	private int height;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);		
		LinearLayout layoutMain = (LinearLayout)findViewById(R.home.layoutMain);
		
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		
		jakerApp = (JakerApp)getApplication();
						
		Book book = (Book)getIntent().getExtras().get("book");
		
		List<String> contents = book.getContents();
		
		HorizontalListView horizontalListView = new HorizontalListView(this, contents.size(), width);
		layoutMain.addView(horizontalListView);
		
		LinearLayout wrapper = new LinearLayout(this);		
		wrapper.setLayoutParams(new LayoutParams(width, height));
		horizontalListView.addView(wrapper);
		
		for (String content : contents) {
			WebView webView = new WebView(this);
			webView.setWebViewClient(new WebViewClient());
			webView.getSettings().setJavaScriptEnabled(true);
			webView.setLayoutParams(new LayoutParams(width, height));
			webView.loadUrl("file://" + jakerApp.getRootPath() + "/" + book.getEdition().getNumber() + "/" + content);
			wrapper.addView(webView);
		}
	}
}