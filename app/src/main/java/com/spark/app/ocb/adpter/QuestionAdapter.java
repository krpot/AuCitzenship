package com.spark.app.ocb.adpter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;

import java.util.List;

public class QuestionAdapter extends BaseAdapter {

    private static final String TAG = "QuestionAdapter";

    Context mContext;
    List<Question> mItems = null;

	public QuestionAdapter(Context context, List<Question> items) {
        mContext = context;
        mItems = items;
	}

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public Question getItem(int position) {
        return mItems != null ? mItems.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Question> items){
        this.mItems = items;
        notifyDataSetChanged();
    }

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.question_item, parent, false);
		}

        TextView txtTitle = (TextView)convertView.findViewById(android.R.id.text1);
        txtTitle.setBackground(null);
        txtTitle.setTextSize(13);

        RadioGroup radioAnswer = (RadioGroup)convertView.findViewById(R.id.radioAnswer);
        radioAnswer.setBackground(null);

        RadioButton[] answerButtons = new RadioButton[3];
        answerButtons[0] = (RadioButton)convertView.findViewById(R.id.radioa);
        answerButtons[1] = (RadioButton)convertView.findViewById(R.id.radiob);
        answerButtons[2] = (RadioButton)convertView.findViewById(R.id.radioc);

        txtTitle.setText("");
        for (RadioButton b: answerButtons) {
            b.setText("");
        }

        Question item = getItem(position);
        Log.d(TAG, "##### getItem / " + item);
        if (item != null) {


            String title = item.id + ". " + item.statement;
            if (item.isCorrect()) {
                txtTitle.setText(title + " (O)");
            }
            else {
                txtTitle.setText(title + " (X)");
                txtTitle.setTextColor(Color.RED);
            }

            for (RadioButton b: answerButtons){
                b.setEnabled(false);
                b.setTextSize(11);
            }

            List<Answer> answers = item.getAnswers();

            if (item.selected>=0 && item.selected<answers.size()) {
                answerButtons[item.selected].setChecked(true);
            }

            //if (answers != null) {
                //Log.d(TAG, "##### AnswerSize / " + answers.size() + ", ##### item.selected / " + item.selected);

                int i=0;
                for (Answer answer : answers){
                    //Log.d(TAG, "##### question.answer:" + answer);
                    if (i>=answerButtons.length) break;

                    RadioButton radioButton = answerButtons[i++];
                    //RadioButton rb = new RadioButton(mContext);
                    //radioAnswer.addView(rb, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    String s = answer.answer;
                    if (answer.correct)
                        s += " (O)";
                    radioButton.setText(s);
                }
            //}
        }
			
		return convertView;
	}
	
	

}
