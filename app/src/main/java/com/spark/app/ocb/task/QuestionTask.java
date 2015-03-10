package com.spark.app.ocb.task;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.util.BeanUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by sunghun.
 */
public class QuestionTask extends AssetsTask<Question> {

    private static final String TAG = "QuestionTask";

    public QuestionTask(Context context) {
        super(context);
    }

    public QuestionTask(Context context, TaskListener<List<Question>> listener) {
        super(context, listener);
    }

    @Override
    public boolean filter(String fileName) {
        return (fileName.startsWith("question") && fileName.endsWith(".json"));
    }

    @Override
    public List<Question> parse(String json) {
        Dao<Question, Integer> qDao = BeanUtils.getQuestionDao(mContext);
        Dao<Answer, Integer> aDao = BeanUtils.getAnswerDao(mContext);


        try {
            aDao.deleteBuilder().clear();
            qDao.deleteBuilder().clear();

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

            return questions;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
