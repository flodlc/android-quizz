package com.example.florian.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import entities.Question;
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
        View view = inflater.inflate(R.layout.fragment_question, container, false);
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

    private void displayTexts(View view) {
        Question question = round.getQuestion();
        ((TextView) view.findViewById(R.id.questionText)).setText(question.getQuestion());
        ((TextView) view.findViewById(R.id.correctAnswer)).setText(question.getAnswer());
        setImage((ImageView) view.findViewById(R.id.answerA), R.drawable.thumb_up);
    }
}
