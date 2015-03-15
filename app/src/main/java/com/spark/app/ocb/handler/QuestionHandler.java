package com.spark.app.ocb.handler;

import android.app.Activity;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.spark.app.ocb.entity.Answer;
import com.spark.app.ocb.entity.Question;

import java.util.List;

/**
 * Created by sunghun.
 */
public class QuestionHandler implements QuestionRender {

    Activity mActivity;
    RadioGroup mRadioGroup;
    RadioButton[] answerButtons;

    public QuestionHandler(Activity activity, RadioGroup radioGroup) {
        mActivity = activity;
        mRadioGroup = radioGroup;
    }

    @Override
    public void render(Question question) {
        List<Answer> answers = question.getAnswers();
        if (answers == null) return;

        if (answerButtons == null){
            answerButtons = new RadioButton[answers.size()];
            for (int i=0; i<answers.size(); i++){

                if (answerButtons[i] == null) {
                    answerButtons[i] = new RadioButton(mActivity);
                    answerButtons[i].setTag(i);
                    answerButtons[i].setOnCheckedChangeListener(mCheckedChangeListener);
                }

                Answer answer = question.getAnswer(i);
                answerButtons[i].setText(answer.answer);
            }
        }

        mRadioGroup.clearCheck();

    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!isChecked) return;

//            Question q = quizService.currentQuestion();
//            if (q == null) return;
//
//            RadioButton button = (RadioButton) buttonView;
//            int selected = (Integer) button.getTag();
//            quizService.selectAnswer(selected);
        };
    };
}
