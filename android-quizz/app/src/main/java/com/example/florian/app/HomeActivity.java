package com.example.florian.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import entities.User;


/**
 * Created by Florian on 31/01/2018.
 */

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle b = new Bundle();
        User user = getIntent().getExtras().getParcelable("user");
        b.putParcelable("user", user);

        OnlineSelectorActivity on = OnlineSelectorActivity.newInstance(b);
        OfflineSelectorActivity off = OfflineSelectorActivity.newInstance(b);

        getSupportFragmentManager().beginTransaction().add(R.id.content, on, "ONLINE").commit();
        getSupportFragmentManager().beginTransaction().add(R.id.content, off, "OFFLINE").commit();
    }
}
