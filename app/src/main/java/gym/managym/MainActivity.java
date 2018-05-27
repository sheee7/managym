package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        final TextView nameText = findViewById(R.id.nameText);
        final TextView birthText = findViewById(R.id.birthText);
        final TextView phoneText = findViewById(R.id.phoneText);
        final TextView weightText = findViewById(R.id.weightText);
        final TextView heightText = findViewById(R.id.heightText);
        final TextView pointText = findViewById(R.id.pointText);
        final TextView adminText = findViewById(R.id.adminText);

        Bundle bundle = getIntent().getExtras();
        UserData userData = bundle.getParcelable("userData");

        idText.setText("ID : " + userData.getUserID());
        pwText.setText("PW : " + userData.getUserPW());
        nameText.setText("Name : " + userData.getName());
        birthText.setText("Birth : " + userData.getBirth());
        phoneText.setText("Phone : " + userData.getPhone());
        weightText.setText("Weight : " + userData.getWeight());
        heightText.setText("Height : " + userData.getHeight());
        pointText.setText("Point : " + userData.getPoint());
        adminText.setText("Admin : " + userData.getAdmin());

        final Button logoutButton = findViewById(R.id.logoutButton);
        final Button noticeButton = findViewById(R.id.noticeButton);
        final Button userManagementButton = findViewById(R.id.userManagementButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        });

        noticeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
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
}