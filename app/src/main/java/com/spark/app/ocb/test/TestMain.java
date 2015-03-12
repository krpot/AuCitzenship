package com.spark.app.ocb.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.TextView;

import com.robotium.solo.Solo;
import com.spark.app.ocb.R;
import com.spark.app.ocb.activity.MainActivity;
import com.spark.app.ocb.activity.PracticeActivity;
import com.spark.app.ocb.activity.TestActivity;

/**
 * Created by sunghun
 */
public class TestMain extends ActivityInstrumentationTestCase2<PracticeActivity> {

    private Solo solo;

    public TestMain() {
        super(PracticeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testDisplayWhiteBox(){

        TextView txtComment = (TextView)solo.getView(R.id.txtComment);

        for (int i=1; i<=19; i++) {
            solo.clickOnRadioButton(0);
            String s1 = txtComment.getText().toString();

            solo.clickOnRadioButton(1);
            String s2 = txtComment.getText().toString();

            solo.clickOnRadioButton(2);
            String s3 = txtComment.getText().toString();

            assertTrue(s1.startsWith("Correct") || s2.startsWith("Correct") || s3.startsWith("Correct"));
        }
    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

}
