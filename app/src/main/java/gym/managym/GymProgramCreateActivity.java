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
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GymProgramCreateActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private GymProgramData programData;
    private boolean create;
    private AlertDialog dialog;
    private GymProgramActivity gymProgramActivity = (GymProgramActivity) GymProgramActivity.gymProgramActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gymprogram_create);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        create = getIntent().getBooleanExtra("create", true);
        final EditText programCreateName = findViewById(R.id.programCreateName);
        final EditText createStartTime = findViewById(R.id.programCreateStartTime);
        final EditText createEndTime = findViewById(R.id.programCreateEndTime);
        final CheckBox [] checkBoxes = new CheckBox[7];
        checkBoxes[0] = findViewById(R.id.sun);
        checkBoxes[1] = findViewById(R.id.mon);
        checkBoxes[2] = findViewById(R.id.tue);
        checkBoxes[3] = findViewById(R.id.wed);
        checkBoxes[4] = findViewById(R.id.thu);
        checkBoxes[5] = findViewById(R.id.fri);
        checkBoxes[6] = findViewById(R.id.sat);
        final EditText programCreateContent = findViewById(R.id.programCreateContents);
        final Button createButton = findViewById(R.id.createButton);

        if (!create) { // if not writing (= program modify)
            programData = bundle.getParcelable("programData");
            programCreateName.setText(programData.getProgramName());
            createStartTime.setText(programData.getStartTime());
            createEndTime.setText(programData.getEndTime());
            programCreateContent.setText(programData.getProgramContents());
            String frequency = programData.getFrequency();
            for(int i = 0; i < 7; i++) {
                if(frequency.charAt(i) == '1') {
                    checkBoxes[i].setChecked(true);
                }
                else {
                    checkBoxes[i].setChecked(false);
                }
            }
            createButton.setText("Modify");
        }

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID;
                final String programName = programCreateName.getText().toString();
                final String startTime = createStartTime.getText().toString();
                final String endTime = createEndTime.getText().toString();
                final String frequency;
                final String programContents = programCreateContent.getText().toString();

                String forFreq = "";
                for(int i = 0; i < 7; i++) {
                    if(checkBoxes[i].isChecked()) {
                        forFreq = forFreq.concat("1");
                    }
                    else {
                        forFreq = forFreq.concat("0");
                    }
                }
                frequency = forFreq;

                if (create) {
                    userID = userData.getUserID();
                }
                else {
                    userID = "";
                }

                if (programName.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GymProgramCreateActivity.this);
                    dialog = builder.setMessage("제목을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (programContents.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GymProgramCreateActivity.this);
                    dialog = builder.setMessage("내용을 입력하세요.").setNegativeButton("OK", null).create();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(GymProgramCreateActivity.this);
                                if (create)
                                    builder.setMessage("프로그램을 게시하시겠습니까?");
                                else
                                    builder.setMessage("프로그램 정보를 수정하시겠습니까?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        gotoGymProgramActivity(userData);
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
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(GymProgramCreateActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                if (create) {
                    ProgramCreate programCreate = new ProgramCreate(programName, userID, startTime, endTime, frequency, programContents, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(GymProgramCreateActivity.this);
                    queue.add(programCreate);
                }
                else  {
                    String programNum = String.valueOf(programData.getProgramNum());
                    ProgramModify programModify = new ProgramModify(programNum, programName, startTime, endTime, frequency, programContents, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(GymProgramCreateActivity.this);
                    queue.add(programModify);
                }
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
        switch(id){
            case R.id.menu_back:
                gotoGymProgramActivity(userData);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoGymProgramActivity(userData);
    }

    private void gotoGymProgramActivity(UserData userData) {
        Intent intent = new Intent(GymProgramCreateActivity.this, GymProgramActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
        gymProgramActivity.finish();
        finish();
    }
}

class ProgramCreate extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/ProgramCreate.php";
    private Map<String, String> parameters;

    public ProgramCreate(String programName, String userID, String startTime, String endTime, String frequency, String contents, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("programName", programName);
        parameters.put("userID", userID);
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);
        parameters.put("frequency", frequency);
        parameters.put("contents", contents);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class ProgramModify extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/ProgramModify.php";
    private Map<String, String> parameters;

    public ProgramModify(String programNum, String programName, String startTime, String endTime, String frequency, String contents, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("programNum", programNum);
        parameters.put("programName", programName);
        parameters.put("startTime", startTime);
        parameters.put("endTime", endTime);
        parameters.put("frequency", frequency);
        parameters.put("contents", contents);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
