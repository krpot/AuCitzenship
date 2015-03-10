package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.Exam;
import com.spark.app.ocb.task.QuestionShuffleTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.List;

public class PracticeActivity extends Activity {
	
	private static final String TAG = "PracticeActivity";

	private Dao<Question, Integer> mQDao;
	private TextView txtTitle, txtComment;
    private RadioGroup radioAnswer;
	private RadioButton radioA, radioB, radioC;
    private Button btnNext, btnAnothergo, btnFinish;
	
    private Exam mExam = new Exam();

	private int mPosition = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);
		
		setupView();
        generateQuestions();
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
//	public void btnBeforeClick(View view){
//		if (mPosition<=1){
//			//SysUtils.toast("Begining of the questions.");
//			return;
//		}
//
//		mPosition--;
//		loadData();
//	}
//
	/**
	 * Click event handler for btnNext
	 * @param view
	 */
	public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.btnNext:
                if (mPosition>=19){
                    SysUtils.toast("End of the questions.");
                    return;
                }

                mPosition++;
                nextQuestion();

                if (isLastQuestion())
                    btnNext.setEnabled(false);
                break;

            case R.id.btnAnothergo:
                generateQuestions();
                onButtonClick(btnNext);
                break;

            case R.id.btnFinish:
                this.finish();
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

        btnNext     = (Button)findViewById(R.id.btnNext);
        btnAnothergo= (Button)findViewById(R.id.btnAnothergo);
        btnFinish   = (Button)findViewById(R.id.btnFinish);
	}

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

            String html = "";
            btnNext.setEnabled(answer.correct);
            if (answer.correct) {
                html = "<p>Correct! </p>";

                if (isLastQuestion()) {
                    btnNext.setVisibility(View.GONE);
                    btnAnothergo.setVisibility(View.VISIBLE);
                }
            } else {
                html = "<p><font color='red'>Wrong! Try again.</font> </p>";
            }

            txtComment.setText(Html.fromHtml(html));

            int selected = (Integer)button.getTag(R.string.key_index);
            mExam.setSelected(question, selected);
        }
    };

    /*
     *
     */
    private void generateQuestions() {
        mPosition = -1;
        btnNext.setVisibility(View.VISIBLE);
        btnAnothergo.setVisibility(View.GONE);

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
                    mExam.questions = result;
                    onButtonClick(btnNext);
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

            mExam.questions = questionList;

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
        Question question = mExam.questions.get(mPosition);
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
                SysUtils.toast("Load data error.");
                return;
            }

            question.shuffle();
        }

        setTitle("Question " + (mPosition + 1) + " of " + mExam.questions.size());
		txtTitle.setText(question.statement);
		txtTitle.setTag(question);
        txtComment.setText("");
        btnNext.setEnabled(false);
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
        return mPosition>=mExam.questions.size()-1;
    }

    private Question currentQuestion(){
        if (mExam == null) return null;

        if (mPosition>=0 && mPosition<=mExam.questions.size()-1)
            return mExam.questions.get(mPosition);

        return null;
    }

}