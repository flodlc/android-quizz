package com.example.florian.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;

import services.ApiServiceInterface;
import services.QuestionManager;
import services.RouterService;
import services.UserManager;

public class FirstMainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_first);
        showLoader();
        if (ApiService.isConnected()) {
            QuestionManager.getOfflineQuestions();
        }
        checkUserData();
    }

    private void checkUserData() {
        //this.deleteFile("QEUser");

        if (!ApiService.isConnected()) {
            User user = UserManager.getUSer();
            if (user != null) {
                RouterService.goHome(this, user);
                finish();
            } else {
                RouterService.goConnectPageAndFinish(FirstMainActivity.this);
                finish();
            }
        } else {
            ApiServiceInterface apiService = ApiService.getService();
            Call<User> call = apiService.getUser();
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() != 403 && response.body() != null) {
                        RouterService.goHome(FirstMainActivity.this, response.body());
                        FirstMainActivity.this.finish();
                    } else {
                        RouterService.goConnectPageAndFinish(FirstMainActivity.this);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    ApiService.showErrorMessage(FirstMainActivity.this);
                }
            });
        }
    }

    private void showLoader() {
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
    }
}