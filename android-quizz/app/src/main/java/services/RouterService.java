package services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.florian.app.HomeActivity;
import com.example.florian.app.OffLineResultActivity;
import com.example.florian.app.OnLineQuizzActivity;
import com.example.florian.app.ResultActivity;

import entities.GameData;
import entities.GameResult;
import entities.Question;
import entities.User;

/**
 * Created by Florian on 11/02/2018.
 */

public class RouterService {

    public static void goHome(Activity activity, User user) {
        Intent intent = new Intent(activity, HomeActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goOnlineQuizz(Activity activity, User user, GameData gameData, AlertDialog alertDialog) {
        alertDialog.dismiss();
        goOnlineQuizz(activity, user, gameData);
    }

    public static void goOnlineQuizz(Activity activity, User user, GameData gameData) {
        Intent intent = new Intent(activity, OnLineQuizzActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        b.putParcelable("gameData", gameData);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goResult(Activity activity, User user, GameResult gameResult) {
        Intent intent = new Intent(activity, ResultActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        b.putParcelable("gameResult", gameResult);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goOfflineResult(Activity activity, User user, int score, Question question) {
        Intent intent = new Intent(activity, OffLineResultActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        b.putParcelable("question", question);
        b.putInt("score", score);
        intent.putExtras(b);
        activity.startActivity(intent);
    }
}
