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
    private boolean write;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodydata_write);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        write = getIntent().getBooleanExtra("write", true);
        final EditText bodyDataWriteHeight = findViewById(R.id.bodyDataWriteHeight);
        final EditText bodyDataWriteWeight = findViewById(R.id.bodyDataWriteWeight);
        final Button postButton = findViewById(R.id.postButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userData.getUserID();
                final int height = bodyDataWriteHeight.getHeight();
                final int weight = bodyDataWriteWeight.getHeight();
                final double bmi = weight / (height * height);

                if (bodyDataWriteHeight.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                    dialog = builder.setMessage("키를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                } else if (bodyDataWriteWeight.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                    dialog = builder.setMessage("몸무게를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                } else {
                    Response.Listener<String> responseListenerB = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(BodyDataWriteActivity.this);
                                    builder.setMessage("신체정보를 추가하시겠습니까?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
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
                        BodyDataPost bodyDataPost = new BodyDataPost(height, weight, "BodyData_" + userID, bmi, responseListenerB);
                        RequestQueue queue = Volley.newRequestQueue(BodyDataWriteActivity.this);
                        queue.add(bodyDataPost);
                    }
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
        switch (id) {
            case R.id.menu_back:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

    class BodyDataPost extends StringRequest {
        final static private String URL = "http://jeffjks.cafe24.com/BodyDataPost.php";
        private Map<String, String> parameters;

        public BodyDataPost(int height, int weight, String userID, double bmi, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            parameters = new HashMap<>();
            parameters.put("userID", userID);
            parameters.put("height", String.valueOf(height));
            parameters.put("weight", String.valueOf(weight));
            parameters.put("bmi", String.valueOf(bmi));
        }

        @Override
        public Map<String, String> getParams() {
            return parameters;
        }
    }
