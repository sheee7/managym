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

        final TextView idText = findViewById(R.id.idText);
        final TextView pwText = findViewById(R.id.pwText);
        final TextView info1Text = findViewById(R.id.info1Text);
        final TextView info2Text = findViewById(R.id.info2Text);
        final TextView info3Text = findViewById(R.id.info3Text);
        final TextView info4Text = findViewById(R.id.info4Text);

        Bundle bundle = getIntent().getExtras();
        UserData userData = bundle.getParcelable("userData");

        idText.setText(userData.getUserID());
        pwText.setText(userData.getUserPW());
        info1Text.setText(userData.getInfo1());
        info2Text.setText(userData.getInfo2());
        info3Text.setText(userData.getInfo3());
        info4Text.setText(userData.getInfo4());
        }
    }