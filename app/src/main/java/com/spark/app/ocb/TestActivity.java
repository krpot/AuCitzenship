package com.spark.app.ocb;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.adpter.QuestionAdapter;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.List;

public class TestActivity extends ListActivity {
	
	private Dao<Question, Integer> mQDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);
		
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
		
	}

	private void setupView(){
		
	}
	
	private void loadData() {
		if (mQDao==null)
			mQDao = BeanUtils.getQuestionDao(this);
		
		List<Question> questions = null;
		try {
			questions = mQDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			SysUtils.toast("Load data error.");
		}
		
		if (questions == null) return;
			
		QuestionAdapter adapter = new QuestionAdapter(this, questions); 
		this.getListView().setAdapter(adapter);
	}

}
