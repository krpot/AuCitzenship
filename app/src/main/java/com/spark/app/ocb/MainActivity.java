package com.spark.app.ocb;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.activity.PracticeActivity;
import com.spark.app.ocb.activity.SplashActivity;
import com.spark.app.ocb.activity.TestActivity;
import com.spark.app.ocb.activity.TestIntroActivity;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.task.QuestionTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.FileUtils;
import com.spark.app.ocb.util.PrefUtils;
import com.spark.app.ocb.util.SysUtils;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MainActivity extends Activity {
	
	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupView();
		//***loadData();

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
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
                Intent intent1 = new Intent(this, PracticeActivity.class);
                startActivity(intent1);
                break;
            case R.id.btnTest:
                Intent intent2 = new Intent(this, TestIntroActivity.class);
                startActivityForResult(intent2, AppConstants.REQUEST_MODAL);
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

    /*
     *
     */
	private void loadData() {

        Dao<Question, Integer> qDao = BeanUtils.getQuestionDao(this);
        try {
            boolean questionUpdated = PrefUtils.readBool(AppConstants.KEY_QUESTION_UPDATED);
            if (questionUpdated) {
                long count = qDao.countOf();
                Log.d(TAG, "#####question.size:" + count);
                questionUpdated = count > 0;
            }

            if (!questionUpdated){
                QuestionTask questionTask = new QuestionTask(this, new TaskListener<List<Question>>(){
                    @Override
                    public void onError(Throwable th) {
                        SysUtils.toast("Error while loading questions.");
                    }

                    @Override
                    public void onComplete(List<Question> result) {
                        PrefUtils.writeBool(AppConstants.KEY_QUESTION_UPDATED, true);
                        Log.d(TAG, "========== QuestionTask onComplete ========");
                    }
                });
                questionTask.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /******
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
		******/
	}
	

}
