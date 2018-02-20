package com.quizz.entities;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameResult implements Parcelable {

    @SerializedName("game")
    @Expose
    private Game game;
    @SerializedName("rounds")
    @Expose
    private List<Round> rounds = new ArrayList<>();
    public final static Parcelable.Creator<GameResult> CREATOR = new Creator<GameResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GameResult createFromParcel(Parcel in) {
            return new GameResult(in);
        }

        public GameResult[] newArray(int size) {
            return (new GameResult[size]);
        }

    };

    protected GameResult(Parcel in) {
        this.game = ((Game) in.readValue((Game.class.getClassLoader())));
        in.readList(this.rounds, (com.quizz.entities.Round.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public GameResult() {
    }

    /**
     * @param rounds
     * @param game
     */
    public GameResult(Game game, List<Round> rounds) {
        super();
        this.game = game;
        this.rounds = rounds;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameResult withGame(Game game) {
        this.game = game;
        return this;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public GameResult withRounds(List<Round> rounds) {
        this.rounds = rounds;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(game);
        dest.writeList(rounds);
    }

    public int describeContents() {
        return 0;
    }

}