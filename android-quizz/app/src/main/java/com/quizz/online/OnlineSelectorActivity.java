package com.quizz.online;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.quizz.R;
import com.google.common.collect.ImmutableMap;

import com.quizz.entities.GameData;
import com.quizz.entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.quizz.services.ApiService;
import com.quizz.services.ApiServiceInterface;
import com.quizz.services.RouterService;


public class OnlineSelectorActivity extends Fragment {

    private User user;
    private AlertDialog alertDialog;
    private ApiServiceInterface apiService;
    private GameData currentGameData;
    private Integer nbInvitations;
    private boolean searchingGame;

    public static OnlineSelectorActivity newInstance(Bundle args) {
        OnlineSelectorActivity f = new OnlineSelectorActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.online_selector, container, false);
        apiService = ApiService.getService();
        Bundle b = getArguments();
        this.user = b.getParcelable("user");
        setIntent(view.findViewById(R.id.onlineButton));

        (view.findViewById(R.id.diplayGames)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goDisplayGames(getActivity(), user);
            }
        });
        (view.findViewById(R.id.sendInvit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goSendInvitation(getActivity(), user);
            }
        });
        (view.findViewById(R.id.diplayInvitations)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RouterService.goDisplayInvitations(getActivity(), user);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Call<Integer> call = apiService.getnbCurrentGame();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(@NonNull Call<Integer> call,
                                   @NonNull Response<Integer> response) {
                if (ApiService.checkCode(getActivity(), response)) {
                    int nbCurrentGame = response.body();
                    if (nbCurrentGame > 0) {
                        if (getView() != null) {
                            ((Button) getView().findViewById(R.id.onlineButtonNotif))
                                    .setText(String.valueOf(nbCurrentGame));

                            getView().findViewById(R.id.onlineButtonNotif).setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (getView() != null) {
                            getView().findViewById(R.id.onlineButtonNotif).setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                if (getView() != null) {
                    getView().findViewById(R.id.onlineButtonNotif).setVisibility(View.GONE);
                }
            }
        });

        Call<Integer> call2 = apiService.getNbInvitations();
        call2.enqueue(new Callback<Integer>() {
            Button invitNotif = getView().findViewById(R.id.invitButtonNotif);
            @Override
            public void onResponse(@NonNull Call<Integer> call,
                                   @NonNull Response<Integer> response) {
                if (ApiService.checkCode(getActivity(), response)) {
                    nbInvitations = response.body();
                    if (nbInvitations > 0) {
                        invitNotif.setVisibility(View.VISIBLE);
                        invitNotif.setText(String.valueOf(nbInvitations));

                        invitNotif.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                RouterService.goDisplayInvitations(getActivity(), user);
                            }
                        });
                    }
                    else {
                        invitNotif.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                invitNotif.setVisibility(View.GONE);
            }
        });
    }

    private void checkNewGame(final long time1) {
        if (!searchingGame || currentGameData == null
                || currentGameData.getGame() == null
                || currentGameData.getGame().getId() == null) {
            return;
        }
        Call<GameData> call = apiService.checkNewGame(ImmutableMap.of("game", String.valueOf(currentGameData.getGame().getId())));

        call.enqueue(new Callback<GameData>() {
            @Override
            public void onResponse(@NonNull Call<GameData> call,
                                   @NonNull Response<GameData> response) {
                if (ApiService.checkCode(getActivity(), response)) {
                    GameData freshGameData = response.body();
                    if (freshGameData != null && freshGameData.getGame().getState() == 1) {
                        RouterService.goTimer(getActivity(), user, freshGameData);
                        alertDialog.dismiss();
                    } else if (SystemClock.elapsedRealtime() - time1 < 20000) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        checkNewGame(time1);
                    } else {
                        alertDialog.dismiss();
                        searchingGame = false;
                    }
                } else {
                    alertDialog.dismiss();
                    searchingGame = false;
                }
            }

            @Override
            public void onFailure(Call<GameData> call, Throwable t) {
                alertDialog.dismiss();
                searchingGame = false;
            }
        });
    }

    private void makeAlertDilogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.waitPlayer));
        builder.setIcon(R.drawable.sablier);
        builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (currentGameData != null && currentGameData.getGame() != null) {
                    Call<Boolean> call = apiService.deleteGame(String.valueOf(currentGameData.getGame().getId()));
                    call.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            ApiService.checkCode(getActivity(), response);
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            ApiService.showErrorMessage(OnlineSelectorActivity.this.getActivity());
                        }
                    });
                    searchingGame = false;
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog = builder.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
    }

    private void makeBadConnectionAlertDilogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.badConnection));
        builder.setPositiveButton("OK", null);
        alertDialog = builder.show();
    }

    private void getNewGame() {
        if(!ApiService.isConnected()) {
            makeBadConnectionAlertDilogue();
            return;
        }
        makeAlertDilogue();
        Call<GameData> call = apiService.getNewGame();
        final long time1 = SystemClock.elapsedRealtime();
        call.enqueue(new Callback<GameData>() {
            @Override
            public void onResponse(@NonNull Call<GameData> call,
                                   @NonNull Response<GameData> response) {
                if (ApiService.checkCode(getActivity(), response)) {
                    currentGameData = response.body();
                    if (currentGameData != null && currentGameData.getGame().getState() == 1) {
                        RouterService.goTimer(getActivity(), user, response.body());
                        alertDialog.dismiss();
                    } else {
                        searchingGame = true;
                        checkNewGame(time1);
                    }
                } else {
                    alertDialog.dismiss();
                    searchingGame = false;
                }
            }

            @Override
            public void onFailure(Call<GameData> call, Throwable t) {
                ApiService.showErrorMessage(OnlineSelectorActivity.this.getActivity());
                alertDialog.dismiss();
                searchingGame = false;
            }
        });
    }

    private void setIntent(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getNewGame();
            }
        });
    }
}