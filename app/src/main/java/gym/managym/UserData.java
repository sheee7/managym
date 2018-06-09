package gym.managym;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

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

class TrainerData extends UserData {
    ArrayList<String> myTraineeIDs;

    public TrainerData() { super(); } //test

    public TrainerData(Parcel in) {
        super(in);
    }

    public TrainerData(String userID, String userPW, String name, String birth, String phone, int weight, int height, int point, int admin) {
        super(userID, userPW, name, birth, phone, weight, height, point, admin);
        // Trainee ID를 받는 부분이 여기 들어가야함.

    }

}

class TraineeData extends UserData {
    private String myTrainerID; //담당 트레이너의 ID

    public TraineeData() { super(); } //test

    public TraineeData(Parcel in) {
        super(in);
    }

    public TraineeData(String userID, String userPW, String name, String birth, String phone, int weight, int height, int point, int admin, String myTrainerID) {
        super(userID, userPW, name, birth, phone, weight, height, point, admin);
        this.myTrainerID = myTrainerID;
    }

    public String getMyTrainerID() { return myTrainerID; }
}