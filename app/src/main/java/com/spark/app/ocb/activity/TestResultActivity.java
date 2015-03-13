package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.MyApp;
import com.spark.app.ocb.R;
import com.spark.app.ocb.adpter.QuestionAdapter;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.ExamResult;

import java.util.ArrayList;
import java.util.List;

import static com.spark.app.ocb.MyApp.app;

public class TestResultActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = "TestResultActivity";

    private Dao<Question, Integer> mQDao;
    private ListFragment mSummaryFragment;
    private ListFragment mReviewFragment;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Click event handler for buttons
     * @param view
     */
    public void onButtonClick(View view){
        switch(view.getId()){
            case R.id.btnFinish:
                setResult(Activity.RESULT_CANCELED);
                this.finish();
                break;

            case R.id.btnAnothergo:
                setResult(Activity.RESULT_OK);
                this.finish();
                break;
        }

    }

    private void setupView() {
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
                //if (mSummaryFragment == null)
                    mSummaryFragment = new SummaryFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, mSummaryFragment)
                        .commit();
                break;
            case 1:
                //if (mReviewFragment == null)
                    mReviewFragment = new ReviewFragment();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, mReviewFragment)
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
    public static class SummaryFragment extends ListFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_result_summary, container, false);

//            TextView txtSummary = (TextView)rootView.findViewById(R.id.txtSummary);
//
//            ExamResult result = ExamResult.newInstance(app.exam());
//
//            StringBuffer sb = new StringBuffer();
//            sb.append("<h4>Questions: ").append(result.total).append("</h4>")
//              .append("<h4>Score: ").append(result.correct).append("</h4>")
//              .append("<h4>Wrong: ").append(result.wrong).append("</h4>")
//              .append("<h4>Marks: ").append(result.markRatio()).append("%</h4>")
//              .append("<h4>Unanswered: ").append(result.unanswered).append("</h4>")
//              .append("<h4>Time spent: ").append(DateUtils.formatElapsedTime(result.elapsed)).append("</h4>");
//            txtSummary.setText(Html.fromHtml(sb.toString()));

            return rootView;
        }



        public void setupListView(){
            ExamResult result = ExamResult.newInstance(app.exam());
            String[] values = {
                    "Questions:" + result.total,
                    "Score:" + result.correct,
                    "Wrong:" + result.wrong,
                    "Marks:" + result.markRatio(),
                    "Unanswered:" + result.unanswered,
                    "Time spent:" + DateUtils.formatElapsedTime(result.elapsed)
            };

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    values
            );

            getListView().setAdapter(adapter);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setupListView();
        }
    }

    //---------------------------------------------------------------------
    // for review tab
    //---------------------------------------------------------------------
    public static class ReviewFragment extends ListFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_result_review, container, false);

            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            setupListView();
        }

        public void setupListView(){
            List<Question> questionList = MyApp.app.exam().questions;
            QuestionAdapter adapter = new QuestionAdapter(getActivity(), questionList);
            getListView().setAdapter(adapter);
        }
    }
}
