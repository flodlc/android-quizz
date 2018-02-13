package com.example.florian.app;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.List;

import entities.Question;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;
import services.QuestionManager;
import services.RouterService;
import services.UserManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteFile("QEUser");
        QuestionManager.getOfflineQuestions(this);
        checkUserData();

        final Activity activity = this;
        (findViewById(R.id.validUser)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();

                ApiServiceInterface apiService = ApiService.getService();
                Call<User> call = apiService.getUser(ImmutableMap.of("user", username));
                showLoader();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(@NonNull Call<User> call,
                                           @NonNull Response<User> response) {
                        if (response.code() == 200) {
                            User user = response.body();
                            try {
                                UserManager.setData(activity, "userId", String.valueOf(user.getId()));
                                UserManager.setData(activity, "username", String.valueOf(user.getUsername()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            RouterService.goHome(activity, user);
                            finish();
                        } else {
                            showDialogue(activity);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        showDialogue(activity);
                    }
                });
            }
        });
    }

    private void checkUserData() {
        String userId = UserManager.getData(this, "userId");
        String username = UserManager.getData(this, "username");
        if (!userId.equals("") && !username.equals("")) {
            RouterService.goHome(this, new User(username, Integer.valueOf(userId)));
            finish();
        }
    }

    private void showLoader() {
        findViewById(R.id.validUser).setVisibility(View.GONE);
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        findViewById(R.id.validUser).setVisibility(View.VISIBLE);
        findViewById(R.id.loader).setVisibility(View.GONE);
    }

    private void showDialogue(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.userNotAvailable);
        builder.setIcon(R.drawable.sablier);
        hideLoader();
        builder.setPositiveButton("OK", null);
        builder.show();
    }
}