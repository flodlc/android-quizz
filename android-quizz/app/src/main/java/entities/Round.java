package entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Round implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("numRound")
    @Expose
    private Integer numRound;

    @SerializedName("question")
    @Expose
    private Question question;

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

    }
            ;

    protected Round(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.numRound = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.question = ((Question) in.readValue((Question.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Round() {
    }

    /**
     *
     * @param id
     * @param numRound
     * @param question
     */
    public Round(Integer id, Integer numRound, Question question) {
        super();
        this.id = id;
        this.numRound = numRound;
        this.question = question;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(numRound);
        dest.writeValue(question);
    }

    public int describeContents() {
        return 0;
    }

}

