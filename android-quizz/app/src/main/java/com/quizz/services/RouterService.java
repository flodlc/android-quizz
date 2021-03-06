package com.quizz.services;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.quizz.AddQuestionActivity;
import com.quizz.HomeActivity;
import com.quizz.MainActivity;
import com.quizz.entities.OfflineGame;
import com.quizz.offline.OffLineQuizzActivity;
import com.quizz.offline.OffLineResultActivity;
import com.quizz.online.DisplayGamesActivity;
import com.quizz.online.DisplayInvitationsActivity;
import com.quizz.online.InvitationActivity;
import com.quizz.online.OnLineQuizzActivity;
import com.quizz.online.ResultActivity;

import com.quizz.entities.GameData;
import com.quizz.entities.GameResult;
import com.quizz.entities.Question;
import com.quizz.entities.User;
import com.quizz.online.TimerActivity;
import com.quizz.stat.StatisticActivity;

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

    public static void goTimer(Activity activity, User user, GameData gameData) {
        Intent intent = new Intent(activity, TimerActivity.class);
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

    public static void goOfflineResult(Activity activity, User user, OfflineGame offlineGame, Question question) {
        Intent intent = new Intent(activity, OffLineResultActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        b.putParcelable("question", question);
        b.putParcelable("offlineGame", offlineGame);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goDisplayGames(Activity activity, User user) {
        Intent intent = new Intent(activity, DisplayGamesActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goDisplayInvitations(Activity activity, User user) {
        Intent intent = new Intent(activity, DisplayInvitationsActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goSendInvitation(Activity activity, User user) {
        Intent intent = new Intent(activity, InvitationActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goOfflineQuizz(Activity activity, User user) {
        Intent intent = new Intent(activity, OffLineQuizzActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goStatistics(Activity activity, User user) {
        Intent intent = new Intent(activity, StatisticActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    public static void goConnectPageAndFinish(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goAddQuestion(Activity activity, User user) {
        Intent intent = new Intent(activity, AddQuestionActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("user", user);
        intent.putExtras(b);
        activity.startActivity(intent);
    }
}
