package com.spark.app.ocb.adpter;

import android.widget.TextView;

/**
 * Created by sunghun
 */
public class ViewHolder {

    private TextView text1;
    private TextView text2;

    public ViewHolder(TextView v1, TextView v2){
        text1 = v1;
        text2 = v2;
    }

    public TextView getText1(){
        return text1;
    }

    public ViewHolder setText1(TextView v){
        text1 = v;
        return this;
    }

    public TextView getText2(){
        return text2;
    }

    public ViewHolder setText2(TextView v){
        text2 = v;
        return this;
    }

    public String text1(){
        return ""+text1.getText();
    }

    public ViewHolder text1(String value){
        text1.setText(value);
        return this;
    }

    public String text2(){
        return ""+text2.getText();
    }

    public ViewHolder text2(String value){
        text1.setText(value);
        return this;
    }

    public void text(String s1, String s2){
        text1.setText(s1);
        text2.setText(s2);
    }
}
