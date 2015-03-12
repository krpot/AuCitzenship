package com.spark.app.ocb.service;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.Exam;
import com.spark.app.ocb.task.QuestionShuffleTask;
import com.spark.app.ocb.task.TaskListener;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by sunghun
 */
public class QuizService {

    private static final String TAG ="QuizService";

    public static final int MSG_LOAD_FINISHED = 20002;
    public static final int MSG_CHANGED = 20003;
    public static final int MSG_SELECTED = 20004;
    public static final int MSG_ERROR = 29999;

    Context mContext;
    Exam mExam;
    int mPosition = -1;
    Handler mHandler;

    public QuizService(Context context, Handler handler){
        mContext = context;
        mExam = new Exam();
        mHandler = handler;
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

    public void goToFirst(){
        mPosition = 0;
        goTo(mPosition);
    }
    /*
     *
     */
    private void goTo(int pos) {
        Question question = mExam.getQuestion(pos);
        Log.d(TAG, "##### Next Question:" + question);

        if (question.answers==null || question.answers.isEmpty()) {
            Log.d(TAG, "---------- goTo / Load answers ---------");
            try {
                Dao<Answer, Integer> answerDao = BeanUtils.getAnswerDao(mContext);
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

        sendMessage(MSG_CHANGED, pos, 0, question);

        Log.d(TAG, "##### Question.Answer.size:" + question.answers.size());
    }

    public int getPosition(){
        return mPosition;
    }

    /*
     *
     */
    public void start() {
        mPosition = -1;

        Dao<Question, Integer> mQDao = null;
        if (mQDao==null)
            mQDao = BeanUtils.getQuestionDao(mContext);

        QuestionShuffleTask task = new QuestionShuffleTask(mContext, mQDao, new TaskListener<List<Question>>() {
            @Override
            public void onError(Throwable th) {
                Log.d(TAG, "========= QuestionShuffleTask onError =========");
                //SysUtils.toast("Error while generating questions.");
                sendMessage(MSG_ERROR, 0, 0, th);
            }

            @Override
            public void onComplete(List<Question> result) {
                Log.d(TAG, "========= QuestionShuffleTask onComplete =========" + result);
                if (result != null && !result.isEmpty()) {
                    mExam.questions = result;
                    sendMessage(MSG_LOAD_FINISHED, 0, 0, result);
                }
            }
        });

        task.execute(20);
    }

    public Question currentQuestion(){
        return mExam.getQuestion(mPosition);
    }

    public int total(){
        Question q = currentQuestion();
        return q != null ? q.answers.size() : 0;
    }

    public boolean isLast(){
        return mPosition >= mExam.questions.size()-1;
    }

    public void selectAnswer(int selected){
        Question question = mExam.getQuestion(mPosition);
        if (question == null) return;

        question.selected = selected;
        sendMessage(MSG_SELECTED, selected, 0, question);
    }

    private void sendMessage(int what, int arg1, int arg2, Object obj){
        Message msg = Message.obtain(mHandler, what, arg1, arg2, obj);
        mHandler.sendMessage(msg);
    }

}
