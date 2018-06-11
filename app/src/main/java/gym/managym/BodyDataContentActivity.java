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

public class BodyDataContentActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private BodyData bodyData;
    private AlertDialog dialog;
    private BodyDataActivity bodyDataActivity = (BodyDataActivity) BodyDataActivity.bodyDataActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);

        final TextView userIDText = findViewById(R.id.userIDText);
        final TextView heightText = findViewById(R.id.heightText);
        final TextView weightText = findViewById(R.id.weightText);
        final TextView recordDateText = findViewById(R.id.recordDateText);
        final TextView bmiText = findViewById(R.id.bmiText);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        bodyData = bundle.getParcelable("noticeData");
        userIDText.setText(bodyData.getUserID());
        heightText.setText(String.valueOf(bodyData.getHeight()));
        weightText.setText(String.valueOf(bodyData.getWeight()));
        recordDateText.setText(bodyData.getDate());
        bmiText.setText(String.valueOf(bodyData.getBmi()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userData.getAdmin() == 0)
            getMenuInflater().inflate(R.menu.activity_menu_default, menu);
        else
            getMenuInflater().inflate(R.menu.activity_menu_content, menu); // Admin일 경우 수정 가능
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_action_modify:
                modifyBodyData();
                break;
            case R.id.menu_action_delete:
                deleteBodyData();
                break;
            case R.id.menu_back:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void modifyBodyData() { // Modify Notice
        Intent intent = new Intent(BodyDataContentActivity.this, BodyDataWriteActivity.class);
        intent.putExtra("noticeData", bodyData);
        intent.putExtra("userData", userData);
        intent.putExtra("write", false);
        startActivity(intent);
        finish();
    }

    private void deleteBodyData() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataContentActivity.this);
                        builder.setMessage("신체정보를 삭제하시겠습니까?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                refreshBodyDataIntent();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataContentActivity.this);
                        dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        BodyDataDelete bodyDataDelete = new BodyDataDelete(bodyData.getDate(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(BodyDataContentActivity.this);
        queue.add(bodyDataDelete);
    }

    private void refreshBodyDataIntent() {
        bodyDataActivity.finish();
        Intent intent = new Intent(BodyDataContentActivity.this, BodyDataActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
        finish();
    }
}

class BodyDataDelete extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/BodyDataDelete.php";
    private Map<String, String> parameters;

    public BodyDataDelete(String recordDate, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("recordDate", recordDate);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class BodyData implements Parcelable {
    private String userID;
    private double height;
    private double weight;
    private String date;
    private double bmi;

    public BodyData() { }

    public BodyData(Parcel in) {
        readFromParcel(in);
    }

    public BodyData(String userID, double height, double weight, String date, double bmi) {
        this.userID = userID;
        this.height = height;
        this.weight = weight;
        this.date = date;
        this.bmi = bmi;
    }

    public static final Creator<BodyData> CREATOR = new Creator<BodyData>() {
        public BodyData createFromParcel(Parcel in) {
            return new BodyData(in);
        }

        public BodyData[] newArray (int size) {
            return new BodyData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public String getUserID() {
        return userID;
    }
    public double getHeight() {
        return height;
    }
    public double getWeight() {
        return weight;
    }
    public String getDate() {
        return date;
    }
    public double getBmi() {
        return bmi;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeDouble(this.height);
        dest.writeDouble(this.weight);
        dest.writeString(this.date);
        dest.writeDouble(this.bmi);
    }
    private void readFromParcel(Parcel in) {
        userID = in.readString();
        height = in.readDouble();
        weight = in.readDouble();
        date = in.readString();
        bmi = in.readDouble();
    }
}