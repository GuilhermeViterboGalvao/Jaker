package br.com.jaker.custom.component;


import br.com.jaker.view.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
@SuppressLint("SetJavaScriptEnabled")
public class JakerFragment extends Fragment {
	
	private JakerWebView webView;
	
	public JakerWebView getWebView() {
		return webView;
	}
	
	private String url;
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout fragmentWebView = (LinearLayout)inflater.inflate(R.layout.fragment_webview, container, false);
		webView = (JakerWebView)fragmentWebView.findViewById(R.fragmentWebView.webView);
		webView.setWebViewClient(new JakerBrowser());
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setPluginsEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setPluginsEnabled(true);
        if (savedInstanceState != null) {
        	url = savedInstanceState.getString("url");
        }
		return fragmentWebView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("url", url);
		super.onSaveInstanceState(outState);		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (webView != null) webView.loadUrl(url);
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (webView != null) webView.loadUrl(url);
	}
}