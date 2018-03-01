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

import java.util.Random;

/**
 * Created by Florian on 31/01/2018.
 */

public class QuestionActivity extends Fragment {

    private int responseTime;
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
        responseTime = getActivity().getResources().getInteger(R.integer.questionTime);
        Bundle b = getArguments();
        this.round = b.getParcelable("round");
        this.displayTexts(view);
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
                if (elapsedTime <= responseTime) {
                    ((ProgressBar) view.findViewById(R.id.progressBar)).setProgress((int) elapsedTime / (responseTime / 100));
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

    private Button[] getButtons(View view) {
        Button[] buttons = new Button[4];
        buttons[0] = view.findViewById(R.id.response1);
        buttons[1] = view.findViewById(R.id.response2);
        buttons[2] = view.findViewById(R.id.response3);
        buttons[3] = view.findViewById(R.id.response4);
        return randomizeArray(buttons);
    }

    private void displayTexts(View view) {
        Question question = round.getQuestion();
        Button[] randomButtons = getButtons(view);

        displayText(randomButtons[0], question.getResponseA());
        setListenner(randomButtons[0], "ResponseA");

        displayText(randomButtons[1], question.getResponseB());
        setListenner(randomButtons[1], "ResponseB");

        displayText(randomButtons[2], question.getResponseC());
        setListenner(randomButtons[2], "ResponseC");

        displayText(randomButtons[3], question.getResponseD());
        setListenner(randomButtons[3], "ResponseD");

        displayText((TextView) view.findViewById(R.id.questionText), question.getQuestion());
    }

    private static Button[] randomizeArray(Button[] array) {
        Random rgen = new Random();  // Random number generator

        for (int i = 0; i < array.length; i++) {
            int randomPosition = rgen.nextInt(array.length);
            Button temp = array[i];
            array[i] = array[randomPosition];
            array[randomPosition] = temp;
        }
        return array;
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


    private void setListenner(View view, final String responseId) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                ((QuizzActivityInterface) getActivity()).saveAnswer(new Answer(round.getId(), responseId));
            }
        });
    }
}
