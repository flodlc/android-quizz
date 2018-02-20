package com.quizz.entities;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostAnswers implements Parcelable {

    @SerializedName("user")
    @Expose
    private Integer user;

    @SerializedName("game")
    @Expose
    private Integer game;

    @SerializedName("answers")
    @Expose
    private List<Answer> answers = new ArrayList<>();

    public final static Parcelable.Creator<PostAnswers> CREATOR = new Creator<PostAnswers>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PostAnswers createFromParcel(Parcel in) {
            return new PostAnswers(in);
        }

        public PostAnswers[] newArray(int size) {
            return (new PostAnswers[size]);
        }

    };

    protected PostAnswers(Parcel in) {
        this.user = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.game = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.answers, (com.quizz.entities.Answer.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public PostAnswers() {
    }

    /**
     * @param answers
     * @param game
     * @param user
     */
    public PostAnswers(Integer user, Integer game, List<Answer> answers) {
        super();
        this.user = user;
        this.game = game;
        this.answers = answers;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public PostAnswers withUser(Integer user) {
        this.user = user;
        return this;
    }

    public Integer getGame() {
        return game;
    }

    public void setGame(Integer game) {
        this.game = game;
    }

    public PostAnswers withGame(Integer game) {
        this.game = game;
        return this;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public PostAnswers withAnswers(List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(user);
        dest.writeValue(game);
        dest.writeList(answers);
    }

    public int describeContents() {
        return 0;
    }

}

