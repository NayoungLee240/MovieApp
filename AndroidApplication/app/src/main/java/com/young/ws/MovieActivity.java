package com.young.ws;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MovieActivity extends AppCompatActivity {
    String link;
    WebView webView;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        final String movieName = intent.getExtras().getString(Contants.MOVIE_NAME);

        link = null;
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);



//        webView.setWebChromeClient(new WebChromeClient());//웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        webView.setWebViewClient(new ViewClient());

//        webView.loadUrl("http://m.movie.naver.com/movie/bi/mi/basic.nhn?code=183866");
//        webView.loadUrl("http://m.naver.com/");

        Thread thread = new Thread() {
            public void run() {
                ApiNaver api = new ApiNaver();
                link = api.main(movieName);
                link = link.substring(8);
                link = "http://m."+link;
                Log.d(Contants.LOG_TAG,link);

                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        cView(link);
                    }
                });
            }
        };
        thread.setDaemon(true);
        thread.start();

        Log.d(Contants.LOG_TAG,movieName);
    }
    public void cView(String getlink){
//        if(!getlink.equals(link)){
            webView.loadUrl(link);
//            link = getlink;
//        }
    }

    private class ViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            view.loadUrl(url);
            return true;
        }
    }



}