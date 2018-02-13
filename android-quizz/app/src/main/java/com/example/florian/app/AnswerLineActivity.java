package com.example.florian.app;

import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import entities.Question;
import entities.Response;
import entities.Round;

/**
 * Created by Florian on 31/01/2018.
 */

public class AnswerLineActivity extends Fragment {

    private Round round;

    public static AnswerLineActivity newInstance(Bundle args) {
        AnswerLineActivity f = new AnswerLineActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_answer_line, container, false);
        Bundle b = getArguments();
        this.round = b.getParcelable("round");
        this.displayTexts(view);
        return view;
    }

    private void setBakgroundColor(View view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getActivity().getColor(color));
        } else {
            view.findViewById(R.id.answerA).setBackgroundColor(getResources().getColor(color));
        }
    }

    private void setImage(ImageView imageView, int imageId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(getActivity().getDrawable(imageId));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(imageId));
        }
    }

    private void setCorrect(ImageView view, boolean isCorrect) {
        if (isCorrect) {
            setImage(view, R.drawable.thumb_up);
            setBakgroundColor(view, R.color.colorGreen);
        } else {
            setImage(view, R.drawable.thumb_down);
            setBakgroundColor(view, R.color.colorRed);
        }

    }

    private String getAnswerText(Question question) {
        switch (question.getAnswer()) {
            case "ResponseA":
                return question.getResponseA();
            case "ResponseB":
                return question.getResponseB();
            case "ResponseC":
                return question.getResponseC();
            default:
                return question.getResponseD();
        }
    }

    private void setAnimation(ImageView imageView, Response responseUser, String answer) {
        if (responseUser != null) {
            setCorrect(imageView, responseUser.getResponse().equals(answer));
        } else {
            setImage(imageView, R.drawable.sablier);
            setBakgroundColor(imageView, R.color.colorGrey);
        }
    }

    private void displayTexts(View view) {
        Question question = round.getQuestion();
        ((TextView) view.findViewById(R.id.questionText)).setText(question.getQuestion());
        ((TextView) view.findViewById(R.id.correctAnswer)).setText(getAnswerText(question));

        setAnimation((ImageView) view.findViewById(R.id.answerA),
                round.getResponseUA(), question.getAnswer());

        setAnimation((ImageView) view.findViewById(R.id.answerB),
                round.getResponseUB(), question.getAnswer());
    }
}
