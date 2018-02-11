package entities;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameResult implements Parcelable {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("answers")
    @Expose
    private List<Answer> answers = null;
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
        this.user = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.answers, (entities.Answer.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public GameResult() {
    }

    /**
     * @param answers
     * @param user
     */
    public GameResult(String user, List<Answer> answers) {
        super();
        this.user = user;
        this.answers = answers;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public GameResult withUser(String user) {
        this.user = user;
        return this;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public GameResult withAnswers(List<Answer> answers) {
        this.answers = answers;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(user);
        dest.writeList(answers);
    }

    public int describeContents() {
        return 0;
    }

}