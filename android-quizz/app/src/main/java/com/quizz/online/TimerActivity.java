package com.quizz.online;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.quizz.R;
import com.quizz.entities.GameData;
import com.quizz.entities.User;
import com.quizz.services.RouterService;

/**
 * Created by Florian on 26/02/2018.
 */

public class TimerActivity extends AppCompatActivity {

    private Handler handler;
    private long beginTime;
    private GameData gameData;
    private User user;
    private int maxTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        maxTime = getResources().getInteger(R.integer.timeBeforeQuizz);

        this.gameData = getIntent().getExtras().getParcelable("gameData");
        this.user = getIntent().getExtras().getParcelable("user");

        setProgressBar();
        startTimer();
    }

    private void startTimer() {
        beginTime = SystemClock.elapsedRealtime();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (TimerActivity.this.isDestroyed()) {
                    return;
                }

                if (SystemClock.elapsedRealtime() - beginTime > maxTime) {
                    RouterService.goOnlineQuizz(TimerActivity.this, user, gameData);
                    finish();
                }
                setTime();
                handler.postDelayed(this, 100);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 100);
    }

    private void setProgressBar() {
        ((ProgressBar) findViewById(R.id.progress)).getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorRed),
                android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void setTime() {
        long elapsedTime = SystemClock.elapsedRealtime() - beginTime;
        ((TextView) findViewById(R.id.timeText)).setText(String.valueOf((int) ((maxTime - elapsedTime) / 1000)));
    }
}
