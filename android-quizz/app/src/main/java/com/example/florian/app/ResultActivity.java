package com.example.florian.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import entities.Game;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;

/**
 * Created by Florian on 11/02/2018.
 */

public class ResultActivity extends AppCompatActivity {

    private User user;
    private Game game;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        user = getIntent().getExtras().getParcelable("user");
        game = getIntent().getExtras().getParcelable("game");

        String text;
        String scoreA = "";

        if (game.getState() != 2) {
            text = "En attente de l'adversaire";
        } else if (game.getWinner().getId() == user.getId()) {
            text = "Gagné (trad)";
        } else {
            text = "Perdu ! (trad)";
        }

        ((TextView)findViewById(R.id.result)).setText(text);

        final Activity activity = this;
        (findViewById(R.id.backHome)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putParcelable("user", user);
                Intent intent = new Intent(activity, HomeActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
        });
    }
}
