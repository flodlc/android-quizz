package com.example.florian.app.online;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.florian.app.QuestionActivity;
import com.example.florian.app.QuizzActivityInterface;
import com.example.florian.app.R;
import com.example.florian.app.offline.OffLineQuizzActivity;

import java.util.ArrayList;

import entities.Answer;
import entities.GameData;
import entities.GameResult;
import entities.PostAnswers;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;
import services.RouterService;

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
        b.putParcelable("round", gameData.getRounds().get(0));
        b.putParcelable("adv", gameData.getGame().getAdv());
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

    private void showLoader() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
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
            Call<GameResult> call = apiService.postAnswers(postAnswers);
            showLoader();
            call.enqueue(new Callback<GameResult>() {
                @Override
                public void onResponse(@NonNull Call<GameResult> call,
                                       @NonNull Response<GameResult> response) {
                    if (ApiService.checkCode(OnLineQuizzActivity.this, response)) {
                        displayResult(response.body());
                    }
                }

                @Override
                public void onFailure(Call<GameResult> call, Throwable t) {
                    ApiService.showErrorMessage(OnLineQuizzActivity.this);
                }
            });
        } else {
            this.onlineInfosActivity.Update(gameData.getRounds().get(roundNb - 1), score);
            this.questionActivity.update(gameData.getRounds().get(roundNb - 1));
        }
    }

    private void displayResult(GameResult gameResult) {
        RouterService.goResult(this, user, gameResult);
        finish();
    }
}
