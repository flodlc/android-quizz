package com.example.florian.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import entities.GameResult;
import entities.Round;
import entities.User;
import services.RouterService;

/**
 * Created by Florian on 11/02/2018.
 */

public class ResultActivity extends AppCompatActivity {

    private User user;
    private GameResult gameResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        user = getIntent().getExtras().getParcelable("user");
        gameResult = getIntent().getExtras().getParcelable("gameResult");

        displayTexts();
        displayRounds();

        final Activity activity = this;
        (findViewById(R.id.backHome)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goHome(activity, user);
            }
        });
    }

    private void displayRounds() {
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
            resultText = "Match nul ! (trad)";
        } else if (gameResult.getGame().getWinner().getId() == user.getId()) {
            resultText = "Gagné ! (trad)";
        } else {
            resultText = "Perdu ! (trad)";
        }

        ((TextView) findViewById(R.id.result)).setText(resultText);
        ((TextView) findViewById(R.id.playerA)).setText(gameResult.getGame().getUserA().getUsername());
        ((TextView) findViewById(R.id.playerB)).setText(gameResult.getGame().getUserB().getUsername());
    }
}
