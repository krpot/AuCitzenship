package com.spark.app.ocb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.AppConstants;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.Updates;
import com.spark.app.ocb.task.DownloadTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.PrefUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SplashActivity extends Activity {
	
	private static final String TAG = "SplashActivity";
    private static final int SPLASH_TIMEOUT = 2000;

    private ProgressBar spinner;
    private TextView txtIntro;

    private Dao<Question, Integer> qDao;
    private Dao<Answer, Integer> aDao;

    //DownloadService mDownloadService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		setupView();

        checkIfThereAreUpdates();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    /*
     *
     */
    private void setupView(){
        spinner = (ProgressBar)findViewById(R.id.spinner);
        txtIntro = (TextView)findViewById(R.id.txtIntro);
	}

    /*
     *
     */
	private void checkIfThereAreUpdates() {

        spinner.setVisibility(View.VISIBLE);

        txtIntro.setText(R.string.check_update);

        //mDownloadService = new DownloadService(this, mDownloadReceiver);
        //mDownloadService.startDownload("http://www.janeart.net/citizen/questions/update.json", "ocbupdate.json", "OcbUpdate");

        DownloadTask task = new DownloadTask(new TaskListener<Map<String, String>>() {
            @Override
            public void onError(Throwable th) {
                txtIntro.setText(R.string.error_download);
                startMainActivity();
            }

            @Override
            public void onComplete(Map<String, String> result) {
                Log.d(TAG, "========== Update list onComplete: " + result);

                if (result != null && !result.isEmpty()) {

                    try {
                        String s = result.get("update");
                        Log.d(TAG, "######: " + s);
                        String[] urls = updatesList(s);
                        spinner.setVisibility(View.GONE);

                        if (urls.length == 0){
                            Log.d(TAG, "######: No update list!!! ");
                            txtIntro.setText(R.string.no_update);
                            startMainActivity();
                        } else {
                            updateQuestionsFromServer(urls);
                        }

                    } catch (RuntimeException e) {
                        spinner.setVisibility(View.GONE);
                        txtIntro.setText(R.string.error_apply);
                        startMainActivity();
                    }
                }
            }
        });
        task.execute(AppConstants.QUESTION_UPDATE_URL);
	}

    private String[] updatesList(String json){

        Log.d(TAG, "========== updatesList: " + json);
        List<String> urls = new ArrayList<String>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jo = jsonArray.getJSONObject(i);
                Updates u = Updates.toObject(jo);

                String version = PrefUtils.readString(u.fileName);
                Log.d(TAG, "#### Current version:" + version);
                if (version.compareTo(u.version)<0) {
                    String url = AppConstants.QUESTION_URL + u.fileName + ".json";
                    urls.add(url);
                    PrefUtils.writeString(u.fileName + "_temp", u.version);
                }
            }

            return urls.toArray(new String[urls.size()]);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void updateQuestionsFromServer(final String... urls) {
        Log.d(TAG, "========== updateQuestionsFromServer: " + urls);

        txtIntro.setText(R.string.download_update);

        spinner.setVisibility(View.VISIBLE);

        DownloadTask task = new DownloadTask(new TaskListener<Map<String, String>>() {

            @Override
            public void onError(Throwable th) {
                txtIntro.setText(R.string.error_download);
                startMainActivity();
                //SysUtils.toast("Error while downloading.\n" + th.getMessage());
            }

            @Override
            public void onComplete(Map<String, String> result) {
                Log.d(TAG, "========== Question onComplete: " + result);

                txtIntro.setText(R.string.download_completed);

                if (result != null && !result.isEmpty()) {

                    try {

                        if (qDao == null) qDao = BeanUtils.getQuestionDao(SplashActivity.this);
                        if (aDao == null) aDao = BeanUtils.getAnswerDao(SplashActivity.this);

                        aDao.deleteBuilder().clear();
                        qDao.deleteBuilder().clear();

                        for (Map.Entry<String, String> entry: result.entrySet()) {
                            saveQuestion(entry.getValue());
                            String version = PrefUtils.readString(entry.getKey() + "_temp");
                            PrefUtils.writeString(entry.getKey(), version);

                            Log.d(TAG, "#### New version:" + PrefUtils.readString(entry.getKey()));
                        }

                    } catch (RuntimeException e) {
                        txtIntro.setText(R.string.error_apply);
                        //SysUtils.toast("Error while parsing data.\n" + e.getMessage());
                    }
                }

                spinner.setVisibility(View.GONE);
                startMainActivity();
            }
        });
        task.execute(urls);
    }

    public void saveQuestion(String json) {
        Log.d(TAG, "========== saveQuestion: " + json);

        try {
            List<Question> questions = Question.loadFromJson(json);
            for (Question question : questions) {
                qDao.create(question);

                int answerNo = 0;
                for (Answer answer : question.answers) {
                    answer.id = answerNo++;
                    Log.d(TAG, "#####Answer:" + answer);
                    aDao.create(answer);
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /*
     *
     */
    private void startMainActivity(){
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

}
