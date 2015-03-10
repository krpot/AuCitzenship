package com.spark.app.ocb.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.RadioButton;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.Exam;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;

/**
 * Created by sunghun
 */
public class QuizService extends Service {

    private static final String TAG ="QuizService";

    Exam mExam;
    int mPosition = -1;
    QuizServiceListener mListener;

    private QuizServiceBinder mBinder = new QuizServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public QuizService setExam(Exam exam){
        mExam = exam;
        return this;
    }

    public QuizService setListener(QuizServiceListener listener){
        mListener = listener;
        return this;
    }

    public void goToPrior(){
        int pos = mPosition;
        pos--;
        if (pos < 0){
            return;
        }

        mPosition = pos;
        goTo(mPosition);
    }

    public void goToNext(){
        int pos = mPosition;
        pos++;
        if (pos > mExam.questions.size()-1){
            return;
        }

        mPosition = pos;
        goTo(mPosition);
    }

    /*
     *
     */
    private void goTo(int pos) {
        Question question = mExam.questions.get(pos);
        Log.d(TAG, "##### Next Question:" + question);

        if (question.answers==null || question.answers.isEmpty()) {
            Log.d(TAG, "---------- goTo / Load answers ---------");
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

        if (mListener!=null)
            mListener.onQuestionChanged(question);

//        setTitle("Question " + (mPosition + 1) + " of " + mExam.questions.size());
//        txtTitle.setText(question.statement);
//        txtTitle.setTag(question);
//        txtComment.setText("");
//        btnNext.setEnabled(false);
//        radioAnswer.clearCheck();

        Log.d(TAG, "##### Question.Answer.size:" + question.answers.size());

//        int i=0;
//        for (Answer answer : question.answers){
//            Log.d(TAG, "##### question.answer:" + answer);
//            RadioButton radioButton = (RadioButton)radioAnswer.getChildAt(i);
//            radioButton.setText(answer.answer);
//            radioButton.setTag(R.string.key_index, i);
//            radioButton.setTag(R.string.key_answer, answer);
//            i++;
//        }

    }

    private void setPosition(int pos){
        if (mPosition != pos){
            mPosition = pos;
            if (mListener!=null)
                mListener.onPositionChanged(pos);
        }
    }

    //------------------------------------------------
    // Binder class
    //------------------------------------------------
    public class QuizServiceBinder extends Binder {

        public QuizService getService(){
            return QuizService.this;
        }

    }

    //------------------------------------------------
    // Listener for service
    //------------------------------------------------
    public static interface QuizServiceListener {

        public void onQuestionChanged(Question question);
        public void onPositionChanged(int pos);
    }
}
