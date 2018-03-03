package com.quizz;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.quizz.entities.Question;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Florian on 03/03/2018.
 */

public class AddQuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);
        setListeners();
    }

    private void showLoader() {
        findViewById(R.id.sendQuestion).setVisibility(View.GONE);
        findViewById(R.id.loader).setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        findViewById(R.id.sendQuestion).setVisibility(View.VISIBLE);
        findViewById(R.id.loader).setVisibility(View.GONE);
    }

    private void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddQuestionActivity.this);
        builder.setTitle(R.string.error);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void showOkMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddQuestionActivity.this);
        builder.setMessage(getResources().getString(R.string.thanksQuestion));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((EditText) findViewById(R.id.question)).setText("");
                ((EditText) findViewById(R.id.goodAnswer)).setText("");
                ((EditText) findViewById(R.id.badAnswer1)).setText("");
                ((EditText) findViewById(R.id.badAnswer2)).setText("");
                ((EditText) findViewById(R.id.badAnswer3)).setText("");
            }
        });
        builder.show();
    }

    private void setListeners() {
        findViewById(R.id.question).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_ENTER);
            }
        });

        findViewById(R.id.sendQuestion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoader();
                String questionText = ((EditText) findViewById(R.id.question)).getText().toString();
                String goodAnswer = ((EditText) findViewById(R.id.goodAnswer)).getText().toString();
                String badAnswer1 = ((EditText) findViewById(R.id.badAnswer1)).getText().toString();
                String badAnswer2 = ((EditText) findViewById(R.id.badAnswer2)).getText().toString();
                String badAnswer3 = ((EditText) findViewById(R.id.badAnswer3)).getText().toString();
                if (questionText.length() <= getResources().getInteger(R.integer.minRecordForAddQuestion)) {
                    showMessage(getResources().getString(R.string.questionToShort));
                    hideLoader();
                    return;
                }
                if (goodAnswer.length() == 0) {
                    showMessage(getResources().getString(R.string.missGoodAnswer));
                    hideLoader();
                    return;
                }
                if (badAnswer1.length() == 0
                        || badAnswer2.length() == 0
                        || badAnswer3.length() == 0) {
                    showMessage(getResources().getString(R.string.missBadAnswers));
                    hideLoader();
                    return;
                }

                Question[] questions = new Question[]{new Question(questionText, goodAnswer, badAnswer1,
                        badAnswer2, badAnswer3, "ResponseA")};

                sendQuestion(questions);
            }
        });
    }

    private void sendQuestion(Question[] questions) {
        ApiServiceInterface apiService = ApiService.getService();
        Call<Boolean> call = apiService.createQuestion(questions);

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (ApiService.checkCode(AddQuestionActivity.this, response)) {
                    showOkMessage();
                } else {
                    showMessage(getResources().getString(R.string.errorServer));
                }
                hideLoader();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                ApiService.showErrorMessage(AddQuestionActivity.this);
                hideLoader();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
