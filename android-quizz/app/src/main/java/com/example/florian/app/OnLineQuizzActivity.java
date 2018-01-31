package com.example.florian.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;

/**
 * Created by Florian on 31/01/2018.
 */

public class OnLineQuizzActivity extends AppCompatActivity {

    private HashMap game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_quizz);
        this.game = (HashMap) (getIntent().getExtras().get("game"));

        this.displayQuestion();
    }

    private void displayQuestion() {
        Bundle b = new Bundle();
        b.putSerializable("question", (HashMap) game.get("question"));
        Fragment f = QuestionActivity.newInstance(b);
        getSupportFragmentManager().beginTransaction().add(R.id.content, f, "TAG").commit();
    }
}
