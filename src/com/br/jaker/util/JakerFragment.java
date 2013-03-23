package com.br.jaker.util;

import com.br.jaker.view.JakerBrowser;
import com.br.jaker.view.R;
import com.br.jaker.view.R.fragmentWebView;
import com.br.jaker.view.R.layout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
@SuppressLint("SetJavaScriptEnabled")
public class JakerFragment extends Fragment {
	
	private WebView webView;
	
	private String url;
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentWebView = (LinearLayout)inflater.inflate(R.layout.fragment_webview, container, false);
		webView = (WebView)fragmentWebView.findViewById(R.fragmentWebView.webView);
		webView.setWebViewClient(new JakerBrowser());
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setPluginsEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);	
		return fragmentWebView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (webView != null) webView.loadUrl(url);
	}
}