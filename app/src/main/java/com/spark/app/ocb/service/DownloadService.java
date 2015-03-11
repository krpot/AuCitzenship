package com.spark.app.ocb.service;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by sunghun.
 */
public class DownloadService {

    private static final String TAG = "DownloadService";

    private Context mContext;
    private BroadcastReceiver mReceiver;
    private DownloadManager mDownloadManager = null;
    private DownloadManager.Query query = null;

    private long mDownloadId = -1;
    private boolean registered = false;
    //private DownloadListener mListener;

    public DownloadService(Context context) {
        mContext = context;
    }

    public DownloadService(Context context, BroadcastReceiver receiver) {
        mContext = context;
        this.mReceiver = receiver;
    }

    public long getDownloadId(){
        return mDownloadId;
    }

    public boolean isRegistered(){
        return registered;
    }

    public void release() {
        if (mReceiver != null){
            mContext.unregisterReceiver(mReceiver);
        }
    }

    public long startDownload(String url, String downloadName, String title){
        Log.d(TAG, "=============== startDownload ================");

        if (mReceiver == null) return mDownloadId;

        if (mDownloadManager == null)
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        f.mkdirs();
        Log.d(TAG, "####getExternalStoragePublicDirectory:" + f.toString());

        DownloadManager.Request request = new DownloadManager.Request((Uri.parse(url)));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle(title);
        request.setDescription("Downloading " + downloadName);

        //if (getExternalStorageState().equals(MEDIA_MOUNTED))
        //    request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, downloadName);
        //else
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, downloadName);

        if (mReceiver != null){
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            //mReceiver = new DownloadManagerReceiver();
            mContext.registerReceiver(mReceiver, filter);
            registered = true;
        }

        mDownloadId = mDownloadManager.enqueue(request);

        //AppUtils.toastResStr(R.string.download_starting);

        return mDownloadId;
    }

    public int cancel(){
        return mDownloadManager.remove(mDownloadId);
    }

//    public void setDownloadListener(DownloadListener downloadListener){
//        this.mListener = downloadListener;
//    }

    /*
     *
     */
    public DownloadInfo queryDownloadInfo(){
        DownloadInfo info = null;

        if (query == null) {
            query = new DownloadManager.Query();
            query.setFilterById(mDownloadId);
        }

        Cursor cursor = mDownloadManager.query(query);
        if (cursor.moveToFirst()) {
            info = DownloadInfo.fromCursor(cursor);
            Log.d(TAG, "##### Downloaded:" + info);
        }

        return info;
    }

    private class DownloadManagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
                release();

                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);

                if ( id != mDownloadId){
                    Log.d(TAG, "Different download ID. Skip.");
                    return;
                }

                DownloadInfo info = queryDownloadInfo();
                //if (info != null) {
                    //if (mListener != null)
                        //mListener.onComplete(info);
                //}

            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)){
                DownloadInfo info = queryDownloadInfo();
                Log.d(TAG, "##### Downloaded:" + info);

                //if (mListener != null)
                    //mListener.onNotificationClicked(info);
            }
        }
    };

    public static interface DownloadListener {
        public void onComplete(DownloadInfo downloadInfo);
        public void onNotificationClicked(DownloadInfo downloadInfo);
    }
}
