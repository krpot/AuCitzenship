package com.spark.app.ocb;

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
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Exam;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class PracticeActivity extends Activity {
	
	private static final String TAG = "PracticeActivity";

	private Dao<Question, Integer> mQDao;
	private TextView txtTitle, txtComment;
    private RadioGroup radioAnswer;
	private RadioButton radioA, radioB, radioC;
    private Button btnNext, btnAnothergo, btnFinish;
	
	private Set<Integer> mQuestionIds = new HashSet<Integer>();
    private Exam mExam = new Exam();

	private int mQuestoinId, mPosition = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practice);
		
		setupView();
        generateQuestions();
        onButtonClick(btnNext);
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
		txtTitle = (TextView)findViewById(android.R.id.text1);
		txtComment = (TextView)findViewById(R.id.txtComment);

		radioA = (RadioButton)findViewById(R.id.radioa);
		radioB = (RadioButton)findViewById(R.id.radiob);
		radioC = (RadioButton)findViewById(R.id.radioc);

        radioA.setOnCheckedChangeListener(onCheckedChangeListener);
        radioB.setOnCheckedChangeListener(onCheckedChangeListener);
        radioC.setOnCheckedChangeListener(onCheckedChangeListener);

        radioAnswer = (RadioGroup)findViewById(R.id.radioAnswer);
        //radioAnswer.setOnCheckedChangeListener(onCheckedChangeListener);

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

            //Answer a = (Answer)radioA.getTag(R.string.key_answer);
            //if (!a.correct){
            //}

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
    }

    /*
     *
     */
	private void nextQuestion() {
        Question question = mExam.questions.get(mPosition);
        Log.d(TAG, "##### Next Question:" + question);

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
		txtTitle.setText((mPosition + 1) + ". " + question.statement);
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

    /*
     *
     */
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
