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
import android.widget.ProgressBar;
import android.widget.TextView;

import entities.Answer;
import entities.Question;
import entities.Round;

/**
 * Created by Florian on 31/01/2018.
 */

public class QuestionActivity extends Fragment {

    private Round round;
    private Handler handler;
    private Runnable runnable;
    private long time;

    public static QuestionActivity newInstance(Bundle args) {
        QuestionActivity f = new QuestionActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        Bundle b = getArguments();
        this.round = b.getParcelable("round");
        this.displayTexts(view);
        this.setListenners(view);
        this.setTimer(view);
        return view;
    }

    public void update(Round round) {
        this.round = round;
        this.displayTexts();
        time = SystemClock.elapsedRealtime();
        handler.postDelayed(runnable, 50);
    }

    private void setTimer(final View view) {
        time = SystemClock.elapsedRealtime();
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                long elapsedTime = SystemClock.elapsedRealtime() - time;
                if (elapsedTime <= 5000) {
                    ((ProgressBar) view.findViewById(R.id.progressBar)).setProgress((int) elapsedTime / 50);
                    handler.postDelayed(this, 50);
                } else {
                    handler.removeCallbacks(runnable);
                    if (getActivity() != null) {
                        ((QuizzActivityInterface) getActivity()).saveAnswer(new Answer(round.getId(), ""));
                    }
                }
            }
        };
        handler.postDelayed(runnable, 50);
    }

    private void displayTexts() {
        this.displayTexts(getView());
    }

    private void displayTexts(View view) {
        Question question = round.getQuestion();
        ((TextView) view.findViewById(R.id.questionText)).setText(question.getQuestion());
        ((Button) view.findViewById(R.id.response1)).setText(question.getResponseA());
        ((Button) view.findViewById(R.id.response2)).setText(question.getResponseB());
        ((Button) view.findViewById(R.id.response3)).setText(question.getResponseC());
        ((Button) view.findViewById(R.id.response4)).setText(question.getResponseD());
    }

    private void setListenners(View view) {
        setListenner(view.findViewById(R.id.response1), "ResponseA");
        setListenner(view.findViewById(R.id.response2), "ResponseB");
        setListenner(view.findViewById(R.id.response3), "ResponseC");
        setListenner(view.findViewById(R.id.response4), "ResponseD");
    }

    private void setListenner(View view, final String responseId) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                ((QuizzActivityInterface) getActivity()).saveAnswer(new Answer(round.getId(), responseId));
            }
        });
    }
}
