package com.quizz.online;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.quizz.R;

import com.quizz.entities.Invitation;
import com.quizz.entities.InvitationSend;
import com.quizz.entities.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Created by Lucas on 22/02/2018.
 */

public class InvitationActivity extends AppCompatActivity {

    private User user;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        this.user = getIntent().getExtras().getParcelable("user");
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
    }


    @Override
    protected void onStart() {
        super.onStart();
        setListener();
        setTextListener();
    }

    private void setTextListener() {
        ((AutoCompleteTextView)findViewById(R.id.username)).setThreshold(1);
        ((AutoCompleteTextView)findViewById(R.id.username)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getUsers(s.toString());
            }
        });
    }

    private void getUsers(final String text) {
        if (text == null || text.equals("")) {
            adapter = new ArrayAdapter<String>(InvitationActivity.this,
                    android.R.layout.simple_dropdown_item_1line);
            return;
        }
        ApiServiceInterface apiService = ApiService.getService();
        Call<List<User>> call = apiService.findUsersByText(text);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.username);
                if (response.code() == 200) {
                    String[] users = new String[response.body().size()];

                    int i = 0;
                    for (User user : response.body()) {
                        users[i] = user.getUsername();
                        i++;
                    }

                    adapter = new ArrayAdapter<String>(InvitationActivity.this,
                            android.R.layout.simple_dropdown_item_1line, users);
                    actv.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {

            }
        });
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
