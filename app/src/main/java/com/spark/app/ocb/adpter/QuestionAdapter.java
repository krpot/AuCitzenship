package com.spark.app.ocb.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.spark.app.ocb.R;
import com.spark.app.ocb.entity.Question;

import java.util.List;

public class QuestionAdapter extends ArrayAdapter<Question> {

	public QuestionAdapter(Context context, List<Question> items) {
		super(context, R.layout.question_item, items);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.question_item, parent, false);
		}
		
		Question item = getItem(position);
		
		TextView title = (TextView) convertView.findViewById(android.R.id.text1);
		title.setText(item.question);
		title.setTag(item);
		
		//RadioGroup radAnswer = (RaidoGroup)findViewById(R.id.radioAnswer);
		RadioButton radioA = (RadioButton)convertView.findViewById(R.id.radioa);
		RadioButton radioB = (RadioButton)convertView.findViewById(R.id.radiob);
		RadioButton radioC = (RadioButton)convertView.findViewById(R.id.radioc);
		
		radioA.setText(item.a);
		radioB.setText(item.b);
		radioC.setText(item.c);
			
		return convertView;
	}
	
	

}
