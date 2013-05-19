package com.spark.app.citizen;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.dao.Dao;
import com.spark.app.citizen.entity.Question;
import com.spark.app.citizen.util.BeanUtils;
import com.spark.app.citizen.util.FileUtils;
import com.spark.app.citizen.util.SysUtils;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	
	private Dao<Question, Integer> mQDao = null;
	private Button btnPractice = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupView();
		loadData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	/**
	 * Click event handler for btnPrice
	 * @param view
	 */
	public void btnPracticeClick(View view){
		Intent i = new Intent(this, PracticeActivity.class);
		startActivity(i);
	}

	private void setupView(){
		btnPractice = (Button)findViewById(R.id.btnPractice);
	}
	
	private void loadData() {
		if (mQDao==null)
			mQDao = BeanUtils.getQuestionDao(this);
		
		
		BufferedInputStream in = null;
		try {
			mQDao.queryRaw("delete from questions");

			List<Question> questions = mQDao.queryForAll();
			if (!questions.isEmpty()) return;
			
			AssetManager asm = this.getAssets();	
			in = new BufferedInputStream(asm.open("qbank.json"));
			String jsonStr = FileUtils.readTextFile(in);
			//Log.d(TAG, json);
			
			
			questions = Question.loadFromJson(jsonStr);
			for (Question question : questions){	
				mQDao.create(question);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			SysUtils.alert(this, "Load Data Error(DB)", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			SysUtils.alert(this, "Load Data Error(IO)", e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
			SysUtils.alert(this, "Load Data Error(JSON)", e.getMessage());
		} finally {
			if (in!=null) try {	in.close();	} catch (Exception e) {}
		}
		
	}
	

}
