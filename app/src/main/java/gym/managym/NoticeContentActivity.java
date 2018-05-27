package gym.managym;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class NoticeContentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);

        final TextView titleText = findViewById(R.id.noticeTitleText);
        final TextView nameText = findViewById(R.id.noticeNameText);
        final TextView dateText = findViewById(R.id.noticeDateText);
        final TextView contentText = findViewById(R.id.noticeContentText);

        //Intent intent = this.getIntent();
        //String content = intent.getStringExtra("noticeData");

        Bundle bundle = getIntent().getExtras();
        NoticeData noticeData = bundle.getParcelable("noticeData");

        titleText.setText(noticeData.getTitle());
        nameText.setText(noticeData.getName());
        dateText.setText(noticeData.getDate());
        contentText.setText(noticeData.getContent());
    }
}

class NoticeData implements Parcelable {
    private String title;
    private String name;
    private String date;
    private String content;

    public NoticeData() { }

    public NoticeData(Parcel in) {
        readFromParcel(in);
    }

    public NoticeData(String title, String name, String date, String content) {
        this.title = title;
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public static final Parcelable.Creator<NoticeData> CREATOR = new Parcelable.Creator<NoticeData>() {
        public NoticeData createFromParcel(Parcel in) {
            return new NoticeData(in);
        }

        public NoticeData[] newArray (int size) {
            return new NoticeData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getContent() {
        return content;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.name);
        dest.writeString(this.date);
        dest.writeString(this.content);
    }
    private void readFromParcel(Parcel in) {
        title = in.readString();
        name = in.readString();
        date = in.readString();
        content = in.readString();
    }
}