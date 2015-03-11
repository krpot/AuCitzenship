package com.spark.app.ocb.service;

import android.app.DownloadManager;
import android.database.Cursor;

/**
 * Created by sunghun
 */
public class DownloadInfo {

    public long id;
    public int bytesDownloadedSoFar;
    public String description;
    public long lastModifiedTimeStamp;
    public String localFileName;
    public String localUri;
    public String mediaProviderUri;
    public String mediaType;
    public int reason;
    public int status;
    public String title;
    public int totalSizeBytes;
    public String uri;


    public static DownloadInfo fromCursor(Cursor c){
        DownloadInfo info = new DownloadInfo();

        info.id = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID));
        info.bytesDownloadedSoFar = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        info.lastModifiedTimeStamp = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP));
        info.localFileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
        info.localUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
        info.mediaProviderUri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIAPROVIDER_URI));
        info.mediaType = c.getString(c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
        info.reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
        info.status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
        info.title = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
        info.totalSizeBytes = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        info.uri = c.getString(c.getColumnIndex(DownloadManager.COLUMN_URI));

        return info;
    }

    public String getErrorMessage(){
        String s = "";
        switch (this.reason){
            case DownloadManager.ERROR_CANNOT_RESUME:
                s = "Some possibly transient error occurred but we can't resume the download.";
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                s = "No external storage device was found.";
                break;
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                s = "The requested destination file already exists.";
                break;
            case DownloadManager.ERROR_FILE_ERROR:
                s = "A storage issue arises which doesn't fit under any other error code.";
                break;
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                s = "An error receiving or processing data occurred at the HTTP level.";
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                s = "There was insufficient storage space.";
                break;
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                s = "There were too many redirects.";
                break;
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                s = "An HTTP code was received that download manager can't handle.";
                break;
            case DownloadManager.ERROR_UNKNOWN:
                s = "The download has completed with an error that doesn't fit under any other error code.";
                break;
            default:
                break;

        }

        return s;
    }

    public String getStatusMessage(){
        String s = "";
        switch (this.status){
//            case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
//                s = "The download exceeds a size limit for downloads over the mobile network and the download manager is waiting for a Wi-Fi connection to proceed.";
//                break;
//            case DownloadManager.PAUSED_UNKNOWN:
//                s = "The download is paused for some other reason.";
//                break;
//            case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
//                s = "The download is waiting for network connectivity to proceed.";
//                break;
//            case DownloadManager.PAUSED_WAITING_TO_RETRY:
//                s = "The download is paused because some network error occurred and the download manager is waiting before retrying the request.";
//                break;
            case DownloadManager.STATUS_FAILED:
                s = "The download has failed (and will not be retried).";
                break;
            case DownloadManager.STATUS_PAUSED:
                s = "The download is waiting to retry or resume.";
                break;
            case DownloadManager.STATUS_PENDING:
                s = "The download is waiting to start.";
                break;
            case DownloadManager.STATUS_RUNNING:
                s = "The download is currently running.";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                s = "The download successful.";
                break;
            default:
                break;

        }

        return s;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "id=" + id +
                ", bytesDownloadedSoFar=" + bytesDownloadedSoFar +
                ", description='" + description + '\'' +
                ", lastModifiedTimeStamp=" + lastModifiedTimeStamp +
                ", localFileName='" + localFileName + '\'' +
                ", localUri='" + localUri + '\'' +
                ", mediaProviderUri='" + mediaProviderUri + '\'' +
                ", mediaType='" + mediaType + '\'' +
                ", reason=" + reason +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", totalSizeBytes=" + totalSizeBytes +
                ", uri='" + uri + '\'' +
                '}';
    }
}
