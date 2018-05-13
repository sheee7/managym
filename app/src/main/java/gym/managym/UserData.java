package gym.managym;

import android.os.Parcel;
import android.os.Parcelable;

public class UserData implements Parcelable {
    private String userID;
    private String userPW;
    private String info1;
    private String info2;
    private String info3;
    private String info4;

    public UserData() { }

    public UserData(Parcel in) {
        readFromParcel(in);
        }

    public UserData(String userID, String userPW, String info1, String info2, String info3, String info4) {
        this.userID = userID;
        this.userPW = userPW;
        this.info1 = info1;
        this.info2 = info2;
        this.info3 = info3;
        this.info4 = info4;
    }

    public static final Parcelable.Creator<UserData> CREATOR = new Parcelable.Creator<UserData>() {
        public UserData createFromParcel(Parcel in) {
            return new UserData(in);
            }

        public UserData[] newArray (int size) {
            return new UserData[size];
            }
       };

    public int describeContents() {
        return 0;
    }

    public String getUserID() {
        return userID;
    }
    public String getUserPW() {
        return userPW;
    }
    public String getInfo1() {
        return info1;
    }
    public String getInfo2() {
        return info2;
    }
    public String getInfo3() {
        return info3;
    }
    public String getInfo4() {
        return info4;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeString(this.userPW);
        dest.writeString(this.info1);
        dest.writeString(this.info2);
        dest.writeString(this.info3);
        dest.writeString(this.info4);
    }
    private void readFromParcel(Parcel in) {
        userID = in.readString();
        userPW = in.readString();
        info1 = in.readString();
        info2 = in.readString();
        info3 = in.readString();
        info4 = in.readString();
    }
}