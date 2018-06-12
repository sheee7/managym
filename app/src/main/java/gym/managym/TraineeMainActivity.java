package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TraineeMainActivity extends AppCompatActivity {
    private Bundle bundle;
    private TraineeData userData;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainee_main);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");

        final TextView welcomeText = findViewById(R.id.welcomeText);
        final TextView pointText = findViewById(R.id.pointText);
        final TextView trainerText = findViewById(R.id.trainerText);

        welcomeText.setText(userData.getUserID()+"("+userData.getName()+") 님 반갑습니다.");
        pointText.setText("현재 포인트 : " + userData.getPoint());

        final Button noticeButton = findViewById(R.id.noticeButton);
        final Button attendButton = findViewById(R.id.attendButton);
        final Button messengerButton = findViewById(R.id.messengerButton);
        final Button gymProgramButton = findViewById(R.id.gymProgramButton);
        final Button bodyDataButton = findViewById(R.id.bodyDataButton);
        final Button workoutDataButton = findViewById(R.id.workoutDataButton);
        final Button pointRecordButton = findViewById(R.id.pointRecordButton);

        noticeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TraineeMainActivity.this, NoticeActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        attendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TraineeMainActivity.this, AttendGymActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        gymProgramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TraineeMainActivity.this, GymProgramActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        messengerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TraineeMainActivity.this, MessengerActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        pointRecordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TraineeMainActivity.this, PointRecordActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        bodyDataButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(TraineeMainActivity.this, BodyDataActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
            }
        });
        workoutDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TraineeMainActivity.this, WorkoutDataActivity.class);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(TraineeMainActivity.this);
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(TraineeMainActivity.this, LoginActivity.class);
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