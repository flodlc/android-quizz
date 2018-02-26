package com.quizz.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Invitation implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("userFrom")
    @Expose
    private User userFrom;
    @SerializedName("userTo")
    @Expose
    private User userTo;
    @SerializedName("played")
    @Expose
    private Boolean played;
    @SerializedName("adv")
    @Expose
    private User adv;
    public final static Parcelable.Creator<Invitation> CREATOR = new Creator<Invitation>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Invitation createFromParcel(Parcel in) {
            return new Invitation(in);
        }

        public Invitation[] newArray(int size) {
            return (new Invitation[size]);
        }

    }
            ;

    protected Invitation(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.userFrom = ((User) in.readValue((User.class.getClassLoader())));
        this.userTo = ((User) in.readValue((User.class.getClassLoader())));
        this.played = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
        this.adv = ((User) in.readValue((User.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Invitation() {
    }

    /**
     *
     * @param id
     * @param userFrom
     * @param played
     * @param userTo
     * @param adv
     */
    public Invitation(Integer id, User userFrom, User userTo, Boolean played, User adv) {
        super();
        this.id = id;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.played = played;
        this.adv = adv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Invitation withId(Integer id) {
        this.id = id;
        return this;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public Invitation withUserFrom(User userFrom) {
        this.userFrom = userFrom;
        return this;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public Invitation withUserTo(User userTo) {
        this.userTo = userTo;
        return this;
    }

    public Boolean getPlayed() {
        return played;
    }

    public void setPlayed(Boolean played) {
        this.played = played;
    }

    public Invitation withPlayed(Boolean played) {
        this.played = played;
        return this;
    }

    public User getAdv() {
        return adv;
    }

    public void setAdv(User adv) {
        this.adv = adv;
    }

    public Invitation withAdv(User adv) {
        this.adv = adv;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(userFrom);
        dest.writeValue(userTo);
        dest.writeValue(played);
        dest.writeValue(adv);
    }

    public int describeContents() {
        return 0;
    }

}