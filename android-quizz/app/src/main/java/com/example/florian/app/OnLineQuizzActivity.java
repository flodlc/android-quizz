package com.example.florian.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import entities.Answer;
import entities.Game;
import entities.GameResult;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;

/**
 * Created by Florian on 31/01/2018.
 */

public class OnLineQuizzActivity extends AppCompatActivity implements QuizzActivityInterface {

    private Game game;
    private User user;
    private ArrayList<Answer> answers;
    private int score;
    private int roundNb;
    private QuestionActivity questionActivity;
    private OnlineInfosActivity onlineInfosActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_quizz);

        this.game = getIntent().getExtras().getParcelable("game");
        this.user = getIntent().getExtras().getParcelable("user");
        this.score = 0;
        this.roundNb = 1;
        this.displayGameInformations();
        this.displayQuestion();
    }

    private void displayGameInformations() {
        Bundle b = new Bundle();
        b.putString("adv", game.getInfos().getAdv());
        this.onlineInfosActivity = OnlineInfosActivity.newInstance(b);
        getSupportFragmentManager().beginTransaction().add(R.id.content,
                onlineInfosActivity, "INFO").commit();
    }

    private void displayQuestion() {
        Bundle b = new Bundle();
        b.putParcelable("round", game.getRounds().get(0));
        this.questionActivity = QuestionActivity.newInstance(b);
        getSupportFragmentManager().beginTransaction().add(R.id.content, this.questionActivity, "QUESTION").commit();
    }

    @Override
    public void saveAnswer(Answer answer) {
        if (answer.getAnswer().equals(game.getRounds().get(roundNb - 1).getQuestion().getAnswer())) {
            score++;
        }
        roundNb++;
        this.answers.add(answer);
        if (answers.size() == game.getRounds().size()) {
            ApiServiceInterface apiService = ApiService.getService();
            Call<GameResult> call = apiService.postAnswers(answers, game.getInfos().getId(), user.getId());
            call.enqueue(new Callback<GameResult>() {
                @Override
                public void onResponse(@NonNull Call<GameResult> call,
                                       @NonNull Response<GameResult> response) {
                    //display game result
                }

                @Override
                public void onFailure(Call<GameResult> call, Throwable t) {
                }
            });
        } else {
            this.onlineInfosActivity.Update(roundNb, score);
            this.questionActivity.update(game.getRounds().get(roundNb - 1));
        }
    }
}
