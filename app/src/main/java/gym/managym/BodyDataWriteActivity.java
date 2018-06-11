package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class BodyDataWriteActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private BodyData bodyData;
    private boolean write;
    private AlertDialog dialog;
    private BodyDataActivity bodyDataActivity = (BodyDataActivity) BodyDataActivity.bodyDataActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodydata_write);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userID");
        write = getIntent().getBooleanExtra("write", true);
        final EditText bodydataWriteHeigt = findViewById(R.id.bodydataWriteHeigt);
        final EditText bodydataWriteWeigt = findViewById(R.id.bodydataWriteWeigt);
        final Button postButton = findViewById(R.id.postButton);

        if (!write) { // if not writing (= body modify)
            bodyData = bundle.getParcelable("bodyData");
            bodydataWriteHeigt.setText(String.valueOf(bodyData.getHeight()));
            bodydataWriteWeigt.setText(String.valueOf(bodyData.getWeight()));
            postButton.setText("Modify");
        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID;
                final double height = bodyData.getHeight();
                final String recordDate;
                final double weight = bodyData.getWeight();
                final double bmi = weight/(height*height);

                if (write) {
                    userID = userData.getUserID();
                    recordDate = "";
                    }
                else {
                    userID = "";
                    recordDate = bodyData.getDate();
                    }

                if (bodydataWriteHeigt.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                    dialog = builder.setMessage("키를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (bodydataWriteWeigt.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                    dialog = builder.setMessage("몸무게를 입력하세요.").setNegativeButton("OK", null).create();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                                if (write)
                                    builder.setMessage("신체정보를 추가하시겠습니까?");
                                else
                                    builder.setMessage("신체정보를 수정하시겠습니까?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        gotoBodyDataActivity(userData);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                if (write) {
                    BodyDataPost bodyDataPost = new BodyDataPost(height, weight, recordDate, bmi, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BodyDataWriteActivity.this);
                    queue.add(bodyDataPost);
                }
                else  {
                    BodyDataModify bodyDataModify = new BodyDataModify(height, weight, recordDate, bmi, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(BodyDataWriteActivity.this);
                    queue.add(bodyDataModify);
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
                gotoBodyDataActivity(userData);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoBodyDataActivity(userData);
    }

    private void gotoBodyDataActivity(UserData userData) {
        Intent intent = new Intent(BodyDataWriteActivity.this, BodyDataActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
        bodyDataActivity.finish();
        finish();
    }
}

class BodyDataPost extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/BodyDataPost.php";
    private Map<String, String> parameters;
    public BodyDataPost(double height,  double weight,String recordDate, double bmi, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("height", String.valueOf(height));
        parameters.put("weight", String.valueOf(weight));
        parameters.put("recordDate", recordDate);
        parameters.put("bmi", String.valueOf(bmi));
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class BodyDataModify extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/BodyDataModify.php";
    private Map<String, String> parameters;

    public BodyDataModify(double height,  double weight,String recordDate, double bmi, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("height", String.valueOf(height));
        parameters.put("weight", String.valueOf(weight));
        parameters.put("recordDate", recordDate);
        parameters.put("bmi", String.valueOf(bmi));
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
