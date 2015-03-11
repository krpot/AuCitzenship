package com.spark.app.ocb.activity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.AppConstants;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.Updates;
import com.spark.app.ocb.service.DownloadInfo;
import com.spark.app.ocb.service.DownloadService;
import com.spark.app.ocb.task.DownloadTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.PrefUtils;
import com.spark.app.ocb.util.SysUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {
	
	private static final String TAG = "SplashActivity";

    private boolean listUpdated;
    private ProgressBar spinner;

    private Dao<Question, Integer> qDao;
    private Dao<Answer, Integer> aDao;

    DownloadService mDownloadService;

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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //if (mDownloadService.isRegistered())
            //unregisterReceiver(mDownloadReceiver);
    }

    private void setupView(){
        spinner = (ProgressBar)findViewById(R.id.spinner);
	}

    /*
     *
     */
	private void checkIfThereAreUpdates() {

        //boolean updated = PrefUtils.readBool(AppConstants.KEY_QUESTION_UPDATED);
        //if (updated) return;

        spinner.setVisibility(View.VISIBLE);

        //mDownloadService = new DownloadService(this, mDownloadReceiver);
        //mDownloadService.startDownload("http://www.janeart.net/citizen/questions/update.json", "ocbupdate.json", "OcbUpdate");

        DownloadTask task = new DownloadTask(new TaskListener<List<String>>() {
            @Override
            public void onError(Throwable th) {
                SysUtils.toast("Error while downloading.\n" + th.getMessage());
            }

            @Override
            public void onComplete(List<String> result) {
                Log.d(TAG, "========== Update list onComplete: " + result);

                if (result != null && !result.isEmpty()) {

                    try {
                        Log.d(TAG, "######: " + result.get(0));
                        String[] urls = updatesList(result.get(0));
                        spinner.setVisibility(View.GONE);

                        updateQuestionsFromServer(urls);

                    } catch (RuntimeException e) {
                        spinner.setVisibility(View.GONE);
                        SysUtils.toast("Error while parsing data.\n" + e.getMessage());
                    }
                }
            }
        });
        task.execute("http://www.janeart.net/citizen/questions/update.json");

        /*
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
        */
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
                if (version.compareTo(u.version)<0) {
                    String url = "http://www.janeart.net/citizen/questions/" + u.fileName + ".json";
                    urls.add(url);
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

        if (urls.length == 0) return;

        spinner.setVisibility(View.VISIBLE);

        DownloadTask task = new DownloadTask(new TaskListener<List<String>>() {

            @Override
            public void onError(Throwable th) {
                SysUtils.toast("Error while downloading.\n" + th.getMessage());
            }

            @Override
            public void onComplete(List<String> result) {
                Log.d(TAG, "========== Question onComplete: " + result);

                if (result != null && !result.isEmpty()) {

                    try {

                        if (qDao == null) qDao = BeanUtils.getQuestionDao(SplashActivity.this);
                        if (aDao == null) aDao = BeanUtils.getAnswerDao(SplashActivity.this);

                        aDao.deleteBuilder().clear();
                        qDao.deleteBuilder().clear();

                        for (String json : result) {
                            saveQuestion(json);
                            //PrefUtils.writeString(u.fileName);
                        }

                    } catch (RuntimeException e) {
                        SysUtils.toast("Error while parsing data.\n" + e.getMessage());
                    }
                }

                spinner.setVisibility(View.GONE);
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

    private void downloadCompleted(DownloadInfo downloadInfo){

        if (downloadInfo != null) {
            if (downloadInfo.status != DownloadManager.STATUS_SUCCESSFUL) {
                SysUtils.toast(downloadInfo.getStatusMessage() + " " + downloadInfo.getErrorMessage());
            } else {

                //downloadInfo.localFileName
                //mDownloadService.startDownload();
            }
        }

        spinner.setVisibility(View.GONE);
    }

    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){

                DownloadInfo info = null;
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
                if ( id != mDownloadService.getDownloadId()){
                    Log.d(TAG, "Different download ID. Skip.");
                } else {
                    info = mDownloadService.queryDownloadInfo();
                }

                downloadCompleted(info);

            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)){
                DownloadInfo info = mDownloadService.queryDownloadInfo();
                Log.d(TAG, "##### Downloaded:" + info);
            }
        }
    };

}
