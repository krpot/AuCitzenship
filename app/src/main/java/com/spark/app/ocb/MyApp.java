package com.spark.app.ocb;

import android.app.Application;
import android.util.Log;

import com.spark.app.ocb.model.Exam;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.DialogUtils;
import com.spark.app.ocb.util.PrefUtils;
import com.spark.app.ocb.util.SysUtils;

public class MyApp extends Application {

	private static final String TAG = "MyApp";
	
	public static MyApp app;

    private Exam mExam;
	
    @Override
	public void onCreate() {
		super.onCreate();
		
		//This should be first
		MyApp.app = this;

        BeanUtils.context = this;
        DialogUtils.context = this;
        SysUtils.context = this;

        PrefUtils.getInstance(this);

        Log.i(TAG, "============ onCreate()");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		
		BeanUtils.closeDatabase();
		
		Log.i(TAG, "============ onTerminate()");
	}

    public Exam exam(){
        if (mExam == null)
            mExam = new Exam();
        return mExam;
    }
}
