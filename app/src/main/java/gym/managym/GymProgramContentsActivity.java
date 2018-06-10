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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GymProgramContentsActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private GymProgramData programData;
    private AlertDialog dialog;
    private GymProgramActivity programActivity = (GymProgramActivity)GymProgramActivity.gymProgramActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gymprogram_content);

        final TextView programName = findViewById(R.id.programNameText);
        final TextView trainerID = findViewById(R.id.trainerNameText);
        final TextView period = findViewById(R.id.programPeriodText);
        final TextView contents = findViewById(R.id.programContentText);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        programData = bundle.getParcelable("programData");

        programName.setText(programData.getProgramName());
        trainerID.setText(programData.getTrainerID());
        period.setText(programData.getStartTime() + "~" + programData.getEndTime() + "(" + programData.changeFrequencyToDay() + ")");
        contents.setText(programData.getProgramContents());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(userData.getAdmin() == 0) {
            getMenuInflater().inflate(R.menu.activity_menu_userattend, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.activity_menu_content, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_action_modify:
                modifyProgram();
                break;
            case R.id.menu_action_delete:
                deleteProgram();
                break;
            case R.id.menu_back:
                finish();
                break;
            case R.id.menu_action_attend:
                break;
            case R.id.menu_action_cancle:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyProgram() {
        Intent intent = new Intent(GymProgramContentsActivity.this, GymProgramCreateActivity.class);
        intent.putExtra("programData", programData);
        intent.putExtra("userData", userData);
        intent.putExtra("create", false);
        startActivity(intent);
        finish();
    }

    private void deleteProgram() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GymProgramContentsActivity.this);
                        builder.setMessage("프로그램을 삭제하시겠습니까?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                refreshProgramIntent();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(GymProgramContentsActivity.this);
                        dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        GymProgramDelete programDelete = new GymProgramDelete(programData.getProgramNumToString(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(GymProgramContentsActivity.this);
        queue.add(programDelete);
    }

    private void refreshProgramIntent() {
        programActivity.finish();
        Intent intent = new Intent(GymProgramContentsActivity.this, GymProgramActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
        finish();
    }
}

class GymProgramData implements Parcelable {
    private int programNum;
    private String programName;
    private String programContents;
    private String trainerID;
    private String startTime;
    private String endTime;
    private String frequency;

    public GymProgramData() { }

    public GymProgramData(Parcel in) {
        readFromParcel(in);
    }

    public GymProgramData(int programNum, String programName, String programContents, String trainerID, String startTime, String endTime, String frequency) {
        this.programNum = programNum;
        this.programName = programName;
        this.programContents = programContents;
        this.trainerID = trainerID;
        this.startTime = startTime;
        this.endTime = endTime;
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
    public int getProgramNum() { return this.programNum; }
    public String getProgramNumToString() { return new String("" + this.programNum); }
    public String getProgramName() {
        return this.programName;
    }
    public String getTrainerID() { return this.trainerID; }
    public String getProgramContents() {
        return this.programContents;
    }
    public String getStartTime() {
        return this.startTime;
    }
    public String getEndTime() { return this.endTime; }
    public String getFrequency() {
        return this.frequency;
    }

    public String changeFrequencyToDay() {
        String result = "";
        for(int i = 0; i < 7; i++) {
            if(frequency.charAt(i) == '1') {
                if(!result.equals("")) {
                    result = result.concat(", ");
                }
                switch (i) {
                    case 0:
                        result = result.concat("SUN");
                        break;
                    case 1:
                        result = result.concat("MON");
                        break;
                    case 2:
                        result = result.concat("TUE");
                        break;
                    case 3:
                        result = result.concat("WED");
                        break;
                    case 4:
                        result = result.concat("THR");
                        break;
                    case 5:
                        result = result.concat("FRI");
                        break;
                    case 6:
                        result = result.concat("SAT");
                        break;
                }
            }
        }
        return result;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.programNum);
        dest.writeString(this.programName);
        dest.writeString(this.trainerID);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeString(this.frequency);
        dest.writeString(this.programContents);
    }

    private void readFromParcel(Parcel in) {
        this.programNum = in.readInt();
        this.programName = in.readString();
        this.trainerID = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.frequency = in.readString();
        this.programContents = in.readString();
    }
}

class GymProgramDelete extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/GymProgramDelete.php";
    private Map<String, String> parameters;

    public GymProgramDelete(String programNum, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("programNum", programNum);
    }

    @Override
    public Map<String, String> getParams() { return parameters;}
}