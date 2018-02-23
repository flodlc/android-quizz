package com.quizz.online;

import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.quizz.R;
import com.google.common.collect.ImmutableMap;

import com.quizz.entities.GameData;
import com.quizz.entities.GameResult;
import com.quizz.entities.Invitation;
import com.quizz.entities.User;

import retrofit2.Call;
import retrofit2.Callback;

import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;

/**
 * Created by Lucas on 22/02/2018.
 */

public class InvitationLineActivity extends Fragment {

    private Invitation invitation;
    private User user;

    public static InvitationLineActivity newInstance(Bundle args) {
        InvitationLineActivity f = new InvitationLineActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation_line, container, false);
        Bundle b = getArguments();
        this.invitation = b.getParcelable("invitation");
        this.user = b.getParcelable("user");
        this.displayTexts(view);
        return view;
    }

    private void setListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<GameData> call = apiService.checkNewGame(ImmutableMap.of("invitation", String.valueOf(invitation.getId())));
                call.enqueue(new Callback<GameData>() {
                    @Override
                    public void onResponse(Call<GameData> call, retrofit2.Response<GameData> response) {
                        if (ApiService.checkCode(getActivity(), response)) {
                            GameData gamedata = response.body();
                            RouterService.goResult(getActivity(), user,
                                    new GameResult(gamedata.getGame(), gamedata.getRounds()));
                        }

                    }

                    @Override
                    public void onFailure(Call<GameData> call, Throwable t) {
                        ApiService.showErrorMessage(InvitationLineActivity.this.getActivity());
                    }
                });
            }
        });
    }

    private void setImage(ImageView imageView, int imageId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(getActivity().getDrawable(imageId));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(imageId));
        }
    }


    private void displayTexts(View view) {
        boolean hasChoice = (invitation.getUserFrom().getUsername().equals(invitation.getAdv().getUsername()));
        TextView typeUser = view.findViewById(R.id.typeUser);
        ImageView invit1 = view.findViewById(R.id.invit1);
        ImageView invit2 = view.findViewById(R.id.invit2);
        if (hasChoice) {
            typeUser.setText(getResources().getString(R.string.invit_from));
        } else {
            typeUser.setText(getResources().getString(R.string.invit_to));
        }
        ((TextView) view.findViewById(R.id.advName)).setText(invitation.getAdv().getUsername());


        if (invitation.getPlayed()) {
            invit1.setVisibility(View.GONE);
            setImage(invit2, R.drawable.ic_accept);
        } else {
            if (hasChoice) {
                setImage(invit1, R.drawable.ic_refuse);
                setImage(invit2, R.drawable.ic_accept);
            } else {
                invit1.setVisibility(View.GONE);
                invit2.setVisibility(View.GONE);
            }
        }
    }
}
