package com.quizz.online;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.quizz.R;

import java.util.List;

import com.quizz.entities.Invitation;
import com.quizz.entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;

/**
 * Created by Lucas on 22/02/2018.
 */

public class DisplayInvitationsActivity extends AppCompatActivity {

    private User user;
    private List<Invitation> invitations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_invitations);
        this.user = getIntent().getExtras().getParcelable("user");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApiServiceInterface apiService = ApiService.getService();
        Call<List<Invitation>> call = apiService.getMyInvitations();
        call.enqueue(new Callback<List<Invitation>>() {
            @Override
            public void onResponse(Call<List<Invitation>> call, Response<List<Invitation>> response) {
                if (ApiService.checkCode(DisplayInvitationsActivity.this, response)) {
                    invitations = response.body();
                    displayInvitations();
                }
                hideLoader();
            }

            @Override
            public void onFailure(Call<List<Invitation>> call, Throwable t) {
                ApiService.showErrorMessage(DisplayInvitationsActivity.this, true);
                hideLoader();
            }
        });
    }

    private void hideLoader() {
        this.findViewById(R.id.loader).setVisibility(View.GONE);
    }


    private void displayInvitations() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }

        if (invitations == null || invitations.size() == 0) {
            findViewById(R.id.noInvitations).setVisibility(View.VISIBLE);
            return;
        }
        this.findViewById(R.id.scrollContent).setVisibility(View.VISIBLE);
        findViewById(R.id.noInvitations).setVisibility(View.GONE);

        for (Invitation invitation : invitations) {
            Bundle b = new Bundle();
            b.putParcelable("user", user);
            b.putParcelable("invitation", invitation);
            Fragment fragment = InvitationLineActivity.newInstance(b);
            getSupportFragmentManager().beginTransaction().add(R.id.content, fragment, "INVITATION").commit();
        }
    }
}