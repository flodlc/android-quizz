package com.example.florian.app;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;

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

        QuestionManager.getOfflineQuestions(this);
        checkUserData();

        (findViewById(R.id.validUser)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                showLoader();
                ApiServiceInterface apiService = ApiService.getService();
                Call<User> call = apiService.getUser(ImmutableMap.of("user", username));
                call.enqueue(callback);
            }
        });
    }

    private void checkUserData() {
        this.deleteFile("QEUser");
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

    private Callback<User> callback = new Callback<User>() {
        @Override
        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
            if (response.code() == 200) {
                User user = response.body();
                try {
                    UserManager.setData(MainActivity.this, "userId", String.valueOf(user.getId()));
                    UserManager.setData(MainActivity.this, "username", String.valueOf(user.getUsername()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RouterService.goHome(MainActivity.this, user);
                finish();
            } else {
                showDialogue(MainActivity.this);
            }
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            showDialogue(MainActivity.this);
        }
    };
}