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
        View view = inflater.inflate(R.layout.fragment_online_infos, container, false);

        Bundle b = getArguments();
        this.round = 1;
        this.score = 0;
        this.adv = b.getString("adv");

        this.displayTexts(view);
        return view;
    }

    private void displayTexts(View view) {
        ((TextView) view.findViewById(R.id.score)).setText(score + "/" + (round - 1));
        ((TextView) view.findViewById(R.id.advName)).setText(adv);
        ((TextView) view.findViewById(R.id.questionNumber)).setText(String.valueOf(round));
    }

    private void DisplayTexts() {
        this.displayTexts(getView());
    }

    public void Update(int round, int score) {
        this.round = round;
        this.score = score;
        this.DisplayTexts();
    }
}
