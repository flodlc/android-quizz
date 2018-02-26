package com.quizz.entities;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.quizz.entities.User;

public class Stat implements Parcelable {

    @SerializedName("myStats")
    @Expose
    private Double myStats;
    @SerializedName("globalStats")
    @Expose
    private Double globalStats;
    @SerializedName("bestUsers")
    @Expose
    private List<User> users = new ArrayList<User>();
    public final static Parcelable.Creator<Stat> CREATOR = new Creator<Stat>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Stat createFromParcel(Parcel in) {
            return new Stat(in);
        }

        public Stat[] newArray(int size) {
            return (new Stat[size]);
        }

    };

    protected Stat(Parcel in) {
        this.myStats = ((Double) in.readValue((Double.class.getClassLoader())));
        this.globalStats = ((Double) in.readValue((Double.class.getClassLoader())));
        in.readList(this.users, (User.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public Stat() {
    }

    /**
     * @param myStats
     * @param users
     * @param globalStats
     */
    public Stat(Double myStats, Double globalStats, List<User> users) {
        super();
        this.myStats = myStats;
        this.globalStats = globalStats;
        this.users = users;
    }

    public Double getMyStats() {
        return myStats;
    }

    public void setMyStats(Double myStats) {
        this.myStats = myStats;
    }

    public Stat withMyStats(Double myStats) {
        this.myStats = myStats;
        return this;
    }

    public Double getGlobalStats() {
        return globalStats;
    }

    public void setGlobalStats(Double globalStats) {
        this.globalStats = globalStats;
    }

    public Stat withGlobalStats(Double globalStats) {
        this.globalStats = globalStats;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Stat withUsers(List<User> users) {
        this.users = users;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(myStats);
        dest.writeValue(globalStats);
        dest.writeList(users);
    }

    public int describeContents() {
        return 0;
    }

}