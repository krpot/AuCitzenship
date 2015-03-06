package com.spark.app.ocb;

import com.spark.app.ocb.util.BeanUtils;

import android.app.Application;
import android.util.Log;

public class MyApp extends Application {

	private static final String TAG = "MyApp";
	
	public static MyApp app;
	
    @Override
	public void onCreate() {
		super.onCreate();
		
		//This should be first
		MyApp.app = this;
		
		Log.i(TAG, "============ onCreate()");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		BeanUtils.closeDatabase();
		
		Log.i(TAG, "============ onTerminate()");
	}
	
}
