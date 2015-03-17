package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
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
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.service.QuizService;
import com.spark.app.ocb.util.CountDownTimerWithPause;
import com.spark.app.ocb.util.DialogUtils;
import com.spark.app.ocb.util.SysUtils;

import java.util.List;

import static com.spark.app.ocb.MyApp.app;

public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";

    private TextView txtTitle, txtComment;
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
                        seekBar.setMax(questionList.size());
                    }
                    txtComment.setTextColor(Color.BLACK);
                    quizService.goToFirst();
                    mTimer.create();

                    break;

                case QuizService.MSG_SELECTED:
                    Log.d(TAG, "##### MSG_SELECTED:" + msg.arg1 + ", Question:" + msg.obj);
                    checkAnswer((Question)msg.obj);
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
            txtComment.setText("Time left: " + DateUtils.formatElapsedTime(seconds));
            //if it has less than one minute left
            if (seconds<=(5*60)){
                txtComment.setTextColor(Color.RED);
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

        //boolean paused = mTimer.isPaused();
        //Log.d(TAG, "##### onCreateOptionsMenu / " + paused);
        //menu.findItem(R.id.menu_resume).setVisible(paused);
        //menu.findItem(R.id.menu_pause).setVisible(!paused);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmExit();
                break;
            case R.id.menu_list:
                displayAsList();
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
        txtComment = (TextView)findViewById(R.id.txtComment);

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

    private void displayAsList(){
        //mTimer.pause();

        //mTimer.resume();
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
                seekBar.setProgress(position);
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

//            if (txtTitle.getTag() == null || !(txtTitle.getTag() instanceof Question)) return;
//            Question question = (Question)txtTitle.getTag();
//            Log.d(TAG, "##### Question:" + question);
//
//            RadioButton button = (RadioButton)buttonView;
//            if (button.getTag(R.string.key_answer) == null || !(button.getTag(R.string.key_answer) instanceof Answer)) return;
//            Answer answer = (Answer)button.getTag(R.string.key_answer);
//            Log.d(TAG, "##### Answer:" + answer);
//
//            int selected = (Integer)button.getTag(R.string.key_index);
//            app.exam().setSelected(question, selected);
        }
    };

    /*
     *
     */
    private void checkAnswer(Question q){
//        boolean correct = q.isCorrect();

//        String html = "";
//        btnNext.setEnabled(correct);
//        if (correct) {
//            html = "<p>Correct! </p>";
//
//            //if (quizService.isLast()) {
//            //    btnNext.setVisibility(View.GONE);
//            //}
//        } else {
//            html = "<p><font color='red'>Wrong! Try again.</font> </p>";
//        }
//
//        txtComment.setText(Html.fromHtml(html));
    }

    /*
     *
     */
    private void displayQuestion(Question question){
        if (question == null || question.answers==null) return;

        Log.d(TAG, "##### displayQuestion / " + question);

        setTitle("Question " + (quizService.getPosition() + 1) + " of " + quizService.total());
        txtTitle.setText(question.statement);
        btnNext.setEnabled(false);
        radioAnswer.clearCheck();

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

        if (question.selected>=0 && question.selected<question.answers.size()){
            answerButtons[question.selected].setChecked(true);
        }
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
