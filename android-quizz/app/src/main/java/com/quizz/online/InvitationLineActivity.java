package com.quizz.online;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;
import com.quizz.R;

import com.quizz.entities.GameData;
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
    private AlertDialog alertDialog;

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

    public View onStartView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invitation_line, container, false);
        Bundle b = getArguments();
        this.invitation = b.getParcelable("invitation");
        this.user = b.getParcelable("user");
        this.displayTexts(view);
        return view;
    }

    private void makeAlertDilogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.invit_removed));
        builder.setIcon(R.drawable.sablier);
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }

    private void setAcceptListener(ImageView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<GameData> call = apiService.acceptInvitation(ImmutableMap.of("invitation", String.valueOf(invitation.getId())));

                call.enqueue(new Callback<GameData>() {
                    @Override
                    public void onResponse(Call<GameData> call, retrofit2.Response<GameData> response) {
                        if (response.code() == 200) {
                            GameData gamedata = response.body();
                            RouterService.goTimer(getActivity(), user, gamedata);
                        } else if (response.code() == 404) {
                            makeAlertDilogue();
                            getFragmentManager().beginTransaction()
                                    .remove(InvitationLineActivity.this).commit();
                        } else {
                            ApiService.showErrorMessage(InvitationLineActivity.this.getActivity());
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

    private void setRefuseListener(ImageView view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<Boolean> call = apiService.refuseInvitation(ImmutableMap.of("invitation", String.valueOf(invitation.getId())));
                call.enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, retrofit2.Response<Boolean> response) {
                        if (ApiService.checkCode(getActivity(), response)) {
                            getFragmentManager().beginTransaction()
                                    .remove(InvitationLineActivity.this).commit();
                        }
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {
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
        ImageView refuse = view.findViewById(R.id.refuse);
        ImageView accept = view.findViewById(R.id.accept);
        TextView stateWait = view.findViewById(R.id.stateWait);
        if (hasChoice) {
            typeUser.setText(getResources().getString(R.string.invit_from));
        } else {
            typeUser.setText(getResources().getString(R.string.invit_to));
        }
        ((TextView) view.findViewById(R.id.advName)).setText(invitation.getAdv().getUsername());
        stateWait.setVisibility(View.GONE);


        if (invitation.getPlayed()) {
            this.setAcceptListener(accept);
            refuse.setVisibility(View.GONE);
            setImage(accept, R.drawable.ic_accept);
        } else {
            this.setRefuseListener(refuse);
            setImage(refuse, R.drawable.ic_refuse);
            if (hasChoice) {
                this.setAcceptListener(accept);
                setImage(accept, R.drawable.ic_accept);
            } else {
                stateWait.setVisibility(View.VISIBLE);
                accept.setVisibility(View.GONE);
            }
        }
    }
}
