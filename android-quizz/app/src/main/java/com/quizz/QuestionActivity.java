package com.quizz;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quizz.entities.Answer;
import com.quizz.entities.Question;
import com.quizz.entities.Round;

/**
 * Created by Florian on 31/01/2018.
 */

public class QuestionActivity extends Fragment {

    private int RESPONSE_TIME = 6000;
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
        this.time = MyApp.getContext().getResources().getInteger(R.integer.questionTime);
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
                if (getActivity() == null) {
                    return;
                }
                long elapsedTime = SystemClock.elapsedRealtime() - time;
                if (elapsedTime <= RESPONSE_TIME) {
                    ((ProgressBar) view.findViewById(R.id.progressBar)).setProgress((int) elapsedTime / (RESPONSE_TIME / 100));
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
        displayText((Button) view.findViewById(R.id.response1), question.getResponseA());
        displayText((Button) view.findViewById(R.id.response2), question.getResponseB());
        displayText((Button) view.findViewById(R.id.response3), question.getResponseC());
        displayText((Button) view.findViewById(R.id.response4), question.getResponseD());
        displayText((TextView) view.findViewById(R.id.questionText), question.getQuestion());
    }

    private void displayText(Button view, String text) {
        view.setVisibility(View.GONE);
        view.setText(text);
        view.setVisibility(View.VISIBLE);
    }

    private void displayText(TextView view, String text) {
        view.setVisibility(View.GONE);
        view.setText(text);
        view.setVisibility(View.VISIBLE);
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
