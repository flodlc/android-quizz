/**
 * Created by lucas on 26/02/2018.
 */
package com.quizz.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitationSend implements Parcelable
{

    @SerializedName("to")
    @Expose
    private String to;
    public final static Parcelable.Creator<InvitationSend> CREATOR = new Creator<InvitationSend>() {


        @SuppressWarnings({
                "unchecked"
        })
        public InvitationSend createFromParcel(Parcel in) {
            return new InvitationSend(in);
        }

        public InvitationSend[] newArray(int size) {
            return (new InvitationSend[size]);
        }

    }
            ;

    public InvitationSend(Parcel in) {
        this.to = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     *
     * @param to
     */
    public InvitationSend(String to) {
        super();
        this.to = to;
    }

    public InvitationSend() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public InvitationSend withTo(String to) {
        this.to = to;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(to);
    }

    public int describeContents() {
        return 0;
    }

}