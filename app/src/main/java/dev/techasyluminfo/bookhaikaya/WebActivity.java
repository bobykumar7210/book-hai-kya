package dev.techasyluminfo.bookhaikaya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import static dev.techasyluminfo.bookhaikaya.DetailActivity.DETAIL_URL;

public class WebActivity extends AppCompatActivity {

    WebView mWebView;
    ProgressBar loaingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().hide();
        mWebView = findViewById(R.id.webview);

        loaingSpinner = findViewById(R.id.loading_spinner_detail);
        loaingSpinner.setVisibility(View.VISIBLE);
        Intent detailIntent = getIntent();
        if (detailIntent.hasExtra(DETAIL_URL)) {
            openWebView(detailIntent);
        } else {
            loaingSpinner.setVisibility(View.INVISIBLE);
        }

    }

    private void openWebView(Intent detailIntent) {



        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String Url = detailIntent.getStringExtra(DETAIL_URL);

        mWebView.loadUrl(Url);

        mWebView.setVisibility(View.VISIBLE);
        loaingSpinner.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
        //adding add tag
    }

}