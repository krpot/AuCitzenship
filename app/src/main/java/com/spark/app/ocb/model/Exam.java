package com.spark.app.ocb.model;

import com.spark.app.ocb.entity.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sunghun
 *
 */
public class Exam {

    public List<Question> questions = new ArrayList<Question>();
    public long elapsed = 0L;

	public Exam(){}

    public void clear(){
        questions.clear();
        elapsed = 0;
    }

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
