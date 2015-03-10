package com.spark.app.ocb.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Question;

public class TestIntroActivity extends Activity {
	
	private Dao<Question, Integer> mQDao = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_intro);
		
		setupView();
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}
	
	private void setupView(){
		TextView txtIntro = (TextView)findViewById(R.id.txtIntro);
        txtIntro.setText(Html.fromHtml(getString(R.string.test_intro)));
	}

    /**
     * Click event handler for buttons
     * @param view
     */
    public void onButtonClick(View view){
        switch (view.getId()){
            case R.id.btnStart:
                setResult(RESULT_OK);
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                break;
        }

        this.finish();
    }



}
