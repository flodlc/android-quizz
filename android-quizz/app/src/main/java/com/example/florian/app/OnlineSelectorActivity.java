package com.example.florian.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableMap;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import entities.Game;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;


public class OnlineSelectorActivity extends Fragment {

    private User user;

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
        setIntent(view.findViewById(R.id.onlineButton), OnLineQuizzActivity.class);
        return view;
    }

    private void setIntent(View view, final Class className) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<Game> call = apiService.getNewGame(ImmutableMap.of("user", String.valueOf(user.getId())));

                call.enqueue(new Callback<Game>() {
                    @Override
                    public void onResponse(@NonNull Call<Game> call,
                                           @NonNull Response<Game> response) {
                        if (response.code() == 200) {
                            Game game = response.body();
                            Intent intent = new Intent(getActivity(), className);
                            Bundle b = new Bundle();
                            b.putParcelable("user", user);
                            b.putParcelable("game", game);
                            intent.putExtras(b);
                            startActivity(intent);
                        } else {
                            //show message looking for new player ...
                        }
                    }

                    @Override
                    public void onFailure(Call<Game> call, Throwable t) {
                    }
                });
            }
        });
    }
}