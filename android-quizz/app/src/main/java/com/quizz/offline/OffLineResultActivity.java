package com.quizz.offline;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.quizz.R;

import com.quizz.entities.Game;
import com.quizz.entities.OfflineGame;
import com.quizz.entities.Question;
import com.quizz.entities.User;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;
import com.quizz.services.UserManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Florian on 11/02/2018.
 */

public class OffLineResultActivity extends AppCompatActivity {

    private User user;
    private OfflineGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activit_offline_result);

        Bundle b = getIntent().getExtras();
        assert b != null;
        user = b.getParcelable("user");
        game = b.getParcelable("offlineGame");

        postGame(game);
        displayTexts((Question) b.getParcelable("question"));

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

    private void checkRecord() {
        String recordString = UserManager.getData("record");
        int record = recordString.equals("") ? 0 : Integer.valueOf(recordString);
        if (game.getScore() > record) {
            findViewById(R.id.newRecord).setVisibility(View.VISIBLE);
            user.setRecord(game.getScore());
            UserManager.saveRecord(user);
        }
    }

    private void postGame(OfflineGame offlineGame) {
        ApiServiceInterface apiService = ApiService.getService();
        Call<String> call = apiService.createOffGame(offlineGame);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void displayTexts(Question question) {
        ((TextView) findViewById(R.id.score)).setText(String.valueOf(game.getScore()));
        ((TextView) findViewById(R.id.questionText)).setText(question.getQuestion());
        ((TextView) findViewById(R.id.correctAnswer)).setText(getAnswerText(question));
        checkRecord();
    }
}
