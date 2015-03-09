package com.spark.app.ocb.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spark
 */
@DatabaseTable(tableName="answers")
public class Answer {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField
    public String answer = "";

    @DatabaseField
    public boolean correct = false;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "question_id")
    public Question question;

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", answer='" + answer + '\'' +
                ", correct=" + correct + '\'' +
                ", question_id=" + (question != null ? question.id : null) +
                '}';
    }

    public static Answer toObject(JSONObject json) throws JSONException {
        Answer answer = new Answer();
        if (json==null)
            return answer;

        //answer.id = i;
        answer.answer = json.optString("answer");
        answer.correct = json.optBoolean("correct");
        answer.question = new Question();

        //q.a = json.optString("a");//.replaceAll("\'", "\'\'");
        //q.b = json.optString("b");//.replaceAll("\'", "\'\'");
        //q.c = json.optString("c");//.replaceAll("\'", "\'\'");
        //q.rightAnswer = json.optString("r_a");

        return answer;
    }
}
