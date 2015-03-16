package com.spark.app.ocb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.AppConstants;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.task.QuestionTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.PrefUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.List;

public class TextbookActivity extends Activity {
	
	private static final String TAG = "TextbookActivity";

    WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_textbook);
		
		setupView();
        loadWebPage();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Click event handler for buttons
	 * @param view
	 */
	public void onButtonClick(View view){
        switch (view.getId()){
            case R.id.btnPractice:
                break;
            case R.id.btnTest:
                break;
        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AppConstants.REQUEST_MODAL != requestCode) return;

        if (resultCode == Activity.RESULT_OK) {
        }
    }

    private void setupView(){
        setTitle(R.string.textbook);
        mWebView = (WebView)findViewById(R.id.webview);
	}

    /*
     *
     */
	private void loadWebPage() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        mWebView.loadUrl("file:///android_asset/index.html");
	}
	

}
