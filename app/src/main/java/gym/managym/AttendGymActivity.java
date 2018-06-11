package gym.managym;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AttendGymActivity extends AppCompatActivity{
    private Bundle bundle;
    private UserData userData;
    private AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendgym);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        final EditText workoutDataName = findViewById(R.id.workoutDataName);
        final EditText workoutStartTime = findViewById(R.id.startTime);
        final EditText workoutEndTime = findViewById(R.id.endTime);
        final EditText workoutStrength = findViewById(R.id.strengthText);
        final Button attendButton = findViewById(R.id.attendButton);

        attendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String dataName = workoutDataName.getText().toString();
                final String userID = userData.getUserID();
                final String startTime = workoutStartTime.getText().toString();
                final String endTime = workoutEndTime.getText().toString();
                final String strength = workoutStrength.getText().toString();

                if(workoutDataName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendGymActivity.this);
                    dialog = builder.setMessage("제목를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if(workoutStartTime.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendGymActivity.this);
                    dialog = builder.setMessage("시작시간을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if(workoutEndTime.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendGymActivity.this);
                    dialog = builder.setMessage("종료시간을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                final int time = setTime(startTime, endTime);
                if(workoutStrength.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AttendGymActivity.this);
                    dialog = builder.setMessage("운동강도를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AttendGymActivity.this);
                                builder.setMessage("운동 데이터를 추가하시겠습니까?\n(포인트가 차감됩니다.)");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int i) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create();
                                builder.show();

                                String date = jsonResponse.getString("date");
                                Log.d("TAG", date);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AttendGymActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                WorkoutDataAdd workoutDataAdd = new WorkoutDataAdd(dataName, userID, startTime, endTime, time, strength, responseListener);
                RequestQueue queue = Volley.newRequestQueue(AttendGymActivity.this);
                queue.add(workoutDataAdd);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Action Bar Setting
        getMenuInflater().inflate(R.menu.activity_menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_back:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    int setTime(String startTime, String endTime) {
        int time;
        String[] startTimeSet = startTime.split(":");
        String[] endTimeSet = endTime.split(":");
        time = (Integer.parseInt(endTimeSet[0]) - Integer.parseInt(startTimeSet[0])) * 60
                + Integer.parseInt(endTimeSet[1]) - Integer.parseInt(startTimeSet[1]);
        return time;
    }
}

class WorkoutDataAdd extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/WorkoutDataAdd.php";
    private Map<String, String> parameters;

    public WorkoutDataAdd(String dataName, String userID, String startTime, String endTime, int time, String strength, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("table", "WORKOUTDATA_"+userID);
        parameters.put("dataName", dataName);
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);
        parameters.put("time", String.valueOf(time));
        parameters.put("strength", strength);
        Log.d("TAG", "WORKOUTDATA_"+userID);
        Log.d("TAG", dataName);
        Log.d("TAG", startTime);
        Log.d("TAG", endTime);
        Log.d("TAG", String.valueOf(time));
        Log.d("TAG", strength);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
