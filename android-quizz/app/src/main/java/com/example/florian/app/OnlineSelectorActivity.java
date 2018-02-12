package com.example.florian.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Service;

import entities.GameData;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;
import services.RouterService;


public class OnlineSelectorActivity extends Fragment {

    private User user;
    private AlertDialog alertDialog;

    public static OnlineSelectorActivity newInstance(Bundle args) {
        OnlineSelectorActivity f = new OnlineSelectorActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_selector, container, false);
        Bundle b = getArguments();
        this.user = b.getParcelable("user");
        setIntent(view.findViewById(R.id.onlineButton));
        return view;
    }

    private void checkNewGame(final GameData gameData) {
        ApiServiceInterface apiService = ApiService.getService();
        Call<GameData> call = apiService.checkNewGame(ImmutableMap.of("user", String.valueOf(user.getId()),
                "game", String.valueOf(gameData.getGame().getId())));
        final long time1 = SystemClock.elapsedRealtime();

        call.enqueue(new Callback<GameData>() {
            @Override
            public void onResponse(@NonNull Call<GameData> call,
                                   @NonNull Response<GameData> response) {
                if (response.code() == 200) {
                    GameData freshGameData = response.body();
                    if (freshGameData.getGame().getState() == 1) {
                        RouterService.goOnlineQuizz(getActivity(), user, gameData, alertDialog);
                    } else if (SystemClock.elapsedRealtime() - time1 < 30000) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkNewGame(freshGameData);
                    } else {
                        alertDialog.dismiss();
                        RouterService.goHome(getActivity(), user);
                    }
                }
            }

            @Override
            public void onFailure(Call<GameData> call, Throwable t) {
            }
        });
    }


    private void getNewGame() {
        ApiServiceInterface apiService = ApiService.getService();
        Call<GameData> call = apiService.getNewGame(ImmutableMap.of("user", String.valueOf(user.getId())));
        final long time1 = SystemClock.currentThreadTimeMillis();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Waiting for a player");
        builder.setIcon(R.drawable.sablier);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel game
            }
        });

        alertDialog = builder.show();

        call.enqueue(new Callback<GameData>() {
            @Override
            public void onResponse(@NonNull Call<GameData> call,
                                   @NonNull Response<GameData> response) {
                if (response.code() == 200) {
                    GameData gameData = response.body();
                    if (gameData.getGame().getState() == 1) {
                        RouterService.goOnlineQuizz(getActivity(), user, response.body(), alertDialog);
                    } else if (SystemClock.currentThreadTimeMillis() - time1 < 30000) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkNewGame(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<GameData> call, Throwable t) {
            }
        });
    }

    private void setIntent(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getNewGame();
            }
        });
    }
}