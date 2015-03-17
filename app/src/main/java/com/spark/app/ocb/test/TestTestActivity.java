package com.spark.app.ocb.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.spark.app.ocb.R;
import com.spark.app.ocb.activity.PracticeActivity;
import com.spark.app.ocb.activity.TestActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by sunghun
 */
public class TestTestActivity extends ActivityInstrumentationTestCase2<TestActivity> {

    private Solo solo;

    public TestTestActivity() {
        super(TestActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testSetPosition(){

        SeekBar seekBar = (SeekBar)solo.getView(R.id.seekBar);

        Method seekBarProgressMethod = null;

        try {
            seekBarProgressMethod = ProgressBar.class.getDeclaredMethod("setProgress", Integer.TYPE, Boolean.TYPE);
            seekBarProgressMethod.setAccessible(true);
            seekBarProgressMethod.invoke(seekBar, 9, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
