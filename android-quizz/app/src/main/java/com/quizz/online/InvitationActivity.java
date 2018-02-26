package com.quizz.online;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.quizz.R;

import com.quizz.entities.Invitation;
import com.quizz.entities.InvitationSend;
import com.quizz.entities.User;

import retrofit2.Call;
import retrofit2.Callback;

import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;


/**
 * Created by Lucas on 22/02/2018.
 */

public class InvitationActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        this.user = getIntent().getExtras().getParcelable("user");
    }


    @Override
    protected void onStart() {
        super.onStart();
        setListener();
    }


    private void setListener() {
        (findViewById(R.id.sendInvit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView errorMsg = (TextView) findViewById(R.id.errorMsg);
                errorMsg.setVisibility(View.GONE);
                (findViewById(R.id.sendInvit)).setVisibility(View.GONE);
                (findViewById(R.id.loader)).setVisibility(View.VISIBLE);
                ApiServiceInterface apiService = ApiService.getService();
                Call<Invitation> call = apiService.sendInvitation(new InvitationSend(((EditText) findViewById(R.id.username)).getText().toString()));

                call.enqueue(new Callback<Invitation>() {
                    @Override
                    public void onResponse(Call<Invitation> call, retrofit2.Response<Invitation> response) {
                        TextView errorMsg = (TextView) findViewById(R.id.errorMsg);
                        if (response.code() == 200) {
                            InvitationActivity.this.finish();
                            RouterService.goDisplayInvitations(InvitationActivity.this, user);
                        } else if (response.code() == 500) {
                            errorMsg.setVisibility(View.VISIBLE);
                            errorMsg.setText(R.string.notMe);
                        } else if (response.code() == 404) {
                            errorMsg.setVisibility(View.VISIBLE);
                            errorMsg.setText(R.string.unknownUser);
                        } else if (response.code() == 409) {
                            errorMsg.setVisibility(View.VISIBLE);
                            errorMsg.setText(R.string.invitationIsset);
                        } else {
                            ApiService.showErrorMessage(InvitationActivity.this);
                        }
                        (findViewById(R.id.sendInvit)).setVisibility(View.VISIBLE);
                        (findViewById(R.id.loader)).setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Invitation> call, Throwable t) {
                        ApiService.showErrorMessage(InvitationActivity.this);
                    }
                });
            }
        });
    }
}
