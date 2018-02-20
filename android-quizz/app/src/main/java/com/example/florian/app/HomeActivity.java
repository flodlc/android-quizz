package com.example.florian.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.florian.app.offline.OfflineSelectorActivity;
import com.example.florian.app.online.OnlineSelectorActivity;

import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.ApiServiceInterface;
import services.QuestionManager;
import services.RouterService;
import services.UserManager;


/**
 * Created by Florian on 31/01/2018.
 */

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //deleteFile("QEQuestions");
        //QuestionManager.getOfflineQuestions(this);

        Bundle b = new Bundle();
        User user = getIntent().getExtras().getParcelable("user");
        b.putParcelable("user", user);

        OnlineSelectorActivity on = OnlineSelectorActivity.newInstance(b);
        OfflineSelectorActivity off = OfflineSelectorActivity.newInstance(b);

        getSupportFragmentManager().beginTransaction().add(R.id.content, on, "ONLINE").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content, off, "OFFLINE").commit();

        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<Boolean> call = apiService.logout();
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        deleteFile("QEUser");
                        RouterService.goConnectPageAndFinish(HomeActivity.this);
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
                        deleteFile("QEUser");
                        RouterService.goConnectPageAndFinish(HomeActivity.this);
                    }
                });
            }
        });
    }
}
