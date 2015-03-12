package com.spark.app.ocb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author sunghun
 *
 */
@DatabaseTable(tableName="questions")
public class Question{

    @DatabaseField(generatedId = true)
    public int id = -1;             //question Id

    @DatabaseField
    public String statement;        //question statement

    @DatabaseField
    public String tag;              //tag strings

    @DatabaseField
    public String reference;        //reference verse Id in the book eg.01010101 - first part, first chapter, first unit, first verse

    @ForeignCollectionField
    public Collection<Answer> answers = new ArrayList<Answer>();

    public int selected = -1;

	public Question(){}

    public Answer getAnswer(int i){
        if (i<0 || i>answers.size()-1) return null;

        ArrayList<Answer> list = (ArrayList<Answer>) answers;
        return list.get(i);
    }

    public void shuffle(){
        long seed = System.nanoTime();
        ArrayList<Answer> list = (ArrayList<Answer>) answers;
        Collections.shuffle(list, new Random(seed));
    }

    public boolean answered(){
        return this.selected > -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (question.id<0 || this.id<0) return false;

        return question.id == this.id;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", tag='" + tag + '\'' +
                ", statement='" + statement + '\'' +
                ", selected='" + selected + '\'' +
                ", answers=" + (answers != null ? answers.size(): 0) +
                '}';
    }

	public static Question toObject(JSONObject json) throws JSONException{
		Question q = new Question();
		if (json==null)
			return q;
		
		//q.id = json.optInt("id");
		q.statement = json.optString("q");
        q.tag       = json.optString("t");
        JSONArray jAnswers = json.optJSONArray("a");
        for (int i=0; i<jAnswers.length(); i++) {
            Answer a = Answer.toObject(jAnswers.getJSONObject(i), q);
            q.answers.add(a);
        }
		return q;
	}
	
	public static List<Question> loadFromJson(String jsonStr) throws JSONException{
		List<Question> list = new ArrayList<Question>();
		JSONArray jArray = new JSONArray(jsonStr);
		for (int i=0; i<jArray.length(); i++){
			JSONObject json = jArray.getJSONObject(i);
			list.add(Question.toObject(json));
		}
		
		return list;
	}

    public boolean isValidSelected(){
        return selected>=0 && selected<this.answers.size();
    }

    public boolean isCorrect(){
        if (isValidSelected()){
            return getAnswers().get(selected).correct;
        }

        return false;
    }

    public ArrayList<Answer> getAnswers(){
        return (ArrayList<Answer>) answers;
    }
}
