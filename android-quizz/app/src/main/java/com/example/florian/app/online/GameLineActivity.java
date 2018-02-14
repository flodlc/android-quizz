package com.example.florian.app.online;

import android.support.v4.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.florian.app.R;
import com.google.common.collect.ImmutableMap;

import entities.Game;
import entities.GameData;
import entities.GameResult;
import entities.Question;
import entities.Response;
import entities.Round;
import entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import services.ApiService;
import services.ApiServiceInterface;
import services.RouterService;

/**
 * Created by Florian on 31/01/2018.
 */

public class GameLineActivity extends Fragment {

    private Game game;
    private User user;

    public static GameLineActivity newInstance(Bundle args) {
        GameLineActivity f = new GameLineActivity();
        f.setArguments(args);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_line, container, false);
        Bundle b = getArguments();
        this.game = b.getParcelable("game");
        this.user = b.getParcelable("user");
        this.displayTexts(view);
        this.setListener(view);
        return view;
    }

    private void setListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApiServiceInterface apiService = ApiService.getService();
                Call<GameData> call = apiService.checkNewGame(ImmutableMap.of("user", String.valueOf(user.getId()),
                        "game", String.valueOf(game.getId())));
                call.enqueue(new Callback<GameData>() {
                    @Override
                    public void onResponse(Call<GameData> call, retrofit2.Response<GameData> response) {
                        GameData gamedata = response.body();
                        RouterService.goResult(getActivity(), user,
                                new GameResult(gamedata.getGame(), gamedata.getRounds()));
                    }

                    @Override
                    public void onFailure(Call<GameData> call, Throwable t) {

                    }
                });
            }
        });
    }

    private void setBakgroundColor(View view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setBackgroundColor(getActivity().getColor(color));
        } else {
            view.findViewById(R.id.answerA).setBackgroundColor(getResources().getColor(color));
        }
    }

    private void setImage(ImageView imageView, int imageId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(getActivity().getDrawable(imageId));
        } else {
            imageView.setImageDrawable(getResources().getDrawable(imageId));
        }
    }

    private void setCorrect(ImageView view, boolean isCorrect) {
        if (isCorrect) {
            setImage(view, R.drawable.thumb_up);
            setBakgroundColor(view, R.color.colorGreen);
        } else {
            setImage(view, R.drawable.thumb_down);
            setBakgroundColor(view, R.color.colorRed);
        }
    }

    private void setAnimation(ImageView imageView) {
        if (game.getWinner() != null) {
            boolean isCorrect = game.getWinner().getUsername().equals(user.getUsername());
            setCorrect(imageView, isCorrect);
        } else {
            setImage(imageView, R.drawable.sablier);
            setBakgroundColor(imageView, R.color.colorGrey);
        }
    }

    private void displayTexts(View view) {
        ((TextView) view.findViewById(R.id.advName)).setText(game.getAdv().getUsername());
        setAnimation((ImageView) view.findViewById(R.id.state));
    }
}
