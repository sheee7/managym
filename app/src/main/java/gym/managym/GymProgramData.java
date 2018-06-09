package gym.managym;

import android.os.Parcel;
import android.os.Parcelable;

class GymProgramData implements Parcelable {
    private String programName;
    private String programContents;
    private String period;
    private int frequency;

    public GymProgramData() { }

    public GymProgramData(Parcel in) {
        readFromParcel(in);
    }

    public GymProgramData(String programName, String programContents, String period, int frequency) {
        this.programName = programName;
        this.programContents = programContents;
        this.period = period;
        this.frequency = frequency;
    }

    public static final Parcelable.Creator<GymProgramData> CREATOR = new Parcelable.Creator<GymProgramData>() {
        public GymProgramData createFromParcel(Parcel in) {
            return new GymProgramData(in);
        }

        public GymProgramData[] newArray (int size) {
            return new GymProgramData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public String getProgramName() {
        return this.programName;
    }

    public String getProgramContents() {
        return this.programContents;
    }

    public String getPeriod() {
        return this.period;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.programName);
        dest.writeString(this.programContents);
        dest.writeString(this.period);
        dest.writeInt(this.frequency);
    }

    private void readFromParcel(Parcel in) {
        this.programName = in.readString();
        this.programContents = in.readString();
        this.period = in.readString();
        this.frequency = in.readInt();
    }
}