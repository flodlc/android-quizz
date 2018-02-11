package com.example.florian.app;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.common.collect.ImmutableMap;

import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity activity = this;
        (findViewById(R.id.validUser)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                ApiServiceInterface apiService = ApiService.getService();
                Call<User> call = apiService.getUser(ImmutableMap.of("user", username));

                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call,
                                           @NonNull Response<User> response) {
                        if (response.code() == 200) {
                            User user = response.body();
                            Bundle b = new Bundle();
                            b.putParcelable("user", user);
                            Intent intent = new Intent(activity, HomeActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
                        } else {
                            //show message looking for new player ...
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                    }
                });
            }
        });
    }
}