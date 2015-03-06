package com.spark.app.ocb.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * @author sunghun
 *
 */
@DatabaseTable(tableName="questions")
public class Question{

	@DatabaseField(id = true) public int id = -1;
	@DatabaseField public String question;
	@DatabaseField public String a;
	@DatabaseField public String b;
	@DatabaseField public String c;
	@DatabaseField public String rightAnswer;
	
	public Question(){}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Question [id=" + id + ", question=" + question + ", a=" + a
				+ ", b=" + b + ", c=" + c + ", rightAnswer=" + rightAnswer
				+ "]";
	}

	public static Question toObject(String jsonStr) throws JSONException{
		JSONObject json = new JSONObject(jsonStr);
		Question q = new Question();
		q.id = json.optInt("id");
		q.question = json.optString("q");
		q.a = json.optString("a");
		q.b = json.optString("b");
		q.c = json.optString("c");
		q.rightAnswer = json.optString("r_a");
		
		return q;
	}
	
	public static Question toObject(JSONObject json) throws JSONException{
		Question q = new Question();
		if (json==null)
			return q;
		
		q.id = json.optInt("id");
		q.question = json.optString("q");
		q.a = json.optString("a");//.replaceAll("\'", "\'\'");
		q.b = json.optString("b");//.replaceAll("\'", "\'\'");
		q.c = json.optString("c");//.replaceAll("\'", "\'\'");
		q.rightAnswer = json.optString("r_a");
		
		return q;
	}
	
	public static List<Question> loadFromJson(String jsonStr) throws JSONException{
		List<Question> list = new ArrayList<Question>();
		JSONObject jsonObj = new JSONObject(jsonStr);
		JSONArray jArray = jsonObj.optJSONArray("data");
		for (int i=0; i<jArray.length(); i++){
			JSONObject json = jArray.getJSONObject(i);
			list.add(Question.toObject(json));
		}
		
		return list;
	}

}
