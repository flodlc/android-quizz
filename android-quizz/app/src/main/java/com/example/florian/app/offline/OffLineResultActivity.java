package com.example.florian.app.offline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.florian.app.R;

import java.io.IOException;

import entities.Question;
import entities.User;
import services.RouterService;
import services.UserManager;

/**
 * Created by Florian on 11/02/2018.
 */

public class OffLineResultActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_offline_result);

        user = getIntent().getExtras().getParcelable("user");

        Bundle b = getIntent().getExtras();

        displayTexts(b.getInt("score"), (Question) b.getParcelable("question"));

        findViewById(R.id.backHome).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goHome(OffLineResultActivity.this, user);
                finish();
            }
        });

        findViewById(R.id.playAgain).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goOfflineQuizz(OffLineResultActivity.this, user);
                finish();
            }
        });
    }

    private String getAnswerText(Question question) {
        switch (question.getAnswer()) {
            case "ResponseA":
                return question.getResponseA();
            case "ResponseB":
                return question.getResponseB();
            case "ResponseC":
                return question.getResponseC();
            default:
                return question.getResponseD();
        }
    }

    private void checkRecord(int score) {
        String recordString = UserManager.getData("record");
        int record = recordString.equals("") ? 0 : Integer.valueOf(recordString);
        if (score > record) {
            findViewById(R.id.newRecord).setVisibility(View.VISIBLE);
            user.setRecord(score);
            UserManager.saveRecord(user);
        }
    }

    private void displayTexts(int score, Question question) {
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(score));
        ((TextView) findViewById(R.id.questionText)).setText(question.getQuestion());
        ((TextView) findViewById(R.id.correctAnswer)).setText(getAnswerText(question));
        checkRecord(score);
    }
}
