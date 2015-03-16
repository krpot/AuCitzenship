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

import static com.spark.app.ocb.MyApp.app;

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

    Dao<Question, Integer> mQDao = null;
    Dao<Answer, Integer> mAnswerDao  = null;

    public QuizService(Context context, Exam exam, Handler handler){
        mContext = context;
        mExam = exam;
        mHandler = handler;

        mQDao = BeanUtils.getQuestionDao(mContext);
        mAnswerDao = BeanUtils.getAnswerDao(mContext);
    }

    public void goToPrior(){
        int pos = mPosition;
        pos--;
        if (pos < 0){
            return;
        }

        setPosition(pos);
    }

    public void goToNext(){
        int pos = mPosition;
        pos++;
        if (pos > mExam.questions.size()-1){
            return;
        }

        setPosition(pos);
    }

    public void goToFirst(){
        setPosition(0);
    }

    /*
     *
     */
    private void goTo(int pos) {
        Question question = mExam.getQuestion(pos);
        Log.d(TAG, "##### Next Question:" + question);


//        if (question == null) {
//            return;
//        }
//
//        if (question.answers==null || question.answers.isEmpty()) {
//            Log.d(TAG, "---------- goTo / Load answers ---------");
//            try {
//                Dao<Answer, Integer> answerDao = BeanUtils.getAnswerDao(mContext);
//                question.answers = answerDao.queryBuilder()
//                        .where()
//                        .eq("question_id", question.id)
//                        .query();
//
//            } catch (SQLException e) {
//                e.printStackTrace();
//                SysUtils.toast("Load data error.");
//                return;
//            }
//
//            question.shuffle();
//        }

        sendMessage(MSG_CHANGED, pos, 0, question);

        Log.d(TAG, "##### Question.Answer.size:" + question.answers.size());
    }

    public int getPosition(){
        return mPosition;
    }

    public void setPosition(int position){
        mPosition = position;
        goTo(position);
    }

    /*
     *
     */
    public void start() {
        mPosition = -1;

        QuestionShuffleTask task = new QuestionShuffleTask(mContext, new TaskListener<List<Question>>() {
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

    public int unAnswered(){
        int unanswered = 0;

        for (Question question : mExam.questions){
            if (question == null && question.selected<0)
                unanswered++;
        }

        return unanswered;
    }

    public Question currentQuestion(){
        return mExam.getQuestion(mPosition);
    }

    public int total(){
        return mExam.questions.size();
    }

    public boolean isFirst(){
        return mPosition == 0;
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
