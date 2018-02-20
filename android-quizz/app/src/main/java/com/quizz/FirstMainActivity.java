package com.quizz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.quizz.entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.quizz.services.ApiService;

import com.quizz.services.ApiServiceInterface;
import com.quizz.services.QuestionManager;
import com.quizz.services.RouterService;
import com.quizz.services.UserManager;

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
                    if (ApiService.checkCode(FirstMainActivity.this, response) && response.body() != null) {
                        RouterService.goHome(FirstMainActivity.this, response.body());
                        FirstMainActivity.this.finish();
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