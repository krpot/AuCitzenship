package com.spark.app.ocb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author sunghun
 *
 */
@DatabaseTable(tableName="questions")
public class Question{

    @DatabaseField(generatedId = true)
    public int id = -1;

    @DatabaseField
    public String statement;

    @ForeignCollectionField
    public Collection<Answer> answers = new ArrayList<Answer>();

	public Question(){}

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", statement='" + statement + '\'' +
                ", answers=" + (answers != null ? answers.size(): 0) +
                '}';
    }

	public static Question toObject(JSONObject json) throws JSONException{
		Question q = new Question();
		if (json==null)
			return q;
		
		//q.id = json.optInt("id");
		q.statement = json.optString("statement");
        JSONArray jAnswers = json.optJSONArray("answers");
        for (int i=0; i<jAnswers.length(); i++) {
            Answer a = Answer.toObject(jAnswers.getJSONObject(i));
            q.answers.add(a);
        }
		return q;
	}
	
	public static List<Question> loadFromJson(String jsonStr) throws JSONException{
		List<Question> list = new ArrayList<Question>();
		//JSONObject jsonObj = new JSONObject(jsonStr);
		JSONArray jArray = new JSONArray(jsonStr);
		for (int i=0; i<jArray.length(); i++){
			JSONObject json = jArray.getJSONObject(i);
			list.add(Question.toObject(json));
		}
		
		return list;
	}

}
