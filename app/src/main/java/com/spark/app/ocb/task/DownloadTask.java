package com.spark.app.ocb.task;

import android.os.AsyncTask;
import android.util.Log;

import com.spark.app.ocb.util.FileUtils;
import com.spark.app.ocb.util.SysUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunghun
 */
public class DownloadTask extends AsyncTask<String, String, Map<String, String>> {

    private static final String TAG = "DownloadTask";

    TaskListener<Map<String, String>> mListener;

    public DownloadTask(TaskListener<Map<String, String>> listener){
        this.mListener = listener;
    }

    @Override
    protected Map<String, String> doInBackground(String... urls) {
        if (urls.length == 0)
            return null;

        Map<String, String> resultMap = new HashMap<String, String>();

        //List<String> results = new ArrayList<String>();
        for (String url: urls) {

            String key = FileUtils.baseNameWithoutExt(SysUtils.baseNameFromUrl(url));

            Log.d(TAG, "========== DownloadTask: " + url + ", baseName=" + key);
            resultMap.put(key, download(url));
            //results.add(download(url));
        }

        return resultMap;
    }

    @Override
    protected void onPostExecute(Map<String, String> map) {
        if (mListener != null)
            mListener.onComplete(map);
    }

    private String download(String fileName){

        URL url = null;
        HttpURLConnection conn = null;

        try {
            url = new URL(fileName);
            conn = (HttpURLConnection)url.openConnection();
            //conn.setConnectTimeout();
            //conn.setReadTimeout();

            return FileUtils.readTextFile(conn.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null)
                mListener.onError(e);
        } finally {

            if (conn != null) conn.disconnect();
        }

        return null;
    }
}
