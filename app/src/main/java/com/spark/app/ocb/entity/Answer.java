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
    public int id=-1;

    @DatabaseField
    public String answer = "";

    @DatabaseField
    public boolean correct = false;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "question_id")
    public Question question;

    public Answer(){}

    public Answer(String answer, boolean correct, Question question) {
        this.answer = answer;
        this.correct = correct;
        this.question = question;
    }

    public Answer(int id, String answer, boolean correct, Question question) {
        this.id = id;
        this.answer = answer;
        this.correct = correct;
        this.question = question;
    }

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
        return toObject(json, new Question());
    }

    public static Answer toObject(JSONObject json, Question question) throws JSONException {
        Answer answer = new Answer();
        if (json==null)
            return answer;

        answer.answer = json.optString("answer");
        answer.correct = json.optBoolean("correct");
        answer.question = question;
        return answer;
    }
}
