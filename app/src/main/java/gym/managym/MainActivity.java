package gym.managym;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView idText = findViewById(R.id.idText);
        TextView pwText = findViewById(R.id.pwText);
        TextView info1Text = findViewById(R.id.info1Text);
        TextView info2Text = findViewById(R.id.info2Text);
        TextView info3Text = findViewById(R.id.info3Text);
        TextView info4Text = findViewById(R.id.info4Text);

        Intent intent = getIntent();

        String userID = intent.getStringExtra("userID");
        String userPW = intent.getStringExtra("userPW");
        String info1 = intent.getStringExtra("info1");
        String info2 = intent.getStringExtra("info2");
        String info3 = intent.getStringExtra("info3");
        String info4 = intent.getStringExtra("info4");

        idText.setText(userID);
        pwText.setText(userPW);
        info1Text.setText(info1);
        info2Text.setText(info2);
        info3Text.setText(info3);
        info4Text.setText(info4);
    }
}


class User { // Unused
    String userID;
    String userPW;
    String info1;
    String info2;
    String info3;
    String info4;

    public User(String userID, String userPW, String info1, String info2, String info3, String info4) {
        this.userID = userID;
        this.userPW = userPW;
        this.info1 = info1;
        this.info2 = info2;
        this.info3 = info3;
        this.info4 = info4;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public String getInfo3() {
        return info3;
    }

    public void setInfo3(String info3) {
        this.info3 = info3;
    }

    public String getInfo4() {
        return info4;
    }

    public void setInfo4(String info4) {
        this.info4 = info4;
    }
}