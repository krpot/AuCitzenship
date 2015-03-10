package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.ExamResult;

import static com.spark.app.ocb.MyApp.app;

public class TestResultActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = "TestResultActivity";

    private Dao<Question, Integer> mQDao;
    private SummaryFragment mSummaryFragment;
    private ReviewFragment mReviewFragment;

    //----------------------------------------------------
    //
    //
    //----------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_result);

        setupView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Click event handler for buttons
     * @param view
     */
    public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.btnBefore:
                break;

            case R.id.btnSubmit:
                break;
        }

    }

    private void setupView() {
//        txtTitle = (TextView)findViewById(android.R.id.text1);
//        txtComment = (TextView)findViewById(R.id.txtComment);
//        btnBefore   = (Button)findViewById(R.id.btnBefore);
//        btnNext     = (Button)findViewById(R.id.btnNext);
//        btnSubmit   = (Button)findViewById(R.id.btnSubmit);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        String[] tabs = getResources().getStringArray(R.array.tabs);
        for (String s : tabs) {
            actionBar.addTab(actionBar.newTab().setText(s).setTabListener(this));
        }

    }

    //---------------------------------------------------------------------
    // ActionBar.TabListener
    //---------------------------------------------------------------------
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        Log.d(TAG, "#### tab.getPosition():" + tab.getPosition());

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (tab.getPosition()){
            case 0:
                if (mSummaryFragment == null)
                    mSummaryFragment = new SummaryFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, mSummaryFragment)
                        .commit();
                break;
            case 1:
                if (mReviewFragment == null)
                    mReviewFragment = new ReviewFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, mSummaryFragment)
                        .commit();
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    //---------------------------------------------------------------------
    // for summary tab
    //---------------------------------------------------------------------
    public static class SummaryFragment extends Fragment {

        TextView txtSummary;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_result_summary, container, false);

            txtSummary = (TextView)rootView.findViewById(R.id.txtSummary);

            ExamResult result = ExamResult.newInstance(app.exam());

            StringBuffer sb = new StringBuffer();
            sb.append("<h4>Questions: ").append(result.total).append("</h4>")
              .append("<h4>Score: ").append(result.correct).append("</h4>")
              .append("<h4>Wrong: ").append(result.wrong).append("</h4>")
              .append("<h4>Marks: ").append(result.markRatio()).append("%</h4>")
              .append("<h4>Unanswered: ").append(result.unanswered).append("</h4>");
            txtSummary.setText(Html.fromHtml(sb.toString()));

            return rootView;
        }

    }

    //---------------------------------------------------------------------
    // for review tab
    //---------------------------------------------------------------------
    public static class ReviewFragment extends Fragment {

        public ReviewFragment(){}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_result_review, container, false);

            return rootView;
        }

    }
}
