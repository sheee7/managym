package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class  PointWriteActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_write);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        final EditText pointWriteUser = findViewById(R.id.pointWriteUser);
        final EditText pointWriteNum = findViewById(R.id.pointWriteNum);
        final Button plusButton = findViewById(R.id.plusButton);
        final Button minusButton = findViewById(R.id.minusButton);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userData.getUserID();
                final String point = pointWriteNum.getText().toString();

                if (pointWriteNum.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                    dialog = builder.setMessage("포인트를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                } else if (pointWriteUser.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                    dialog = builder.setMessage("사용자를 입력하세요.").setNegativeButton("OK", null).create();
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                                    builder.setMessage("포인트를 추가하시겠습니까?");
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

                                    String date = jsonResponse.getString("recordDate");
                                    Log.d("TAG", date);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                                    dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                    dialog.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    PointAdd pointAdd = new PointAdd(userID, point, responseListenerB);
                    RequestQueue queue = Volley.newRequestQueue(PointWriteActivity.this);
                    queue.add(pointAdd);
                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userData.getUserID();
                final String point = pointWriteNum.getText().toString();

                if (pointWriteNum.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                    dialog = builder.setMessage("포인트를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                } else if (pointWriteUser.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                    dialog = builder.setMessage("사용자를 입력하세요.").setNegativeButton("OK", null).create();
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
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                                    builder.setMessage("포인트를 차감하시겠습니까?");
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

                                    String date = jsonResponse.getString("recordDate");
                                    Log.d("TAG", date);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PointWriteActivity.this);
                                    dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                    dialog.show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    PointMinus pointMinus = new PointMinus(userID, point, responseListenerB);
                    RequestQueue queue = Volley.newRequestQueue(PointWriteActivity.this);
                    queue.add(pointMinus);
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

    class PointMinus extends StringRequest {
        final static private String URL = "http://jeffjks.cafe24.com/Pointchange.php";
        private Map<String, String> parameters;

        public PointMinus(String userID, String point, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            parameters = new HashMap<>();
            parameters.put("table", "PointRecord_" + userID);
            parameters.put("point", point);
            parameters.put("totalPoint", String.valueOf(PointListView.totalPoint - Integer.parseInt(point)));
            parameters.put("remainPoint", String.valueOf(PointListView.remainPoint - Integer.parseInt(point)));
            Log.d("TAG", "PointRecord_" + userID);
            Log.d("TAG", point);
            Log.d("TAG", String.valueOf(PointListView.totalPoint - Integer.parseInt(point)));
            Log.d("TAG", String.valueOf(PointListView.remainPoint - Integer.parseInt(point)));
        }

        @Override
        public Map<String, String> getParams() {
            return parameters;
        }
    }
}

    class PointAdd extends StringRequest {
        final static private String URL = "http://jeffjks.cafe24.com/Pointchange.php";
        private Map<String, String> parameters;

        public PointAdd(String userID, String point, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            parameters = new HashMap<>();
            parameters.put("table", "PointRecord_" + userID);
            parameters.put("point", point);
            parameters.put("totalPoint", String.valueOf(PointListView.totalPoint));
            parameters.put("remainPoint", String.valueOf(PointListView.remainPoint + Integer.parseInt(point)));
            Log.d("TAG", "PointRecord_" + userID);
            Log.d("TAG", point);
            Log.d("TAG", String.valueOf(PointListView.totalPoint));
            Log.d("TAG", String.valueOf(PointListView.remainPoint + Integer.parseInt(point)));
        }


        @Override
        public Map<String, String> getParams() {
            return parameters;
        }
    }

