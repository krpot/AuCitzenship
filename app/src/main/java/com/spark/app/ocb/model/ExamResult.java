package com.spark.app.ocb.model;

import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;

/**
 * Created by spark on 3/10/15.
 */
public class ExamResult {

    public int total;
    public int correct;
    public int wrong;
    public int unanswered;

    private Exam exam;

    public ExamResult(){}

    public ExamResult(int total, int correct, int wrong, int unanswered){
        this.total = total;
        this.correct = correct;
        this.wrong = wrong;
        this.unanswered = unanswered;
    }

    public double marks() {
        if (total == 0)
            return 0;

        return correct/total;
    }

    public int markRatio() {
        return (int)(marks() * 100);
    }

    public static ExamResult newInstance(Exam exam){
        int total = exam.questions.size();
        int correct = 0;
        int unanswered = 0;
        int wrong = 0;

        for (Question question : exam.questions){
            if (question.selected<0)
                unanswered++;
            else {
                if (question.isCorrect())
                    correct++;
                else
                    wrong++;
            }
        }

        return new ExamResult(total, correct, wrong, unanswered);
    }
}


