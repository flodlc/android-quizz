package com.example.florian.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


    private void displayTexts(View view) {
        Question question = round.getQuestion();
        ((TextView) view.findViewById(R.id.questionText)).setText(question.getQuestion());
        ((Button) view.findViewById(R.id.response1)).setText(question.getResponseA());
        ((Button) view.findViewById(R.id.response2)).setText(question.getResponseB());
        ((Button) view.findViewById(R.id.response3)).setText(question.getResponseC());
        ((Button) view.findViewById(R.id.response4)).setText(question.getResponseD());
    }
}
