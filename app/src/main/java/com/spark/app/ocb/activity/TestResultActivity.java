package com.spark.app.ocb.activity;

import static com.spark.app.ocb.MyApp.app;
import static java.util.AbstractMap.SimpleEntry;
import static com.spark.app.ocb.AppConstants.*;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NavUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.AppConstants;
import com.spark.app.ocb.MyApp;
import com.spark.app.ocb.R;
import com.spark.app.ocb.adpter.QuestionAdapter;
import com.spark.app.ocb.adpter.TestResultAdapter;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.ExamResult;

import java.util.ArrayList;
import java.util.List;


public class TestResultActivity extends FragmentActivity {

    private static final String TAG = "TestResultActivity";

    private static final String FR_SUMMARY = "FR_SUMMARY";
    private static final String FR_REVIEW  = "FR_REVIEW";

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
                if (mReviewFragment == null || mSummaryFragment.isVisible()){
                    setResult(Activity.RESULT_CANCELED);
                    this.finish();
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, mSummaryFragment, FR_SUMMARY)
                            .commit();
                }
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
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSummaryFragment = new SummaryFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mSummaryFragment, FR_SUMMARY)
                .commit();

    }

    private void showReviewFragment(int position){
        if (mReviewFragment == null)
            mReviewFragment = new ReviewFragment();

        switch (position){
            //KEY_RESULT_TOTAL
            case 0:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mReviewFragment, FR_REVIEW)
                        .commit();
                break;
            //KEY_RESULT_CORRECT
            case 1:
                break;
            //KEY_RESULT_WRONG
            case 2:
                break;
            //KEY_RESULT_MISSING
            case 3:
                break;
            //KEY_RESULT_MARKS
            case 4:
                break;
            //KEY_RESULT_TIME
            case 5:
                break;
        }
    }

    //---------------------------------------------------------------------
    // for summary
    //---------------------------------------------------------------------
    public static class SummaryFragment extends ListFragment {

        TestResultAdapter adapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_result_summary, container, false);
        }
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setupListView();
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            //SimpleEntry<String, String> item = adapter.getItem(position);

            TestResultActivity activity = (TestResultActivity)getActivity();
            activity.showReviewFragment(position);
        }

        public void setupListView(){
            ExamResult result = ExamResult.newInstance(app.exam());

            List<SimpleEntry<String, String>> items = new ArrayList<SimpleEntry<String, String>>();
            items.add(new SimpleEntry(KEY_RESULT_TOTAL, String.valueOf(result.total)));
            items.add(new SimpleEntry(KEY_RESULT_CORRECT, String.valueOf(result.correct)));
            items.add(new SimpleEntry(KEY_RESULT_WRONG, String.valueOf(result.wrong)));
            items.add(new SimpleEntry(KEY_RESULT_MISSING, String.valueOf(result.unanswered)));
            items.add(new SimpleEntry(KEY_RESULT_MARKS, String.valueOf(result.markRatio())));
            items.add(new SimpleEntry(KEY_RESULT_TIME, DateUtils.formatElapsedTime(result.elapsed)));

            adapter = new TestResultAdapter(getActivity(), items);
            getListView().setAdapter(adapter);
        }

    }

    //---------------------------------------------------------------------
    // for review
    //---------------------------------------------------------------------
    public static class ReviewFragment extends ListFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_result_review, container, false);
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
