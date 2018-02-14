package com.example.florian.app.online;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.example.florian.app.R;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.List;

import entities.Game;
import entities.Round;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;

/**
 * Created by Florian on 14/02/2018.
 */

public class DisplayGamesActivity extends AppCompatActivity{

    private User user;
    private List<Game> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_games);
        this.user = getIntent().getExtras().getParcelable("user");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApiServiceInterface apiService = ApiService.getService();
        Call<List<Game>> call = apiService.getMyGames(ImmutableMap.of("user", String.valueOf(user.getId())));
        call.enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                games = response.body();
                displayRounds();
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {

            }
        });
    }

    private void displayRounds() {
        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        for (Game game : games) {
            Bundle b = new Bundle();
            b.putParcelable("user", user);
            b.putParcelable("game", game);
            Fragment fragment = GameLineActivity.newInstance(b);
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, "GAME").commit();
        }
    }
}