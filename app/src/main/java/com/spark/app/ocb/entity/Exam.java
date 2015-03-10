package com.spark.app.ocb.entity;

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

    public String getInStatement(){
        String s = "";
        for (Question question : questions)
            s += s.length()==0 ? question.id : "," + question.id;

        return s;
    }

    public void setSelected(Question question, int value){
        int location = questions.indexOf(question);
        if (location>-1 || location<=questions.size()-1) {
            question.selected = value;
            questions.set(location, question);
        }
    }

    public int correct(){
        int n = 0;
        for (Question question : questions){
            if (question.isCorrect())
                n++;
        }

        return n;
    }

    public double marks() {
        int sz = questions.size();
        if (sz == 0)
            return 0;

        return correct()/sz;
    }

    public double markRatio() {
        return Math.floor(marks() * 100);
    }


}
