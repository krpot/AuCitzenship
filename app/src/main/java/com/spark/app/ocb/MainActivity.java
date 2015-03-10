package com.spark.app.ocb;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.FileUtils;
import com.spark.app.ocb.util.SysUtils;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";
	private static final String QUESTION_PATH = "questions";

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

    /*
     *
     */
	private void loadData() {
		if (mQDao==null)
			mQDao = BeanUtils.getQuestionDao(this);

        Dao<Answer, Integer> answerDao = BeanUtils.getAnswerDao(this);
		
		
		BufferedInputStream in = null;
		try {
            answerDao.deleteBuilder().clear();
            mQDao.deleteBuilder().clear();

            long count = mQDao.countOf();
            Log.d(TAG, "#####question.size:" + count);
			//***if (count>20) return;

			AssetManager asm = this.getAssets();
            String[] list = asm.list("");
            for (int i=0, sz=list.length; i<sz; i++) {
                String s = list[i];
                Log.d(TAG, "#####JSON file name:" + s);

                if (s.endsWith(".json")) {
                    in = new BufferedInputStream(asm.open(s));
                    String jsonStr = FileUtils.readTextFile(in);
                    Log.d(TAG, jsonStr);

                    List<Question> questions = Question.loadFromJson(jsonStr);
                    for (Question question : questions) {
                        mQDao.create(question);

                        int answerNo = 0;
                        for (Answer answer : question.answers) {
                            answer.id = answerNo++;
                            Log.d(TAG, "#####Answer:" + answer);
                            answerDao.create(answer);
                        }
                    }
                }
            }
			
		} catch (SQLException e) {
			e.printStackTrace();
			SysUtils.alert("Load Data Error(DB)", e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			SysUtils.alert("Load Data Error(IO)", e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
			SysUtils.alert("Load Data Error(JSON)", e.getMessage());
		} finally {
			if (in!=null) try {	in.close();	} catch (Exception e) {}
		}
		
	}
	

}
