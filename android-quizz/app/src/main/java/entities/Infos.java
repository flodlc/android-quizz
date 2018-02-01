package entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Infos implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("state")
    @Expose
    private Integer state;

    @SerializedName("adv")
    @Expose
    private String adv;

    public final static Parcelable.Creator<Infos> CREATOR = new Creator<Infos>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Infos createFromParcel(Parcel in) {
            return new Infos(in);
        }

        public Infos[] newArray(int size) {
            return (new Infos[size]);
        }

    }
            ;

    protected Infos(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.state = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.adv = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Infos() {
    }

    /**
     *
     * @param id
     * @param state
     * @param adv
     */
    public Infos(Integer id, Integer state, String adv) {
        super();
        this.id = id;
        this.state = state;
        this.adv = adv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Infos withId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Infos withState(Integer state) {
        this.state = state;
        return this;
    }

    public String getAdv() {
        return adv;
    }

    public void setAdv(String adv) {
        this.adv = adv;
    }

    public Infos withAdv(String adv) {
        this.adv = adv;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(state);
        dest.writeValue(adv);
    }

    public int describeContents() {
        return 0;
    }

}