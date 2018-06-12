package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrainerMainActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_main);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");

        final TextView welcomeText = findViewById(R.id.welcomeText);
        final TextView adminTet = findViewById(R.id.adminText);

        welcomeText.setText(userData.getUserID()+"("+userData.getName()+") 님 반갑습니다.");
        adminTet.setText("관리자 계정");

        final Button gymProgramButton = findViewById(R.id.gymProgramButton);
        final Button noticeButton = findViewById(R.id.noticeButton);
        final Button userManagementButton = findViewById(R.id.userManagementButton);
        final Button messengerButton = findViewById(R.id.messengerButton);
        final Button managePointButton = findViewById(R.id.managePointButton);

        gymProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TrainerMainActivity.this, GymProgramActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });

        noticeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TrainerMainActivity.this, NoticeActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });

        userManagementButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TrainerMainActivity.this, UserManagementActivity.class);
                startActivity(intent);
            }
        });

        messengerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TrainerMainActivity.this, MessengerActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        managePointButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TrainerMainActivity.this, PointWriteActivity.class);
                intent.putExtra("userData", userData);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TrainerMainActivity.this);
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(TrainerMainActivity.this, LoginActivity.class);
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

