package com.example.florian.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import entities.Answer;
import entities.Question;
import entities.Round;

/**
 * Created by Florian on 31/01/2018.
 */

public class QuestionActivity extends Fragment {

    private Round round;

    public static QuestionActivity newInstance(Bundle args) {
        QuestionActivity f = new QuestionActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_selector, container, false);
        Bundle b = getArguments();
        this.round = b.getParcelable("question");
        this.displayTexts();
        this.setListenners();
        return view;
    }

    public void update(Round round) {
        this.round = round;
        this.displayTexts();
    }

    public void displayTexts() {
        Question question = round.getQuestion();
        ((TextView) getView().findViewById(R.id.questionText)).setText(question.getQuestion());
        ((Button) getView().findViewById(R.id.response1)).setText(question.getResponseA());
        ((Button) getView().findViewById(R.id.response2)).setText(question.getResponseB());
        ((Button) getView().findViewById(R.id.response3)).setText(question.getResponseC());
        ((Button) getView().findViewById(R.id.response4)).setText(question.getResponseD());
    }

    private void setListenners() {
        Question question = round.getQuestion();
        setListenner(getView().findViewById(R.id.response1), question.getResponseA());
        setListenner(getView().findViewById(R.id.response2), question.getResponseB());
        setListenner(getView().findViewById(R.id.response3), question.getResponseC());
        setListenner(getView().findViewById(R.id.response4), question.getResponseD());
    }

    private void setListenner(View view, final String responseId) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((QuizzActivityInterface) getActivity().getParent()).saveAnswer(new Answer(round.getId(), responseId));
            }
        });
    }
}
