package com.example.florian.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableMap;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;


public class OnlineSelectorActivity extends Fragment {

    private String user;

    public static OnlineSelectorActivity newInstance(Bundle args) {
        OnlineSelectorActivity f = new OnlineSelectorActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_selector, container, false);
        Bundle b = getArguments();
        this.user = b.getString("user");
        setIntent(view.findViewById(R.id.onlineButton), OnLineQuizzActivity.class);
        return view;
    }

    private void setIntent(View view, final Class className) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<LinkedTreeMap> call = apiService.postNewGame(ImmutableMap.of("user", user));

                call.enqueue(new Callback<LinkedTreeMap>() {
                    @Override
                    public void onResponse(Call<LinkedTreeMap> call, Response<LinkedTreeMap> response) {
                        LinkedTreeMap body = response.body();
                        if (((Double) body.get("status")).intValue() == 1) {
                            Intent intent = new Intent(getActivity(), className);
                            Bundle b = new Bundle();
                            b.putString("user", user);
                            b.putSerializable("game", (LinkedTreeMap) body.get("game"));
                            intent.putExtras(b);
                            startActivity(intent);
                        } else {
                            //show message looking for new player ...
                        }
                    }

                    @Override
                    public void onFailure(Call<LinkedTreeMap> call, Throwable t) {}
                });


            }
        });
    }
}