package com.example.florian.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import entities.Answer;
import entities.GameData;
import entities.GameResult;
import entities.PostAnswers;
import entities.Question;
import entities.Round;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;
import services.QuestionManager;
import services.RouterService;

/**
 * Created by Florian on 31/01/2018.
 */

public class OffLineQuizzActivity extends AppCompatActivity implements QuizzActivityInterface {

    private User user;
    private int score;
    private QuestionActivity questionActivity;
    private QuestionManager questionManager;
    private ActivityOfflineInfo activityOfflineInfo;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_quizz);

        questionManager = new QuestionManager(this);
        this.user = getIntent().getExtras().getParcelable("user");
        this.score = 0;
        this.displayScore();
        this.displayQuestion();
    }

    private void displayScore() {
        this.activityOfflineInfo = ActivityOfflineInfo.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content, activityOfflineInfo, "INFO").commit();
    }

    private void displayQuestion() {
        Bundle b = new Bundle();
        try {
            currentQuestion = questionManager.getRandomQuestion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        b.putParcelable("round", new Round(currentQuestion));
        this.questionActivity = QuestionActivity.newInstance(b);
        getSupportFragmentManager().beginTransaction().add(R.id.content, this.questionActivity, "QUESTION").commit();
    }

    @Override
    public void saveAnswer(Answer answer) {
        if (answer.getAnswer().equals(currentQuestion.getAnswer())) {
            try {
                currentQuestion = questionManager.getRandomQuestion();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.questionActivity.update(new Round(currentQuestion));
            this.activityOfflineInfo.update(score);
            score++;
        } else {
            displayResult();
        }
    }

    private void displayResult() {
        RouterService.goOfflineResult(this, user, score, currentQuestion);
        finish();
    }
}
