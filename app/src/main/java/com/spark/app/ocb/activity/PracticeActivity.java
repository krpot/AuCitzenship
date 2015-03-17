package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spark.app.ocb.MyApp;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.service.QuizService;

public class PracticeActivity extends Activity {
	
	private static final String TAG = "PracticeActivity";

	private TextView txtTitle, txtComment;
    private RadioGroup radioAnswer;
	private RadioButton[] answerButtons;
    private Button btnNext, btnAnothergo, btnFinish;
	
    private QuizService quizService;

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
                    quizService.goToFirst();
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);

        setupView();

        quizService = new QuizService(this, MyApp.app.exam(), mHandler);
        quizService.start();
	}

    /**
	 * Click event handler for btnNext
	 * @param view
	 */
	public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.btnNext:
                quizService.goToNext();
                break;

            case R.id.btnAnothergo:
                quizService.start();
                break;

            case R.id.btnFinish:
                this.finish();
                break;
        }

	}

	private void setupView(){
        setTitle(R.string.practice);

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

        btnNext     = (Button)findViewById(R.id.btnNext);
        btnAnothergo= (Button)findViewById(R.id.btnAnothergo);
        btnFinish   = (Button)findViewById(R.id.btnFinish);
	}

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) return;

            Question q = quizService.currentQuestion();
            if (q == null) return;

            RadioButton button = (RadioButton) buttonView;
            int selected = (Integer) button.getTag();
            quizService.selectAnswer(selected);
        };
    };

    private void checkAnswer(Question q){
        boolean correct = q.isCorrect();

        String html = "";
        btnNext.setEnabled(correct);
        if (correct) {
            html = "<p>Correct! </p>";

            if (quizService.isLast()) {
                btnNext.setVisibility(View.GONE);
                btnAnothergo.setVisibility(View.VISIBLE);
            }
        } else {
            html = "<p><font color='red'>Wrong! Try again.</font> </p>";
        }

        txtComment.setText(Html.fromHtml(html));
    }

    private void displayQuestion(Question question){
        if (question == null || question.answers==null) return;

        Log.d(TAG, "##### displayQuestion / " + question);

        setTitle("Question " + (quizService.getPosition() + 1) + " of " + quizService.total());
        txtTitle.setText(question.statement);
        txtComment.setText("");
        btnNext.setEnabled(false);
        radioAnswer.clearCheck();

        //boolean isLastQuestion = quizService.isLast();
        btnNext.setVisibility(View.VISIBLE);
        btnAnothergo.setVisibility(View.GONE);

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

}
