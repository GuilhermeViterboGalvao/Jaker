package br.com.jaker.custom.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

@SuppressLint("NewApi")
public class JakerWebView extends WebView {

	public JakerWebView(Context context, AttributeSet attrs, int defStyle, boolean privateBrowsing) {
		super(context, attrs, defStyle, privateBrowsing);
	}

	public JakerWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public JakerWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JakerWebView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    return super.onTouchEvent(event);
	}
}