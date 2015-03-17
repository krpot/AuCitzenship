package com.spark.app.ocb.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

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

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupView();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_main, menu);
//		return true;
//	}
	
	/**
	 * Click event handler for buttons
	 * @param view
	 */
	public void onButtonClick(View view){
        switch (view.getId()){
            case R.id.btnPractice:
                Intent intent1 = new Intent(this, PracticeActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnTest:
                Intent intent2 = new Intent(this, TestIntroActivity.class);
                startActivityForResult(intent2, AppConstants.REQUEST_MODAL);
                break;
            case R.id.btnStudy:
                Intent intent3 = new Intent(this, TextbookActivity.class);
                startActivity(intent3);
                break;
            case R.id.btnInfo:
                Intent intent4 = new Intent(Intent.ACTION_VIEW);
                intent4.setData(Uri.parse("http://www.citizenship.gov.au/applying/how_to_apply"));
                startActivity(intent4);
                break;
            default:
                SysUtils.toast(R.string.not_supported_yet);
                break;
        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AppConstants.REQUEST_MODAL != requestCode) return;

        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(this, TestActivity.class);
            startActivityForResult(intent, AppConstants.REQUEST_MODAL);
        }
    }

    private void setupView(){
		//btnPractice = (Button)findViewById(R.id.btnPractice);
	}

}
