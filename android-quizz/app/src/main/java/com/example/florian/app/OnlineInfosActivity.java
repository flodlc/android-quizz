package com.example.florian.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Florian on 01/02/2018.
 */

public class OnlineInfosActivity extends Fragment {

    private int round;
    private String adv;
    private int score;

    public static OnlineInfosActivity newInstance(Bundle args) {
        OnlineInfosActivity f = new OnlineInfosActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_selector, container, false);

        Bundle b = getArguments();
        this.round = 0;
        this.score = 0;
        this.adv = b.getString("adv");

        this.DisplayTexts();
        return view;
    }

    private void DisplayTexts() {
        ((TextView) getView().findViewById(R.id.score)).setText(score + "/" + (round - 1));
        ((TextView) getView().findViewById(R.id.advName)).setText(adv);
        ((TextView) getView().findViewById(R.id.questionNumber)).setText(String.valueOf(round));
    }

    public void Update(int round, int score) {
        this.round = round;
        this.score = score;
        this.DisplayTexts();
    }
}
