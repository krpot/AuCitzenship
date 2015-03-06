package com.spark.app.ocb;

import android.app.Application;
import android.util.Log;

import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

public class MyApp extends Application {

	private static final String TAG = "MyApp";
	
	public static MyApp app;
	
    @Override
	public void onCreate() {
		super.onCreate();
		
		//This should be first
		MyApp.app = this;

        BeanUtils.context = this;
        SysUtils.context = this;

        Log.i(TAG, "============ onCreate()");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		BeanUtils.closeDatabase();
		
		Log.i(TAG, "============ onTerminate()");
	}
	
}
