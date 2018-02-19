package entities;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameData implements Parcelable {

    @SerializedName("game")
    @Expose
    private Game game;

    @SerializedName("rounds")
    @Expose
    private List<Round> rounds = new ArrayList<Round>();

    public final static Parcelable.Creator<GameData> CREATOR = new Creator<GameData>() {


        @SuppressWarnings({
                "unchecked"
        })
        public GameData createFromParcel(Parcel in) {
            return new GameData(in);
        }

        public GameData[] newArray(int size) {
            return (new GameData[size]);
        }

    };

    protected GameData(Parcel in) {
        this.game = ((Game) in.readValue((Game.class.getClassLoader())));
        in.readList(this.rounds, (entities.Round.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     */
    public GameData() {
    }

    /**
     * @param game
     * @param rounds
     */
    public GameData(Game game, List<Round> rounds) {
        super();
        this.game = game;
        this.rounds = rounds;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public GameData withInfos(Game game) {
        this.game = game;
        return this;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public GameData withRounds(List<Round> rounds) {
        this.rounds = rounds;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(game);
        dest.writeList(rounds);
    }

    public int describeContents() {
        return 0;
    }

}