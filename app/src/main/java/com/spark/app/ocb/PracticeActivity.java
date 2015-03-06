package com.spark.app.ocb;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PracticeActivity extends Activity {
	
	private static final String TAG = "PracticeActivity";
	
	private Dao<Question, Integer> mQDao = null;
	private TextView txtTitle = null; 
	private RadioButton radioA, radioB, radioC = null;
	
	private Set<Integer> mQuestionIds = new HashSet<Integer>();
	
	private int mQuestoinId, mCurrentNo = 1;
	

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
	 * Click event handler for btnBefore
	 * @param view
	 */
	public void btnBeforeClick(View view){
		if (mCurrentNo<=1){
			SysUtils.toast("Begining of the questions.");
			return;
		}
		
		mCurrentNo--;
		loadData();
	}
	
	/**
	 * Click event handler for btnNext
	 * @param view
	 */
	public void btnNextClick(View view){
		if (mCurrentNo>=20){
			SysUtils.toast("End of the questions.");
			return;
		}
		
		mCurrentNo++;
		loadData();
	}

	private void setupView(){
		txtTitle = (TextView)findViewById(android.R.id.text1);
		radioA = (RadioButton)findViewById(R.id.radioa);
		radioB = (RadioButton)findViewById(R.id.radiob);
		radioC = (RadioButton)findViewById(R.id.radioc);
	}
	
	private void loadData() {
		if (mQDao==null)
			mQDao = BeanUtils.getQuestionDao(this);
		
		Question question = null;
		try {
			Random random = new Random();
			if (mQuestionIds.size()<20){
				do {
					mQuestoinId = generateQuestionId(1, 20, random);
				} while (mQuestionIds.contains(mQuestoinId));

				mQuestionIds.add(mQuestoinId);
			} else {
				mQuestoinId = generateQuestionId(1, 20, random);
			}
			
			Log.d(TAG, "Generated question Id:" + mQuestoinId);
			mQuestoinId = mCurrentNo;
			question = mQDao.queryForId(mQuestoinId);
		} catch (SQLException e) {
			e.printStackTrace();
			SysUtils.toast("Load data error.");
		}
		
		if (question == null) return;
		
		txtTitle.setText(mCurrentNo + ". " + question.question);
		txtTitle.setTag(question);
		radioA.setText(question.a);
		radioB.setText(question.b);
		radioC.setText(question.c);
	}
	
	private int generateQuestionId(int aStart, int aEnd, Random aRandom){
	    if ( aStart > aEnd ) {
	      throw new IllegalArgumentException("Start cannot exceed End.");
	    }
	    //get the range, casting to long to avoid overflow problems
	    long range = (long)aEnd - (long)aStart + 1;
	    // compute a fraction of the range, 0 <= frac < range
	    long fraction = (long)(range * aRandom.nextDouble());
	    int randomNumber =  (int)(fraction + aStart);    
	    
	    return randomNumber;
	}

}
