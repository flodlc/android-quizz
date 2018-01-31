package com.example.florian.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Created by Florian on 31/01/2018.
 */

public class QuestionActivity extends Fragment {

    private HashMap question;

    public static QuestionActivity newInstance(Bundle args) {
        QuestionActivity f = new QuestionActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_selector, container, false);
        Bundle b = getArguments();
        this.question = (HashMap) b.getSerializable("question");

        ((TextView) view.findViewById(R.id.questionText)).setText((String) question.get("text"));
        ((Button) view.findViewById(R.id.response1)).setText((String) question.get("response1"));
        ((Button) view.findViewById(R.id.response2)).setText((String) question.get("response2"));
        ((Button) view.findViewById(R.id.response3)).setText((String) question.get("response3"));
        ((Button) view.findViewById(R.id.response4)).setText((String) question.get("response4"));
        return view;
    }
}
