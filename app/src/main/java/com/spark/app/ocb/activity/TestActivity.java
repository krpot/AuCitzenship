package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.AppConstants;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.task.QuestionShuffleTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.DialogUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.List;

import static com.spark.app.ocb.MyApp.app;

public class TestActivity extends Activity {

    private static final String TAG = "TestActivity";

    private Dao<Question, Integer> mQDao;
    private TextView txtTitle, txtComment;
    private RadioGroup radioAnswer;
    private RadioButton radioA, radioB, radioC;
    private Button btnNext, btnBefore, btnSubmit;
    private SeekBar seekBar;

    private int mPosition = -1;

    //----------------------------------------------------
    //
    // CountDownTimer
    //----------------------------------------------------
    private CountDownTimer mTimer = new CountDownTimer(AppConstants.TEST_MINUTE, 1000){

        @Override
        public void onTick(long millisUntilFinished) {
            app.exam().elapsed = AppConstants.TEST_MINUTE - millisUntilFinished;

            long seconds = millisUntilFinished/1000L;
            txtComment.setText("Time left: " + DateUtils.formatElapsedTime(seconds));

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
        generateQuestions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mTimer.cancel();
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
        switch(view.getId()){
            case R.id.btnBefore:
                if (mPosition<=0){
                    SysUtils.toast("Begin of the questions.");
                    return;
                }

                setPosition(--mPosition);
                nextQuestion();
                break;

            case R.id.btnNext:
                if (mPosition>=19){
                    SysUtils.toast("End of the questions.");
                    return;
                }

                setPosition(++mPosition);
                nextQuestion();
                break;

            case R.id.btnSubmit:
                submitTest();
                break;
        }

    }

    private void setupView(){
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtTitle = (TextView)findViewById(android.R.id.text1);
        txtComment = (TextView)findViewById(R.id.txtComment);

        radioA = (RadioButton)findViewById(R.id.radioa);
        radioB = (RadioButton)findViewById(R.id.radiob);
        radioC = (RadioButton)findViewById(R.id.radioc);

        radioA.setOnCheckedChangeListener(onCheckedChangeListener);
        radioB.setOnCheckedChangeListener(onCheckedChangeListener);
        radioC.setOnCheckedChangeListener(onCheckedChangeListener);

        radioAnswer = (RadioGroup)findViewById(R.id.radioAnswer);

        btnBefore   = (Button)findViewById(R.id.btnBefore);
        btnNext     = (Button)findViewById(R.id.btnNext);
        btnSubmit   = (Button)findViewById(R.id.btnSubmit);

        seekBar     = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){

        @Override
        public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
            if (fromUser){
                mPosition = position;
                nextQuestion();
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

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener(){

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) return;

            if (txtTitle.getTag() == null || !(txtTitle.getTag() instanceof Question)) return;
            Question question = (Question)txtTitle.getTag();
            Log.d(TAG, "##### Question:" + question);

            RadioButton button = (RadioButton)buttonView;
            if (button.getTag(R.string.key_answer) == null || !(button.getTag(R.string.key_answer) instanceof Answer)) return;
            Answer answer = (Answer)button.getTag(R.string.key_answer);
            Log.d(TAG, "##### Answer:" + answer);

            int selected = (Integer)button.getTag(R.string.key_index);
            app.exam().setSelected(question, selected);
        }
    };

    /*
     *
     */
    private void generateQuestions() {
        setPosition(-1);
        app.exam().clear();

        if (mQDao==null)
            mQDao = BeanUtils.getQuestionDao(this);

        QuestionShuffleTask task = new QuestionShuffleTask(this, mQDao, new TaskListener<List<Question>>() {
            @Override
            public void onError(Throwable th) {
                Log.d(TAG, "========= QuestionShuffleTask onError =========");
                SysUtils.toast("Error while generating questions.");
            }

            @Override
            public void onComplete(List<Question> result) {
                Log.d(TAG, "========= QuestionShuffleTask onComplete =========" + result);
                if (result != null && !result.isEmpty()) {
                    app.exam().questions = result;

                    seekBar.setMax(result.size());
                    seekBar.setProgress(0);

                    onButtonClick(btnNext);
                    mTimer.start();
                }
            }
        });

        task.execute(20);


        /*
        Question question = null;
        List<Question> questionList = null;
        try {
            String sql = "SELECT * FROM questions ORDER BY RANDOM() LIMIT 20";
            questionList = mQDao.queryRaw(sql,
                    new RawRowMapper<Question>(){
                        @Override
                        public Question mapRow(String[] strings, String[] strings2) throws SQLException {
                            Question q = new Question();
                            for (int i=0, sz=strings.length; i<sz; i++) {
                                String column = strings[i];
                                String value = strings2[i];
                                if ("id".equals(column)) q.id = Integer.valueOf(value);
                                if ("statement".equals(column)) q.statement = value;
                            }

                            return q;
                        }
                    }).getResults();


            if (questionList == null || questionList.isEmpty()) return;

            app.exam().questions = questionList;

        } catch (SQLException e) {
            e.printStackTrace();
            SysUtils.toast("Load data error.");
        }
        */
    }

    /*
     *
     */
    private void nextQuestion() {
        Question question = app.exam().questions.get(mPosition);
        Log.d(TAG, "##### Next Question:" + question);

        if (question.answers==null || question.answers.isEmpty()) {
            Log.d(TAG, "---------- nextQuestion / Load answers ---------");
            try {
                Dao<Answer, Integer> answerDao = BeanUtils.getAnswerDao(this);
                question.answers = answerDao.queryBuilder()
                        .where()
                        .eq("question_id", question.id)
                        .query();

            } catch (SQLException e) {
                e.printStackTrace();
                SysUtils.toast("Display question error.");
                return;
            }

            question.shuffle();
        }

        setTitle("Question " + (mPosition + 1) + " of " + app.exam().questions.size());
        txtTitle.setText(question.statement);
        txtTitle.setTag(question);
        radioAnswer.clearCheck();

        Log.d(TAG, "##### Question.Answer.size:" + question.answers.size());

        int i=0;
        for (Answer answer : question.answers){
            Log.d(TAG, "##### question.answer:" + answer);
            RadioButton radioButton = (RadioButton)radioAnswer.getChildAt(i);
            radioButton.setText(answer.answer);
            radioButton.setTag(R.string.key_index, i);
            radioButton.setTag(R.string.key_answer, answer);

            if (question.selected>=0 && i == question.selected){
                radioButton.setChecked(true);
            }

            i++;
        }

    }

    private boolean isLastQuestion(){
        return (!app.exam().questions.isEmpty()) && (mPosition >= ( app.exam().questions.size()-1));
    }

    private Question currentQuestion(){
        if (app.exam() == null) return null;

        if (mPosition>=0 && mPosition<=app.exam().questions.size()-1)
            return app.exam().questions.get(mPosition);

        return null;
    }

    /*
     *
     */
    private void setPosition(int position){
        if (mPosition != position)
            mPosition = position;

        seekBar.setProgress(position);

        btnBefore.setEnabled(mPosition>0);
        btnNext.setEnabled(!isLastQuestion());
    }

    /*
     *
     */
    private void submitTest(){
        int answered = 0;

        String msg = "";
        for (Question question : app.exam().questions){
            if (question ==  null) return;

            if (question.selected<0)
                answered++;
        }

        if (answered == 0){
            showTestResult();
            return;
        }

        if (answered>0){
            msg += getString(answered > 1 ? R.string.alert_not_answered_plural : R.string.alert_not_answered);
        }

        msg += "\n" + getString(R.string.confirm_submit_test);

        DialogUtils.confirm(this, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showTestResult();
            }
        });

    }

    private void showTestResult(){
        Intent intent = new Intent(TestActivity.this, TestResultActivity.class);
        startActivity(intent);

        finish();
    }
}
