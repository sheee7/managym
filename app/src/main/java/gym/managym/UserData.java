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
    private int admin;

    public UserData() { } //test

    public UserData(Parcel in) {
        readFromParcel(in);
    }

    public UserData(String userID, String userPW, String name, String birth, String phone, int weight, int height, int admin) {
        this.userID = userID;
        this.userPW = userPW;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.weight = weight;
        this.height = height;
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
        dest.writeInt(this.admin);
    }
    void readFromParcel(Parcel in) {
        userID = in.readString();
        userPW = in.readString();
        name = in.readString();
        birth = in.readString();
        phone = in.readString();
        weight = in.readInt();
        height = in.readInt();
        admin = in.readInt();
    }
}

class TraineeData extends UserData {
    private String trainer;
    private int point;

    public TraineeData() {
        super();
    }
    public TraineeData(Parcel in) {
        super(in);
    }
    public TraineeData(String userID, String userPW, String name, String birth, String phone, int weight, int height, int point, String trainer, int admin) {
        super(userID, userPW, name, birth, phone, weight, height, admin);
        this.point = point;
        this.trainer = trainer;
    }

    public String getTrainer() {
        return trainer;
    }
    public int getPoint() {
        return point;
    }

    public static final Creator<TraineeData> CREATOR = new Creator<TraineeData>() {
        public TraineeData createFromParcel(Parcel in) {
            return new TraineeData(in);
        }

        public TraineeData[] newArray (int size) {
            return new TraineeData[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.trainer);
        dest.writeInt(this.point);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
        trainer = in.readString();
        point = in.readInt();
    }
}