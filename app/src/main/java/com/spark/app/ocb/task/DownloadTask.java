package com.spark.app.ocb.task;

import android.os.AsyncTask;
import android.util.Log;

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
public class DownloadTask extends AsyncTask<String, String, List<String>> {

    private static final String TAG = "DownloadTask";

    TaskListener<List<String>> mListener;

    public DownloadTask(TaskListener<List<String>> listener){
        this.mListener = listener;
    }

    @Override
    protected List<String> doInBackground(String... urls) {
        if (urls.length == 0)
            return null;

        Map<String, String> resultMap = new HashMap<String, String>();
        List<String> results = new ArrayList<String>();
        for (String url: urls) {

            Log.d(TAG, "========== DownloadTask: " + url);
            //resultMap(url, download(url));
            results.add(download(url));
        }

        return results;
    }

    @Override
    protected void onPostExecute(List<String> list) {
        if (mListener != null)
            mListener.onComplete(list);
    }

    private String download(String fileName){

        URL url = null;
        HttpURLConnection conn = null;

        try {
            url = new URL(fileName);
            conn = (HttpURLConnection)url.openConnection();
            //conn.setConnectTimeout();
            //conn.setReadTimeout();

            StringBuffer sb = new StringBuffer();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            while ((line = br.readLine()) != null){
                sb.append(line);
            }

            Log.d(TAG, "#### read:" + sb.toString());

            br.close();

            return sb.toString();

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
