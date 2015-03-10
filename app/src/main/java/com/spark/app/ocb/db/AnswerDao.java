package com.spark.app.ocb.db;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.spark.app.ocb.entity.Answer;

import java.sql.SQLException;

/**
 * Created by sunghun
 */
public class AnswerDao extends BaseDaoImpl<Answer, Integer> {

    protected AnswerDao() throws SQLException {
        super(Answer.class);
    }

    @Override
    public int create(Answer data) throws SQLException {
        return super.create(data);
    }

    @Override
    public int update(Answer data) throws SQLException {
        return super.update(data);
    }
}
