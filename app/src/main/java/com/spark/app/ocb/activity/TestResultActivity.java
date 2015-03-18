package com.spark.app.ocb.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.MyApp;
import com.spark.app.ocb.R;
import com.spark.app.ocb.adpter.QuestionAdapter;
import com.spark.app.ocb.adpter.TestResultAdapter;
import com.spark.app.ocb.entity.Question;
import com.spark.app.ocb.model.ExamResult;

import java.util.ArrayList;
import java.util.List;

import static com.spark.app.ocb.AppConstants.KEY_RESULT_CORRECT;
import static com.spark.app.ocb.AppConstants.KEY_RESULT_MARKS;
import static com.spark.app.ocb.AppConstants.KEY_RESULT_MISSING;
import static com.spark.app.ocb.AppConstants.KEY_RESULT_TIME;
import static com.spark.app.ocb.AppConstants.KEY_RESULT_TOTAL;
import static com.spark.app.ocb.AppConstants.KEY_RESULT_WRONG;
import static com.spark.app.ocb.MyApp.app;
import static java.util.AbstractMap.SimpleEntry;


public class TestResultActivity extends FragmentActivity {

    private static final String TAG = "TestResultActivity";

    private static final String FR_SUMMARY = "FR_SUMMARY";
    private static final String FR_REVIEW  = "FR_REVIEW";

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mReviewFragment == null || mSummaryFragment.isVisible()){
                    setResult(Activity.RESULT_CANCELED);
                    this.finish();
                } else {
//                    getSupportFragmentManager()
//                            .beginTransaction()
//                            .replace(R.id.container, mSummaryFragment, FR_SUMMARY)
//                            .commit();
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.addTab(actionBar.newTab().setText("Questions").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Summary").setTabListener(tabListener));

//        mSummaryFragment = new SummaryFragment();
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, mSummaryFragment, FR_SUMMARY)
//                .commit();

    }

    private void showReviewFragment(int position){
        if (position>3) return;

        //if (mReviewFragment == null)
//        mReviewFragment = ReviewFragment.newInstance(position);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, mReviewFragment, FR_REVIEW)
//                .commit();
        //else
            //mReviewFragment.setupListView(position);

        /*
        switch (position){
            //KEY_RESULT_TOTAL
            case 0:
            //KEY_RESULT_CORRECT
            case 1:
            //KEY_RESULT_WRONG
            case 2:
            //KEY_RESULT_MISSING
            case 3:

                break;
            //KEY_RESULT_MARKS
            case 4:
            //KEY_RESULT_TIME
            case 5:
                return;
        }
        */
    }

    /*
     * Tab listener
     */
    private ActionBar.TabListener tabListener = new ActionBar.TabListener(){
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            Fragment fragment;
            switch (tab.getPosition()){
                case 0:
                    if (mReviewFragment == null)
                        mReviewFragment = ReviewFragment.newInstance();

                    fragment = mReviewFragment;
//                    getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.container, mReviewFragment, FR_REVIEW)
//                            .commit();
                    break;
                case 1:
                    if (mSummaryFragment==null)
                        mSummaryFragment = new SummaryFragment();
                    fragment = mSummaryFragment;
                    break;
                default:
                    return;
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();

        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }
    };

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

            //TestResultActivity activity = (TestResultActivity)getActivity();
            //activity.showReviewFragment(position);
        }

        private void setupListView(){
            ExamResult result = ExamResult.newInstance(app.exam());

            List<SimpleEntry<String, String>> items = new ArrayList<SimpleEntry<String, String>>();
            items.add(new SimpleEntry(KEY_RESULT_TOTAL, String.valueOf(result.total)));
            items.add(new SimpleEntry(KEY_RESULT_CORRECT, String.valueOf(result.correct)));
            items.add(new SimpleEntry(KEY_RESULT_WRONG, String.valueOf(result.wrong)));
            items.add(new SimpleEntry(KEY_RESULT_MISSING, String.valueOf(result.unanswered)));
            items.add(new SimpleEntry(KEY_RESULT_TIME, DateUtils.formatElapsedTime(result.elapsed)));
            items.add(new SimpleEntry(KEY_RESULT_MARKS, result.markRatio() + "%"));

            adapter = new TestResultAdapter(getActivity(), items);
            getListView().setAdapter(adapter);
        }

    }

    //---------------------------------------------------------------------
    // for review
    //---------------------------------------------------------------------
    public static class ReviewFragment extends ListFragment {

        static int mOption;
        public static ReviewFragment newInstance(){
            //mOption = option;
            return new ReviewFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_result_review, container, false);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setupListView(0);
            //setupListView(mOption);
        }

        public void setupListView(int option){

            List<Question> subList = null;
            List<Question> questionList = MyApp.app.exam().questions;
            for (int i=0, sz=questionList.size(); i<sz; i++){
                questionList.get(i).id = i+1;
            }

            if (option ==0) {
                subList = questionList;
            } else {
                subList = new ArrayList<Question>();
                //!KEY_RESULT_TOTAL
                for (Question q : questionList) {
                    switch (option) {
                        //KEY_RESULT_CORRECT
                        case 1:
                            if (q.isCorrect()) subList.add(q);
                            break;

                        //KEY_RESULT_WRONG
                        case 2:
                            if (!q.isCorrect()) subList.add(q);
                            break;

                        //KEY_RESULT_MISSING
                        case 3:
                            if (q.selected<0) subList.add(q);
                            break;
                    }
                }
            }

            QuestionAdapter adapter = new QuestionAdapter(getActivity(), subList);
            getListView().setAdapter(adapter);
        }

    }
}
