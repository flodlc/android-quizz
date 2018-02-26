package com.quizz.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfflineGame implements Parcelable {

    @SerializedName("score")
    @Expose
    private Integer score;
    @SerializedName("time")
    @Expose
    private Integer time;
    public final static Parcelable.Creator<OfflineGame> CREATOR = new Creator<OfflineGame>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OfflineGame createFromParcel(Parcel in) {
            return new OfflineGame(in);
        }

        public OfflineGame[] newArray(int size) {
            return (new OfflineGame[size]);
        }

    };

    protected OfflineGame(Parcel in) {
        this.score = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.time = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public OfflineGame() {
    }

    /**
     * @param time
     * @param score
     */
    public OfflineGame(Integer score, Integer time) {
        super();
        this.score = score;
        this.time = time;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public OfflineGame withScore(Integer score) {
        this.score = score;
        return this;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public OfflineGame withTime(Integer time) {
        this.time = time;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(score);
        dest.writeValue(time);
    }

    public int describeContents() {
        return 0;
    }
}