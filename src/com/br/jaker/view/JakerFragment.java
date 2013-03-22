package com.br.jaker.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

@SuppressLint({ "SetJavaScriptEnabled", "ValidFragment" })
public class JakerFragment extends Fragment {
	
	public JakerFragment(String url) {
		this.url = url;
	}
	
	private String url;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentWebView = (LinearLayout)inflater.inflate(R.layout.fragment_webview, container, false);
		WebView webView = (WebView)fragmentWebView.findViewById(R.fragmentWebView.webView);
		webView.setWebViewClient(new WebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(url);	
		return fragmentWebView;
	}	
}