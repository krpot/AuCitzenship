package com.spark.app.ocb.adpter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;

import java.util.List;

public class QuestionFilterAdapter extends BaseAdapter {

    private static final String TAG = "QuestionFilterAdapter";

    Context mContext;
    List<Question> mItems;
    boolean mShowAsList = false;
    int mPosition = -1;

	public QuestionFilterAdapter(Context context, List<Question> items) {
        this.mContext = context;
        this.mItems = items;
	}

    @Override
    public int getCount() {
        return mShowAsList ? mItems.size() : 1;
    }

    @Override
    public Question getItem(int position) {
        return mShowAsList ? mItems.get(position) : mItems.get(mPosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Question> items){
        this.mItems = items;
        notifyDataSetChanged();
    }

    public boolean getShowAsList(){
        return mShowAsList;
    }

    public void setShowAsList(boolean value){
        mShowAsList = value;
        notifyDataSetChanged();
    }

    public void toggleFiltered(){
        setShowAsList(!mShowAsList);
    }

    public int getPosition(){
        return mPosition;
    }

    public void setPosition(int position){
        mPosition = position;
        notifyDataSetChanged();
    }

    public void goToPrior(){
        int pos = mPosition;
        pos--;
        if (pos < 0){
            return;
        }

        setPosition(pos);
    }

    public void goToNext(){
        int pos = mPosition;
        pos++;
        if (pos > mItems.size()-1){
            return;
        }

        setPosition(pos);
    }

    public boolean hasMore(){
        return mPosition < mItems.size();
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

        final Question item = getItem(position);
        Log.d(TAG, "##### getItem / " + item);

        TextView txtTitle = (TextView)convertView.findViewById(android.R.id.text1);
        txtTitle.setTextColor(Color.BLACK);
        txtTitle.setBackground(null);
        //txtTitle.setTextSize(13);

        RadioGroup radioAnswer = (RadioGroup)convertView.findViewById(R.id.radioAnswer);
        radioAnswer.setBackground(null);
        radioAnswer.clearCheck();

        final RadioButton[] answerButtons = new RadioButton[3];
        answerButtons[0] = (RadioButton)convertView.findViewById(R.id.radioa);
        answerButtons[1] = (RadioButton)convertView.findViewById(R.id.radiob);
        answerButtons[2] = (RadioButton)convertView.findViewById(R.id.radioc);

        if (item != null) {
            for (RadioButton b: answerButtons) {
                b.setText("");
                b.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (!isChecked || item == null) return;
                        RadioButton button = (RadioButton) buttonView;
                        item.selected = (Integer) button.getTag();
                        answerButtons[item.selected].setChecked(true);
                    }
                });
            }

            String title = item.id + ". " + item.statement;
            txtTitle.setText(title);

            List<Answer> answers = item.getAnswers();

            if (item.selected>=0 && item.selected<answers.size()) {
                answerButtons[item.selected].setChecked(true);
            }

            int i=0;
            for (Answer answer : answers){
                if (i>=answerButtons.length) break;
                RadioButton radioButton = answerButtons[i++];
                String s = answer.answer;
                radioButton.setText(s);
            }
        }
			
		return convertView;
	}

    public static interface QuizViewFilter<T> {
        public boolean filter(int index, T t);
    }

}
