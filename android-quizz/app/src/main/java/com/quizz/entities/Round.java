package com.quizz.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Round implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("numRound")
    @Expose
    private Integer numRound;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("question")
    @Expose
    private Question question;
    @SerializedName("responseUA")
    @Expose
    private Response responseUA;
    @SerializedName("responseUB")
    @Expose
    private Response responseUB;
    public final static Parcelable.Creator<Round> CREATOR = new Creator<Round>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Round createFromParcel(Parcel in) {
            return new Round(in);
        }

        public Round[] newArray(int size) {
            return (new Round[size]);
        }

    };

    protected Round(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.numRound = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.state = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.question = ((Question) in.readValue((Question.class.getClassLoader())));
        this.responseUA = ((Response) in.readValue((Response.class.getClassLoader())));
        this.responseUB = ((Response) in.readValue((Response.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Round() {
    }

    public Round(Question question) {
        this.question = question;
    }

    /**
     * @param id
     * @param numRound
     * @param state
     * @param question
     * @param responseUB
     * @param responseUA
     */
    public Round(Integer id, Integer numRound, Integer state, Question question, Response responseUA, Response responseUB) {
        super();
        this.id = id;
        this.numRound = numRound;
        this.state = state;
        this.question = question;
        this.responseUA = responseUA;
        this.responseUB = responseUB;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Round withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getNumRound() {
        return numRound;
    }

    public void setNumRound(Integer numRound) {
        this.numRound = numRound;
    }

    public Round withNumRound(Integer numRound) {
        this.numRound = numRound;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Round withState(Integer state) {
        this.state = state;
        return this;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Round withQuestion(Question question) {
        this.question = question;
        return this;
    }

    public Response getResponseUA() {
        return responseUA;
    }

    public void setResponseUA(Response responseUA) {
        this.responseUA = responseUA;
    }

    public Round withResponseUA(Response responseUA) {
        this.responseUA = responseUA;
        return this;
    }

    public Response getResponseUB() {
        return responseUB;
    }

    public void setResponseUB(Response responseUB) {
        this.responseUB = responseUB;
    }

    public Round withResponseUB(Response responseUB) {
        this.responseUB = responseUB;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(numRound);
        dest.writeValue(state);
        dest.writeValue(question);
        dest.writeValue(responseUA);
        dest.writeValue(responseUB);
    }

    public int describeContents() {
        return 0;
    }

}