package com.spark.app.ocb.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

/**
 * Created by sunghun
 */
public class PrefUtils {

    static PrefUtils prefUtils;
    static SharedPreferences mPref;

    Context mContext;

    private PrefUtils(Context context){
        mContext = context;
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PrefUtils getInstance(Context context){
        if (prefUtils == null)
            prefUtils = new PrefUtils(context);

        return prefUtils;
    }

    public static String readString(String key){
        return mPref.getString(key, "");
    }

    public static PrefUtils writeString(String key, String value){
        mPref.edit().putString(key, value).commit();
        return prefUtils;
    }

    public static int readInt(String key){
        return mPref.getInt(key, 0);
    }

    public static PrefUtils writeInt(String key, int value){
        mPref.edit().putInt(key, value).commit();
        return prefUtils;
    }

    public static boolean readBool(String key){
        return mPref.getBoolean(key, false);
    }

    public static PrefUtils writeBool(String key, boolean value){
        mPref.edit().putBoolean(key, value).commit();
        return prefUtils;
    }

}
