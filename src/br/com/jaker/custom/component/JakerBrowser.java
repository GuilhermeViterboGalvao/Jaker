package br.com.jaker.custom.component;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @author guilherme
 * @email catanduva.gvg@gmail.com
 * */
public class JakerBrowser extends WebViewClient {
	
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }	
}