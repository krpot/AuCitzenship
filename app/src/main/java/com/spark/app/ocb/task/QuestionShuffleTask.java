package com.spark.app.ocb.task;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RawRowMapper;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.service.QuizService;
import com.spark.app.ocb.util.BeanUtils;
import com.spark.app.ocb.util.SysUtils;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunghun
 */
public class QuestionShuffleTask extends AsyncTask<Integer, List<Question>, List<Question>> {

    Context mContext;
    Dao<Question, Integer> mDao;
    Dao<Answer, Integer> mAnswerDao;
    TaskListener<List<Question>> mListener;


    public QuestionShuffleTask(Context context, TaskListener<List<Question>> listener){
        mContext = context;
        mDao = BeanUtils.getQuestionDao(context);
        mAnswerDao = BeanUtils.getAnswerDao(context);
        mListener = listener;
    }

    @Override
    protected List<Question> doInBackground(Integer... params) {
        int limit = params.length >0 ? params[0] : 20;
        return shuffle(limit);
    }

    @Override
    protected void onPostExecute(List<Question> questions) {
        if (mListener != null)
            mListener.onComplete(questions);
    }

    private List<Question> shuffle(int limit) {
        Question question = null;
        List<Question> questionList = null;
        try {
            String sql = "SELECT * FROM questions ORDER BY RANDOM() LIMIT " + limit;
            questionList = mDao.queryRaw(sql,
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

                            shuffleAnswers(q);

                            return q;
                        }
                    }).getResults();

            int no = 1;
            for (Question q : questionList)
                q.id = no++;

            return questionList;

        } catch (SQLException e) {
            e.printStackTrace();
            if (mListener != null)
                mListener.onError(e);
            //SysUtils.toast("Load data error.");
        }

        return Collections.emptyList();
    }

    private void shuffleAnswers(Question question) throws SQLException{
        if (question == null) return;

        question.answers = mAnswerDao.queryBuilder()
                .where()
                .eq("question_id", question.id)
                .query();

        question.shuffle();
    }
}
