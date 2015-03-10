package com.spark.app.ocb.task;

/**
 * Created by sunghun
 */
public interface TaskListener<T> {

    public void onError(Throwable th);
    public void onComplete(T result);

}
