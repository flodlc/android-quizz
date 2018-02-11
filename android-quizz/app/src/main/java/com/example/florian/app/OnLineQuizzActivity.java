package com.example.florian.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import entities.Answer;
import entities.Game;
import entities.GameData;
import entities.GameResult;
import entities.PostAnswers;
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

    private GameData gameData;
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

        this.gameData = getIntent().getExtras().getParcelable("gameData");
        this.user = getIntent().getExtras().getParcelable("user");
        this.score = 0;
        this.roundNb = 1;
        this.answers = new ArrayList<>();
        this.displayGameInformations();
        this.displayQuestion();
    }

    private void displayGameInformations() {
        Bundle b = new Bundle();
        b.putString("adv", gameData.getGame().getAdv());
        this.onlineInfosActivity = OnlineInfosActivity.newInstance(b);
        getSupportFragmentManager().beginTransaction().add(R.id.content,
                onlineInfosActivity, "INFO").commit();
    }

    private void displayQuestion() {
        Bundle b = new Bundle();
        b.putParcelable("round", gameData.getRounds().get(0));
        this.questionActivity = QuestionActivity.newInstance(b);
        getSupportFragmentManager().beginTransaction().add(R.id.content, this.questionActivity, "QUESTION").commit();
    }

    @Override
    public void saveAnswer(Answer answer) {
        if (answer.getAnswer().equals(gameData.getRounds().get(roundNb - 1).getQuestion().getAnswer())) {
            score++;
        }
        roundNb++;
        this.answers.add(answer);
        if (answers.size() == gameData.getRounds().size()) {
            ApiServiceInterface apiService = ApiService.getService();
            PostAnswers postAnswers = new PostAnswers(user.getId(), gameData.getGame().getId(), answers);
            Call<Game> call = apiService.postAnswers(postAnswers);
            call.enqueue(new Callback<Game>() {
                @Override
                public void onResponse(@NonNull Call<Game> call,
                                       @NonNull Response<Game> response) {
                    displayResult(response.body());
                }

                @Override
                public void onFailure(Call<Game> call, Throwable t) {
                }
            });
        } else {
            this.onlineInfosActivity.Update(roundNb, score);
            this.questionActivity.update(gameData.getRounds().get(roundNb - 1));
        }
    }

    private void displayResult(Game game) {
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        b.putParcelable("game", game);
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }
}
