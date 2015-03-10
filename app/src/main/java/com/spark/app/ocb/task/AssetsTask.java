package com.spark.app.ocb.task;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

import com.spark.app.ocb.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by sunghun
 */
public abstract class AssetsTask<T> extends ResourceTask<T> {

    private static final String TAG = "AssetsTask";


    public AssetsTask(Context context){
        super(context);
    }

    public AssetsTask(Context context, TaskListener<List<T>> listener) {
        super(context, listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<T> list) {
        super.onPostExecute(list);
    }

    @Override
    public List<String> listFiles(String path) {
        AssetManager asm = mContext.getAssets();
        try {
            return Arrays.asList(asm.list(""));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public InputStream inputStream(String path) {
        try {
            return mContext.getAssets().open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

//    @Override
//    public boolean filter(String fileName) {
//        return false;
//    }
//
//    @Override
//    public T parse(String json) {
//        return null;
//    }
}
