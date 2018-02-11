package entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Game implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("adv")
    @Expose
    private String adv;
    @SerializedName("winner")
    @Expose
    private User winner;
    @SerializedName("pointsA")
    @Expose
    private Integer pointsA;
    @SerializedName("pointsB")
    @Expose
    private Integer pointsB;
    public final static Parcelable.Creator<Game> CREATOR = new Creator<Game>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        public Game[] newArray(int size) {
            return (new Game[size]);
        }
    };

    protected Game(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.state = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.adv = ((String) in.readValue((String.class.getClassLoader())));
        this.winner = ((User) in.readValue((User.class.getClassLoader())));
        this.pointsA = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.pointsB = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public Game() {
    }

    public Game(Integer id, Integer state, String adv, User winner, Integer pointsA, Integer pointsB) {
        super();
        this.id = id;
        this.state = state;
        this.adv = adv;
        this.winner = winner;
        this.pointsA = pointsA;
        this.pointsB = pointsB;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Game withState(Integer state) {
        this.state = state;
        return this;
    }

    public String getAdv() {
        return adv;
    }

    public void setAdv(String adv) {
        this.adv = adv;
    }

    public Game withAdv(String adv) {
        this.adv = adv;
        return this;
    }

    public User getWinner() {
        return winner;
    }

    public void setWinner(User winner) {
        this.winner = winner;
    }

    public Game withWinner(User winner) {
        this.winner = winner;
        return this;
    }

    public Integer getPointsA() {
        return pointsA;
    }

    public void setPointsA(Integer pointsA) {
        this.pointsA = pointsA;
    }

    public Game withPointsA(Integer pointsA) {
        this.pointsA = pointsA;
        return this;
    }

    public Integer getPointsB() {
        return pointsB;
    }

    public void setPointsB(Integer pointsB) {
        this.pointsB = pointsB;
    }

    public Game withPointsB(Integer pointsB) {
        this.pointsB = pointsB;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(state);
        dest.writeValue(adv);
        dest.writeValue(winner);
        dest.writeValue(pointsA);
        dest.writeValue(pointsB);
    }

    public int describeContents() {
        return 0;
    }
}