package com.spark.app.ocb.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunghun
 *
 */
public class Exam {

	public int id = -1;
    public List<Choice> choices = new ArrayList<Choice>();

	public Exam(){}


    public int correct(){
        int n = 0;
        for (Choice choice : choices){
            if (choice.isCorrect())
                n++;
        }

        return n;
    }

    public double marks() {
        int sz = choices.size();
        if (sz == 0)
            return 0;

        return correct()/sz;
    }

    public double markRatio() {
        return Math.floor(marks() * 100);
    }

	public static Exam toObject(String jsonStr) throws JSONException{
		JSONObject json = new JSONObject(jsonStr);
		Exam q = new Exam();
		//q.q = json.optString("q");
		//q.b = json.optString("b");
		//q.c = json.optString("c");
		//q.rightAnswer = json.optString("r_a");
		
		return q;
	}
	
	public static Exam toObject(JSONObject json) throws JSONException{
		Exam q = new Exam();
		if (json==null)
			return q;
		
		q.id = json.optInt("id");
		//q.q = q.loadFromJson(json.optString("q"));
		//q.b = json.optString("b");//.replaceAll("\'", "\'\'");
		//q.c = json.optString("c");//.replaceAll("\'", "\'\'");
		//q.rightAnswer = json.optString("r_a");
		
		return q;
	}
	
	public static List<Exam> loadFromJson(String jsonStr) throws JSONException{
		List<Exam> list = new ArrayList<Exam>();
		JSONObject jsonObj = new JSONObject(jsonStr);
		JSONArray jArray = jsonObj.optJSONArray("data");
		for (int i=0; i<jArray.length(); i++){
			JSONObject json = jArray.getJSONObject(i);
			list.add(Exam.toObject(json));
		}
		
		return list;
	}

}
