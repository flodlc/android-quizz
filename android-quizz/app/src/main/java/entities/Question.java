package entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Question implements Parcelable
{

    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("responseA")
    @Expose
    private String responseA;
    @SerializedName("responseB")
    @Expose
    private String responseB;
    @SerializedName("responseC")
    @Expose
    private String responseC;
    @SerializedName("responseD")
    @Expose
    private String responseD;
    @SerializedName("answer")
    @Expose
    private String answer;
    public final static Parcelable.Creator<Question> CREATOR = new Creator<Question>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return (new Question[size]);
        }

    }
            ;

    protected Question(Parcel in) {
        this.question = ((String) in.readValue((String.class.getClassLoader())));
        this.responseA = ((String) in.readValue((String.class.getClassLoader())));
        this.responseB = ((String) in.readValue((String.class.getClassLoader())));
        this.responseC = ((String) in.readValue((String.class.getClassLoader())));
        this.responseD = ((String) in.readValue((String.class.getClassLoader())));
        this.answer = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Question() {
    }

    /**
     *
     * @param answer
     * @param responseD
     * @param responseC
     * @param responseB
     * @param responseA
     * @param question
     */
    public Question(String question, String responseA, String responseB, String responseC, String responseD, String answer) {
        super();
        this.question = question;
        this.responseA = responseA;
        this.responseB = responseB;
        this.responseC = responseC;
        this.responseD = responseD;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Question withQuestion(String question) {
        this.question = question;
        return this;
    }

    public String getResponseA() {
        return responseA;
    }

    public void setResponseA(String responseA) {
        this.responseA = responseA;
    }

    public Question withResponseA(String responseA) {
        this.responseA = responseA;
        return this;
    }

    public String getResponseB() {
        return responseB;
    }

    public void setResponseB(String responseB) {
        this.responseB = responseB;
    }

    public Question withResponseB(String responseB) {
        this.responseB = responseB;
        return this;
    }

    public String getResponseC() {
        return responseC;
    }

    public void setResponseC(String responseC) {
        this.responseC = responseC;
    }

    public Question withResponseC(String responseC) {
        this.responseC = responseC;
        return this;
    }

    public String getResponseD() {
        return responseD;
    }

    public void setResponseD(String responseD) {
        this.responseD = responseD;
    }

    public Question withResponseD(String responseD) {
        this.responseD = responseD;
        return this;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Question withAnswer(String answer) {
        this.answer = answer;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(question);
        dest.writeValue(responseA);
        dest.writeValue(responseB);
        dest.writeValue(responseC);
        dest.writeValue(responseD);
        dest.writeValue(answer);
    }

    public int describeContents() {
        return 0;
    }

}