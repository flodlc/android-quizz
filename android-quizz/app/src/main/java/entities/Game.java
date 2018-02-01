package entities;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Game implements Parcelable {

    @SerializedName("infos")
    @Expose
    private Infos infos;

    @SerializedName("rounds")
    @Expose
    private List<Round> rounds = null;

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
        this.infos = ((Infos) in.readValue((Infos.class.getClassLoader())));
        in.readList(this.rounds, (entities.Round.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public Game() {
    }

    /**
     * @param infos
     * @param rounds
     */
    public Game(Infos infos, List<Round> rounds) {
        super();
        this.infos = infos;
        this.rounds = rounds;
    }

    public Infos getInfos() {
        return infos;
    }

    public void setInfos(Infos infos) {
        this.infos = infos;
    }

    public Game withInfos(Infos infos) {
        this.infos = infos;
        return this;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public Game withRounds(List<Round> rounds) {
        this.rounds = rounds;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(infos);
        dest.writeList(rounds);
    }

    public int describeContents() {
        return 0;
    }

}