package com.spark.app.ocb.util;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.db.DbHelper;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.Updates;

import java.sql.SQLException;

public class BeanUtils {

    public static Context context;
	
	static DbHelper mDbHelper = null;
	static Dao<Question, Integer> mQuestionDao = null;
	static Dao<Answer, Integer> mAnswerDao = null;

	private static DbHelper getDatabaseHelper() {
		if (mDbHelper == null) {
			mDbHelper = OpenHelperManager.getHelper(context, DbHelper.class);
		}
		return mDbHelper;
	}
	
	/**
	 * Close SQLite database
	 */
	public static void closeDatabase(){
		if (mDbHelper != null) {
			OpenHelperManager.releaseHelper();
			mDbHelper = null;
		}
	}
	
	public static Dao<Question, Integer> getQuestionDao(Context context){
		try {
			if (mQuestionDao == null)
				mQuestionDao = getDatabaseHelper().getQuestionDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return mQuestionDao;
	}

	public static Dao<Answer, Integer> getAnswerDao(Context context){
		try {
			if (mAnswerDao == null)
                mAnswerDao = getDatabaseHelper().getAnswerDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return mAnswerDao;
	}


}
