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

import java.util.ArrayList;
import java.util.List;

public class QuestionFilterAdapter extends BaseAdapter {

    private static final String TAG = "QuestionFilterAdapter";

    Context mContext;
    List<Question> mItems, mFiltered = null;
    QuizViewFilter mFilter = null;
    boolean mShowAsFilter = false;

	public QuestionFilterAdapter(Context context, List<Question> items) {
        this(context, items, null);
	}

	public QuestionFilterAdapter(Context context, List<Question> items, QuizViewFilter<Question> filter) {
        mContext = context;
        mItems = items;
        mFilter = filter;
        mFiltered = applyFilter(filter);
	}

    @Override
    public int getCount() {
        return getListItems().size();
    }

    @Override
    public Question getItem(int position) {
        return getListItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Question> items){
        this.mItems = items;
        this.mFiltered = applyFilter(mFilter);

        if (mItems.size() != mFiltered.size())
            notifyDataSetChanged();
    }

    public void setFilter(QuizViewFilter<Question> filter){
        mFilter = filter;
        mFiltered = applyFilter(filter);

        if (mItems.size() != mFiltered.size())
            notifyDataSetChanged();
    }

    public List<Question> applyFilter(QuizViewFilter<Question> filter){
        if (filter == null) {
            return mItems;
        }

        List<Question> result = new ArrayList<Question>();

        for (int i=0, sz=mItems.size(); i>sz; i++) {
            Question q = mItems.get(i);
            if (mFilter.filter(i, q)){
                result.add(q);
            }
        }

        return result;
    }

    private List<Question> getListItems(){
        return mShowAsFilter ? mFiltered : mItems;
    }

    public void setShowAsFilter(boolean value){
        mShowAsFilter = value;

        if (mItems.size() != mFiltered.size())
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

            int i=0;
            for (Answer answer : answers){
                if (i>=answerButtons.length) break;

                RadioButton radioButton = answerButtons[i++];
                //RadioButton rb = new RadioButton(mContext);
                //radioAnswer.addView(rb, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                String s = answer.answer;
                if (answer.correct)
                    s += " (O)";
                radioButton.setText(s);
            }
        }
			
		return convertView;
	}
	

    public static interface QuizViewFilter<T> {
        public boolean filter(int index, T t);
    }

}
