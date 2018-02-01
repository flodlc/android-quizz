package entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Answer implements Parcelable
{

    @SerializedName("roundId")
    @Expose
    private Integer roundId;
    @SerializedName("answer")
    @Expose
    private String answer;
    public final static Parcelable.Creator<Answer> CREATOR = new Creator<Answer>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        public Answer[] newArray(int size) {
            return (new Answer[size]);
        }

    }
            ;

    protected Answer(Parcel in) {
        this.roundId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.answer = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Answer() {
    }

    /**
     *
     * @param answer
     * @param roundId
     */
    public Answer(Integer roundId, String answer) {
        super();
        this.roundId = roundId;
        this.answer = answer;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }

    public Answer withRoundId(Integer roundId) {
        this.roundId = roundId;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Answer withAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(roundId);
        dest.writeValue(answer);
    }

    public int describeContents() {
        return 0;
    }

}