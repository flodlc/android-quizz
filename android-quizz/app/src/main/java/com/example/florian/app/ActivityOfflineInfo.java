package com.example.florian.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import entities.Round;
import entities.User;

/**
 * Created by Florian on 01/02/2018.
 */

public class ActivityOfflineInfo extends Fragment {

    private int score;

    public static ActivityOfflineInfo newInstance() {
        return new ActivityOfflineInfo();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_offline_info, container, false);
        this.score = 0;
        this.displayTexts(view);
        return view;
    }

    private void displayTexts(View view) {
        ((TextView) view.findViewById(R.id.score)).setText(String.valueOf(score));
    }

    private void DisplayTexts() {
        this.displayTexts(getView());
    }

    public void update(int score) {
        this.score = score;
        this.DisplayTexts();
    }
}
