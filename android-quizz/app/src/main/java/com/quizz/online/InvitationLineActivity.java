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

    private void setBakgroundColor(View view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getActivity().getColor(color));
        } else {
            view.setBackgroundColor(getResources().getColor(color));
        }
    }

    private void setImage(ImageView imageView, int imageId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(getActivity().getDrawable(imageId));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(imageId));
        }
    }

    private void setCorrect(ImageView view, String winStatus) {
        if (winStatus.equals("WIN")) {
            setImage(view, R.drawable.thumb_up);
            setBakgroundColor(view, R.color.colorGreen);
        } else if (winStatus.equals("LOSE")) {
            setImage(view, R.drawable.thumb_down);
            setBakgroundColor(view, R.color.colorRed);
        } else {
            setImage(view, R.drawable.equal);
        }
    }

    private void setAnimation(ImageView imageView) {
        if (invitation.getPlayed()) {
            /**
             * DOIT JOUER
             */
            setCorrect(imageView, "WIN");
        } else {
            /**
             * ATTENTE DE L'ADVERSAIRE
             */
            setCorrect(imageView, "LOSE");
        }
    }

    private void displayTexts(View view) {
        ((TextView) view.findViewById(R.id.advName)).setText(invitation.getAdv().getUsername());
        setAnimation((ImageView) view.findViewById(R.id.state));
    }
}
