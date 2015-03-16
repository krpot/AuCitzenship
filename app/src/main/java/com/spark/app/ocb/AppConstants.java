package com.spark.app.ocb;

/**
 * Created by spark
 */
public class AppConstants {

    public static final int REQUEST_MODAL = 10001;

    public static final long TEST_MINUTE = 1000 * 60 * 45;

    public static final String EXTRA_DATA1 = "data1";

    public static final String QUESTION_URL = "http://www.janeart.net/citizen/questions/";
    public static final String QUESTION_UPDATE_URL = QUESTION_URL + "update.json";

    public static final String KEY_QUESTION_UPDATED = "question_updated";
    //public static final String KEY_UPDATE_LIST_UPDATED = "update_list_updated";

    //========================================================
    // SimpleEntry TestResult
    //========================================================
    public static final String KEY_RESULT_TOTAL     = "Total";
    public static final String KEY_RESULT_CORRECT   = "Correct";
    public static final String KEY_RESULT_WRONG     = "Wrong";
    public static final String KEY_RESULT_MISSING   = "Missing";
    public static final String KEY_RESULT_MARKS     = "Marks";
    public static final String KEY_RESULT_TIME      = "Time";
}
