package com.spark.app.ocb.model;

import com.spark.app.ocb.entity.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunghun
 *
 */
public class Exam {

	public int id = -1;
    public List<Question> questions = new ArrayList<Question>();

	public Exam(){}

    public boolean addQuestion(Question question){
        if (!questions.contains(question)) {
            return questions.add(question);
        }

        return false;
    }

    public void setSelected(Question question, int value){
        int location = questions.indexOf(question);
        if (location>-1 || location<=questions.size()-1) {
            question.selected = value;
            questions.set(location, question);
        }
    }
}
