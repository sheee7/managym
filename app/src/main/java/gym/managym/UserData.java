package gym.managym;

import android.os.Parcel;
import android.os.Parcelable;

class UserData implements Parcelable {
    private String userID;
    private String userPW;
    private String name;
    private String birth;
    private String phone;
    private int weight;
    private int height;
    private int point;
    private int admin;

    public UserData() { } //test

    public UserData(Parcel in) {
        readFromParcel(in);
    }

    public UserData(String userID, String userPW, String name, String birth, String phone, int weight, int height, int point, int admin) {
        this.userID = userID;
        this.userPW = userPW;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.weight = weight;
        this.height = height;
        this.point = point;
        this.admin = admin;
    }

    public static final Creator<UserData> CREATOR = new Creator<UserData>() {
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
    public String getName() {
        return name;
    }
    public String getBirth() {
        return birth;
    }
    public String getPhone() {
        return phone;
    }
    public int getWeight() {
        return weight;
    }
    public int getHeight() {
        return height;
    }
    public int getPoint() {
        return point;
    }
    public int getAdmin() {
        return admin;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeString(this.userPW);
        dest.writeString(this.name);
        dest.writeString(this.birth);
        dest.writeString(this.phone);
        dest.writeInt(this.weight);
        dest.writeInt(this.height);
        dest.writeInt(this.point);
        dest.writeInt(this.admin);
    }
    private void readFromParcel(Parcel in) {
        userID = in.readString();
        userPW = in.readString();
        name = in.readString();
        birth = in.readString();
        phone = in.readString();
        weight = in.readInt();
        height = in.readInt();
        point = in.readInt();
        admin = in.readInt();
    }
}
