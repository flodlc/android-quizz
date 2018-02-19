package com.example.florian.app.offline;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.florian.app.QuestionActivity;
import com.example.florian.app.QuizzActivityInterface;
import com.example.florian.app.R;
import com.example.florian.app.online.OnlineSelectorActivity;

import java.io.IOException;

import entities.Answer;
import entities.Question;
import entities.Round;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import services.ApiService;
import services.QuestionManager;
import services.RouterService;

/**
 * Created by Florian on 31/01/2018.
 */

public class OffLineQuizzActivity extends AppCompatActivity implements QuizzActivityInterface {

    private User user;
    private int score;
    private QuestionActivity questionActivity;
    private ActivityOfflineInfo activityOfflineInfo;
    private Question currentQuestion;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_quizz);

        this.user = getIntent().getExtras().getParcelable("user");
        this.score = 0;
        this.displayScore();
        this.displayQuestion();
    }

    private void displayScore() {
        this.activityOfflineInfo = ActivityOfflineInfo.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content, activityOfflineInfo, "INFO").commit();
    }

    public void displayQuestion() {
        Bundle b = new Bundle();

        try {
            currentQuestion = QuestionManager.getRandomQuestion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentQuestion != null) {
            b.putParcelable("round", new Round(currentQuestion));
            this.questionActivity = QuestionActivity.newInstance(b);
            getSupportFragmentManager().beginTransaction().add(R.id.content, this.questionActivity, "QUESTION").commit();
        } else {
            alertDialog = makeAlertDilogue();
            QuestionManager.getOfflineQuestions();
        }
    }


    private AlertDialog makeAlertDilogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OffLineQuizzActivity.this);
        builder.setIcon(R.drawable.sablier);
        builder.setMessage(R.string.loadingQuestions);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        return alertDialog;
    }


    @Override
    public void saveAnswer(Answer answer) {
        if (answer.getAnswer().equals(currentQuestion.getAnswer())) {
            try {
                currentQuestion = QuestionManager.getRandomQuestion();
            } catch (IOException e) {
                e.printStackTrace();
            }
            score++;
            this.questionActivity.update(new Round(currentQuestion));
            this.activityOfflineInfo.update(score);
        } else {
            displayResult();
        }
    }

    private void displayResult() {
        RouterService.goOfflineResult(this, user, score, currentQuestion);
        finish();
    }
}
