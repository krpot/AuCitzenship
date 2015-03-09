package com.spark.app.ocb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunghun
 *
 */
//@DatabaseTable(tableName="choice")
public class Choice {

    //@DatabaseField(generatedId=true)
    int id = -1;

    //@DatabaseField
    public Question question;

    //@DatabaseField
    public int selected; //selected answer (0/1/2)

	public Choice(){}

    public Choice(Question question, int selected) {
        this.question = question;
        this.selected = selected;
    }

    public Choice(int id, Question question, int selected) {
        this.id = id;
        this.question = question;
        this.selected = selected;
    }

    /*
         *
         */
    public boolean isCorrect(){
        int i = -1;
        for (Answer answer: question.answers){
            i++;
            if (answer.correct) break;
        }

        return (i>0 && i == selected);
    }

	public static Choice toObject(String jsonStr) throws JSONException{
		JSONObject json = new JSONObject(jsonStr);
		Choice q = new Choice();
		//q.q = json.optString("q");
		//q.question = json.optString("a");
		//q.b = json.optString("b");
		//q.c = json.optString("c");
		//q.rightAnswer = json.optString("r_a");
		
		return q;
	}
	
	public static Choice toObject(JSONObject json) throws JSONException{
		Choice q = new Choice();
		if (json==null)
			return q;
		
		//q.q = q.loadFromJson(json.optString("q"));
		//q.question = json.optString("a");//.replaceAll("\'", "\'\'");
		//q.b = json.optString("b");//.replaceAll("\'", "\'\'");
		//q.c = json.optString("c");//.replaceAll("\'", "\'\'");
		//q.rightAnswer = json.optString("r_a");
		
		return q;
	}
	
	public static List<Choice> loadFromJson(String jsonStr) throws JSONException{
		List<Choice> list = new ArrayList<Choice>();
		JSONObject jsonObj = new JSONObject(jsonStr);
		JSONArray jArray = jsonObj.optJSONArray("data");
		for (int i=0; i<jArray.length(); i++){
			JSONObject json = jArray.getJSONObject(i);
			list.add(Choice.toObject(json));
		}
		
		return list;
	}

}
