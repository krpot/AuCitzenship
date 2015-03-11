package com.spark.app.ocb.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.spark.app.ocb.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunghun
 */
public abstract class ResourceTask<T> extends AsyncTask<String, List<T>, List<T>> {

    private static final String TAG = "ResourceTask";

    protected final Context mContext;
    protected TaskListener<List<T>> mListener;

    public ResourceTask(Context context){
        this.mContext = context;
    }

    public ResourceTask(Context context, TaskListener<List<T>> listener){
        this.mContext = context;
        this.mListener = listener;
    }

    @Override
    protected List<T> doInBackground(String... strings) {
        return readFiles(listFiles(strings.length>0 ? strings[0] : ""));
    }

    @Override
    protected void onPostExecute(List<T> list) {
        if (mListener != null)
            mListener.onComplete(list);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    private List<T> readFiles(List<String> fileList) {
        BufferedInputStream in = null;
        try {

            for (String s : fileList) {
                Log.d(TAG, "##### file name:" + s);

                if (filter(s)) {
                    in = new BufferedInputStream(inputStream(s));
                    String jsonStr = FileUtils.readTextFile(in);
                    Log.d(TAG, jsonStr);

                    return parse(jsonStr);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
            if (mListener != null) mListener.onError(e);
        } finally {
            if (in != null) try {
                in.close();
            } catch (Exception e) {
            }
        }

        return Collections.emptyList();
    }

    public abstract List<String> listFiles(String path);
    public abstract InputStream inputStream(String path);
    public abstract boolean filter(String fileName);
    public abstract List<T> parse(String json);
}
