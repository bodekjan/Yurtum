package com.bodekjan.uyweather.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.bodekjan.uyweather.R;
import com.bodekjan.uyweather.util.CommonHelper;
import com.mikepenz.iconics.view.IconicsImageView;

public class WebActivity extends MyBaseActivity {
    private WebView webView;
    private String webViewUrl;
    private TextView headerTitle;
    IconicsImageView arrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(android.R.color.holo_blue_light));
        }
        setContentView(R.layout.activity_web);
        uyFace = Typeface.createFromAsset(getAssets(), "fonts/ALKATIP.TTF");
        arrow=(IconicsImageView)findViewById(R.id.backarrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                WebActivity.this.finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            webViewUrl = bundle.getString("url");
        }
        headerTitle=(TextView) findViewById(R.id.webtitle);
        headerTitle.setText(bundle.getString("title"));
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.addJavascriptInterface(new JavaScriptObject(this), "myObj");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                CookieSyncManager.getInstance().sync();
            }
        });
        webView.loadUrl(webViewUrl);
    }
    @Override
    public void onResume() {
        CommonHelper.activities.add(this);
        super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    public class JavaScriptObject {
        Context mContxt;
        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }
        @JavascriptInterface
        public int funGetLang() {
            SharedPreferences pref=getSharedPreferences("settings", Context.MODE_PRIVATE);
            int language=pref.getInt("lang",0);
            return language;
        }
    }
}
