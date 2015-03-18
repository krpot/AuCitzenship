package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.spark.app.ocb.AppConstants;
import com.spark.app.ocb.MyApp;
import com.spark.app.ocb.R;
import com.spark.app.ocb.adpter.QuestionFilterAdapter;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.service.QuizService;
import com.spark.app.ocb.util.CountDownTimerWithPause;
import com.spark.app.ocb.util.DialogUtils;
import com.spark.app.ocb.util.SysUtils;

import java.util.ArrayList;
import java.util.List;

import static com.spark.app.ocb.MyApp.app;

public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";

    private TextView txtTitle, txtElapsed;
    private RadioGroup radioAnswer;
    private RadioButton[] answerButtons;
    private Button btnNext, btnBefore, btnSubmit;
    private SeekBar seekBar;

    private QuizService quizService;

    //----------------------------------------------------
    //
    // Handler for QuizService
    //----------------------------------------------------
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case QuizService.MSG_CHANGED:
                    Log.d(TAG, "##### MSG_CHANGED:" + msg.arg1 + ", question:" + msg.obj);
                    displayQuestion((Question)msg.obj);
                    break;

                case QuizService.MSG_LOAD_FINISHED:
                    Log.d(TAG, "##### MSG_LOAD_FINISHED:" + msg.obj);

                    if (msg.obj != null) {
                        List<Question> questionList = (List<Question>)(msg.obj);
                        seekBar.setMax(questionList.size()-1);
                    }
                    txtElapsed.setTextColor(Color.BLACK);
                    quizService.goToFirst();
                    invalidateOptionsMenu();
                    mTimer.create();
                    break;

                case QuizService.MSG_SELECTED:
                    Log.d(TAG, "##### MSG_SELECTED:" + msg.arg1 + ", Question:" + msg.obj);
                    break;

                case QuizService.MSG_ERROR:
                    Log.d(TAG, "##### MSG_ERROR:" + msg.obj);
                    break;
            }
        }
    };

    //----------------------------------------------------
    //
    // CountDownTimer
    //----------------------------------------------------
    private CountDownTimerWithPause mTimer = new CountDownTimerWithPause(AppConstants.TEST_MINUTE, 1000, true){

        @Override
        public void onTick(long millisUntilFinished) {
            app.exam().elapsed = AppConstants.TEST_MINUTE - millisUntilFinished;

            long seconds = millisUntilFinished/1000L;
            txtElapsed.setText("Time left: " + DateUtils.formatElapsedTime(seconds));
            //if it has less than one minute left
            if (seconds<=(5*60)){
                txtElapsed.setTextColor(Color.RED);
            }

            int minutes = (int)(seconds /60);
            if (minutes <= 10 && (minutes % 3 == 0)) {
                SysUtils.toast(minutes + " minutes left.");
            }
        }

        @Override
        public void onFinish() {
            radioAnswer.setEnabled(false);

            DialogUtils.alertResStr(TestActivity.this,
                    R.string.app_name,
                    R.string.alert_test_finished,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showTestResult();
                        }
                    });
        }
    };

    //----------------------------------------------------
    //
    //
    //----------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        setupView();

        quizService = new QuizService(this, MyApp.app.exam(), mHandler);
        quizService.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);

        /*
         * Code when you are not using AppCompat
        View count = menu.findItem(R.id.badge).getActionView();
        notifCount = (Button) count.findViewById(R.id.notif_count);
        notifCount.setText(String.valueOf(mNotifCount));
        return super.onCreateOptionsMenu(menu);
        */

        //If you use AppCompat, you should set the ActionLayout in code :
        // MenuItem item = menu.findItem(R.id.badge);
        // MenuItemCompat.setActionView(item, R.layout.feed_update_count);
        // notifCount = (Button) MenuItemCompat.getActionView(item);
        //Use supportInvalidateOptionsMenu() need to use instead of invalidateOptionsMenu() if you target for API level below 11

        int count = quizService.unAnswered();
        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.badge_count);
        Button badgeCount = (Button) MenuItemCompat.getActionView(item);
        badgeCount.setText(String.valueOf(count));
        badgeCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertMissingQuestions();
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmExit();
                break;
//            case R.id.menu_pause:
//                Log.d(TAG, "##### onOptionsItemSelected / menu_pause");
//                mTimer.pause();
//                break;
//            case R.id.menu_resume:
//                Log.d(TAG, "##### onOptionsItemSelected / menu_resume");
//                mTimer.resume();
//                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Click event handler for buttons
     * @param view
     */
    public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.btnBefore:
                quizService.goToPrior();
                break;

            case R.id.btnNext:
                quizService.goToNext();
                break;

            case R.id.btnSubmit:
                submitTest();
                break;

            case R.id.badgeCount:
                alertMissingQuestions();
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.REQUEST_MODAL){
            switch (resultCode){
                case Activity.RESULT_CANCELED:
                    finish();
                    break;
                case Activity.RESULT_OK:
                    app.exam().clear();
                    quizService.start();
                    break;
            }
        }
    }

    private void setupView(){
        setTitle(R.string.test);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtTitle = (TextView)findViewById(android.R.id.text1);
        txtElapsed = (TextView)findViewById(R.id.txtElapsed);

        answerButtons = new RadioButton[3];
        answerButtons[0] = (RadioButton)findViewById(R.id.radioa);
        answerButtons[1] = (RadioButton)findViewById(R.id.radiob);
        answerButtons[2] = (RadioButton)findViewById(R.id.radioc);

        for (int i=0, sz=answerButtons.length; i<sz; i++){
            answerButtons[i].setTag(i);
            answerButtons[i].setOnCheckedChangeListener(onCheckedChangeListener);
        }

        radioAnswer = (RadioGroup)findViewById(R.id.radioAnswer);

        btnBefore   = (Button)findViewById(R.id.btnBefore);
        btnNext     = (Button)findViewById(R.id.btnNext);
        btnSubmit   = (Button)findViewById(R.id.btnSubmit);

        seekBar     = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

    }

    private void confirmExit(){
        mTimer.pause();
        DialogUtils.confirm(this, R.string.confirm_exit_test, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE)
                    finish();
                else
                    mTimer.resume();
            }
        });
    }

    //----------------------------------------------------
    //
    // Seekbar listener
    //----------------------------------------------------
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
            if (fromUser){
                quizService.setPosition(position);
                //seekBar.setProgress(position);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    //----------------------------------------------------
    //
    // When tapping answer radiobox
    //----------------------------------------------------
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) return;

            Question q = quizService.currentQuestion();
            if (q == null) return;

            RadioButton button = (RadioButton) buttonView;
            int selected = (Integer) button.getTag();
            quizService.selectAnswer(selected);

            invalidateOptionsMenu();
            updateAnswerState(q);
        }
    };


    /*
     *
     */
    private void displayQuestion(Question question){
        if (question == null || question.answers==null) return;

        Log.d(TAG, "##### displayQuestion / " + question);

        setTitle("Question " + question.id + " of " + quizService.total());
        txtTitle.setText(question.statement);

        seekBar.setProgress(quizService.getPosition());

        btnBefore.setEnabled(!quizService.isFirst());
        btnNext.setEnabled(!quizService.isLast());

        Log.d(TAG, "##### Question.Answer.size:" + question.answers.size());

        int i=0;
        for (Answer answer : question.answers){
            Log.d(TAG, "##### question.answer:" + answer);
            if (i>=answerButtons.length) break;

            RadioButton radioButton = answerButtons[i++];
            radioButton.setText(answer.answer);
        }

        radioAnswer.clearCheck();
        if (question.selected>=0 && question.selected<question.answers.size()){
            answerButtons[question.selected].setChecked(true);
        }

        updateAnswerState(question);
    }

    /*
     *
     */
    private void updateAnswerState(Question question){
        //txtRemain.setText(quizService.unAnswered() + " missing");
        Drawable drwable = getResources().getDrawable(question.answered() ? R.drawable.well : R.drawable.wellwarning);
        txtTitle.setBackground(drwable);
        radioAnswer.setBackground(drwable);
    }

    /*
     *
     */
    private void alertMissingQuestions(){
        final List<Question> missings = quizService.getMissingQuestions();
        Log.d(TAG, "=============== alertMissingQuestions / " + missings.size());
        if (missings.size() == 0) return;

        mTimer.pause();
        String[] result = new String[missings.size()];
        for (int i=0, sz= missings.size(); i<sz; i++){
            Question q = missings.get(i);
            result[i] = q.id + ". " + q.statement;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.missing_questions)
               .setItems(result, new DialogInterface.OnClickListener(){
                   @Override
                   public void onClick(DialogInterface dialog, int position) {
                       Log.d(TAG, "-------- AlertDialog.Builder / DialogInterface.onClick : " + position);

                       quizService.setPosition(missings.get(position));
                       dialog.dismiss();

                       mTimer.resume();
                   }
               })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mTimer.resume();
                    }
                })
                .create()
                .show();
    }

    /*
     *
     */
    private void submitTest(){
        if (mTimer.isRunning())
            mTimer.pause();

        String msg = "";
        int unanswered = quizService.unAnswered();

        if (unanswered>0){
            String sQuestion = unanswered == 1 ? "One question " : unanswered + " questions";
            msg = String.format(getString(R.string.alert_not_answered), sQuestion) + "\n";
        }

        msg += getString(R.string.confirm_submit_test);

        DialogUtils.confirm(this, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    mTimer.cancel();
                    showTestResult();
                } else {
                    mTimer.resume();
                }
            }
        });

    }

    private void showTestResult(){
        Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
        startActivityForResult(intent, AppConstants.REQUEST_MODAL);
        //finish();
    }
}
