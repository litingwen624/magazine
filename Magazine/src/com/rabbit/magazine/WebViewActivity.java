package com.rabbit.magazine;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		String url=getIntent().getExtras().getString("url");
		webview=(WebView) findViewById(R.id.webview);
		webview.setWebChromeClient(new WebChromeClient(){             
            @Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				return super.onJsAlert(view, url, message, result);
			}

			public void onProgressChanged(WebView view, int progress){               
                setTitle("Loading...");          
                setProgress(progress * 100);        
                if(progress == 100)               
                    setTitle(R.string.app_name);          
                }            
            }   
        
        );           
		webview.setWebViewClient(new WebViewClient(){
			 public boolean shouldOverrideUrlLoading(WebView view, String url) {  
                 view.loadUrl(url);  
                 return true;  
             }  
			 @Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		webview.loadUrl(url);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {  
        	webview.goBack();  
            return true;  
        }  
        return super.onKeyDown(keyCode, event);  
    }
}
