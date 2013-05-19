package com.spark.app.citizen.util;

import java.sql.SQLException;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.spark.app.citizen.db.DbHelper;
import com.spark.app.citizen.entity.Question;

public class BeanUtils {
	
	static DbHelper mDbHelper = null;
	static Dao<Question, Integer> mQuestionDao = null;
	
	private static DbHelper getDatbaseHelper(Context context) {
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
				mQuestionDao = getDatbaseHelper(context).getQuestionDao();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return mQuestionDao;
	}
	
}
