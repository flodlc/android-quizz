package com.quizz.offline;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
        checkRecord();
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

    @Override
    public void onStop() {
        super.onStop();
        finish();
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

    private void showCanAddQuetion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.bravo);
        builder.setIcon(R.drawable.check);
        builder.setMessage(R.string.infoAddQuestion);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void checkRecord() {
        String recordString = UserManager.getData("record");
        int record = recordString.equals("") ? 0 : Integer.valueOf(recordString);
        if (game.getScore() > record) {
            findViewById(R.id.newRecord).setVisibility(View.VISIBLE);
            user.setRecord(game.getScore());
            UserManager.saveRecord(user);
        }

        if (record < getResources().getInteger(R.integer.minRecordForAddQuestion)
                && game.getScore() >= getResources().getInteger(R.integer.minRecordForAddQuestion)) {
            showCanAddQuetion();
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
    }
}
