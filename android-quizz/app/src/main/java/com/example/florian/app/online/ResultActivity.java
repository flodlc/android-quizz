package com.example.florian.app.online;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.florian.app.R;
import com.google.common.collect.ImmutableMap;

import entities.GameData;
import entities.GameResult;
import entities.Round;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import services.ApiService;
import services.ApiServiceInterface;
import services.RouterService;

/**
 * Created by Florian on 11/02/2018.
 */

public class ResultActivity extends AppCompatActivity {

    private User user;
    private GameResult gameResult;
    private Runnable runnable;
    private Handler handler;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        user = getIntent().getExtras().getParcelable("user");
        gameResult = getIntent().getExtras().getParcelable("gameResult");

        displayTexts();
        displayRounds();

        if (gameResult.getGame().getState() != 2) {
            time = SystemClock.elapsedRealtime();
            runnable = new Runnable() {
                @Override
                public void run() {
                    checkGameResult();
                }
            };
            handler = new Handler();
            handler.postDelayed(runnable, 2000);
        }

        final Activity activity = this;
        (findViewById(R.id.backHome)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goHome(activity, user);
                finish();
            }
        });
    }

    private void displayRounds() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        for (Round round : gameResult.getRounds()) {
            Bundle b = new Bundle();
            b.putParcelable("round", round);
            Fragment fragment = AnswerLineActivity.newInstance(b);
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, "ROUND").commit();
        }
    }

    private void displayTexts() {
        String resultText;

        if (gameResult.getGame().getState() != 2) {
            resultText = "En attente de l'adversaire";
        } else if (gameResult.getGame().getWinner() == null) {
            resultText = getResources().getString(R.string.matchNul);
        } else if (gameResult.getGame().getWinner().getUsername().equals(user.getUsername())) {
            resultText = getResources().getString(R.string.win);
        } else {
            resultText = getResources().getString(R.string.lost);
        }

        ((TextView) findViewById(R.id.result)).setText(resultText);
        ((TextView) findViewById(R.id.playerA)).setText(gameResult.getGame().getUserA().getUsername());
        ((TextView) findViewById(R.id.playerB)).setText(gameResult.getGame().getUserB().getUsername());
    }

    private void checkGameResult() {
        if (this.isDestroyed()) {
            handler.removeCallbacks(runnable);
            return;
        }
        ApiServiceInterface apiService = ApiService.getService();
        Call<GameData> call = apiService.checkNewGame(ImmutableMap.of("game",
                String.valueOf(gameResult.getGame().getId())));

        call.enqueue(new Callback<GameData>() {
            @Override
            public void onResponse(Call<GameData> call, retrofit2.Response<GameData> response) {
                if (ApiService.checkCode(ResultActivity.this, response)) {
                    GameData gamedata = response.body();
                    if (gamedata.getGame().getState() == 2) {
                        handler.removeCallbacks(runnable);
                        gameResult = new GameResult(gamedata.getGame(), gamedata.getRounds());
                        displayTexts();
                        displayRounds();
                    } else if (SystemClock.elapsedRealtime() - time < 20000) {
                        handler.postDelayed(runnable, 2000);
                    } else {
                        handler.removeCallbacks(runnable);
                    }
                }
            }

            @Override
            public void onFailure(Call<GameData> call, Throwable t) {

            }
        });
    }
}
