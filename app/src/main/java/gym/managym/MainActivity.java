package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");

        final TextView idText = findViewById(R.id.idText);
        final TextView pwText = findViewById(R.id.pwText);
        final TextView nameText = findViewById(R.id.nameText);
        final TextView birthText = findViewById(R.id.birthText);
        final TextView phoneText = findViewById(R.id.phoneText);
        final TextView weightText = findViewById(R.id.weightText);
        final TextView heightText = findViewById(R.id.heightText);
        final TextView pointText = findViewById(R.id.pointText);
        final TextView adminText = findViewById(R.id.adminText);

        idText.setText("ID : " + userData.getUserID());
        pwText.setText("PW : " + userData.getUserPW());
        nameText.setText("Name : " + userData.getName());
        birthText.setText("Birth : " + userData.getBirth());
        phoneText.setText("Phone : " + userData.getPhone());
        weightText.setText("Weight : " + userData.getWeight());
        heightText.setText("Height : " + userData.getHeight());
        pointText.setText("Point : " + userData.getPoint());
        adminText.setText("Admin : " + userData.getAdmin());

        final Button noticeButton = findViewById(R.id.noticeButton);
        final Button userManagementButton = findViewById(R.id.userManagementButton);

        noticeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });

        userManagementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserManagementActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu_default, menu); // getMenuInflater 를 사용해서 xml 파일 가져옴
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_back:
                logout();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
}

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