package com.quizz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.quizz.offline.OfflineSelectorActivity;
import com.quizz.online.OnlineSelectorActivity;

import com.quizz.entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;


/**
 * Created by Florian on 31/01/2018.
 */

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle b = new Bundle();
        final User user = getIntent().getExtras().getParcelable("user");
        b.putParcelable("user", user);

        OnlineSelectorActivity on = OnlineSelectorActivity.newInstance(b);
        OfflineSelectorActivity off = OfflineSelectorActivity.newInstance(b);

        getSupportFragmentManager().beginTransaction().add(R.id.content, on, "ONLINE").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content, off, "OFFLINE").commit();

        findViewById(R.id.statButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouterService.goStatistics(HomeActivity.this, user);
            }
        });
    }
}
