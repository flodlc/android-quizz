package entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("plainPassword")
    @Expose
    private String plainPassword;
    @SerializedName("plainPasswordVerif")
    @Expose
    private String plainPasswordVerif;
    @SerializedName("record")
    @Expose
    private Integer record;
    public final static Parcelable.Creator<User> CREATOR = new Creator<User>() {


        @SuppressWarnings({
                "unchecked"
        })
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return (new User[size]);
        }

    };

    protected User(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.username = ((String) in.readValue((String.class.getClassLoader())));
        this.plainPassword = ((String) in.readValue((String.class.getClassLoader())));
        this.plainPasswordVerif = ((String) in.readValue((String.class.getClassLoader())));
        this.record = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public User() {
    }

    /**
     * @param plainPasswordVerif
     * @param id
     * @param record
     * @param username
     * @param plainPassword
     */
    public User(Integer id, String username, String plainPassword, String plainPasswordVerif, Integer record) {
        super();
        this.id = id;
        this.username = username;
        this.plainPassword = plainPassword;
        this.plainPasswordVerif = plainPasswordVerif;
        this.record = record;
    }

    /**
     * @param username
     * @param plainPassword
     */
    public User(String username, String plainPassword) {
        super();
        this.username = username;
        this.plainPassword = plainPassword;
    }

    /**
     * @param plainPasswordVerif
     * @param username
     * @param plainPassword
     */
    public User(String username, String plainPassword, String plainPasswordVerif) {
        super();
        this.username = username;
        this.plainPassword = plainPassword;
        this.plainPasswordVerif = plainPasswordVerif;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User withUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
    }

    public User withPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
        return this;
    }

    public String getPlainPasswordVerif() {
        return plainPasswordVerif;
    }

    public void setPlainPasswordVerif(String plainPasswordVerif) {
        this.plainPasswordVerif = plainPasswordVerif;
    }

    public User withPlainPasswordVerif(String plainPasswordVerif) {
        this.plainPasswordVerif = plainPasswordVerif;
        return this;
    }

    public Integer getRecord() {
        return record;
    }

    public void setRecord(Integer record) {
        this.record = record;
    }

    public User withRecord(Integer record) {
        this.record = record;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(username);
        dest.writeValue(plainPassword);
        dest.writeValue(plainPasswordVerif);
        dest.writeValue(record);
    }

    public int describeContents() {
        return 0;
    }

}
