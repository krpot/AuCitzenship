package com.spark.app.ocb.adpter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import static java.util.AbstractMap.SimpleEntry;

public class TestResultAdapter extends BaseAdapter {

    private static final String TAG = "TestResultAdapter";

    Context mContext;
    List<SimpleEntry<String, String>> mItems = null;

	public TestResultAdapter(Context context, List<SimpleEntry<String, String>> items) {
        mContext = context;
        mItems = items;
	}

    @Override
    public int getCount() {
        return mItems != null ? mItems.size() : 0;
    }

    @Override
    public SimpleEntry<String, String> getItem(int position) {
        return mItems != null ? mItems.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<SimpleEntry<String, String>> items){
        this.mItems = items;
        notifyDataSetChanged();
    }

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

            viewHolder = new ViewHolder(
                    (TextView)convertView.findViewById(android.R.id.text1),
                    (TextView)convertView.findViewById(android.R.id.text2));
            convertView.setTag(viewHolder);
		} else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SimpleEntry<String, String> item = getItem(position);
        Log.d(TAG, "##### getView / " + item);

        viewHolder.text(item.getKey(), item.getValue());

		return convertView;
	}

}
