package gym.managym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        final EditText idText = findViewById(R.id.idText);
        final EditText pwText = findViewById(R.id.pwText);
        final Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                final String textUserID = idText.getText().toString(); // id칸에 적힌 스트링 불러오기
                final String textUserPW = pwText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String> () {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                String userID = jsonResponse.getString("userID");
                                String userPW = jsonResponse.getString("userPW");
                                String name = jsonResponse.getString("name");
                                String birth = jsonResponse.getString("birth");
                                String phone = jsonResponse.getString("phone");
                                int weight = jsonResponse.getInt("weight");
                                int height = jsonResponse.getInt("height");
                                int point = jsonResponse.getInt("point");
                                String trainer = jsonResponse.getString("trainer");
                                int admin = jsonResponse.getInt("admin");

                                if(admin == 0) {
                                    TraineeData userData = new TraineeData(userID, userPW, name, birth, phone, weight, height, point, trainer, admin); // parcelable
                                    Intent intent = new Intent(LoginActivity.this, TraineeMainActivity.class);
                                    intent.putExtra("userData", userData);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    UserData userData = new UserData(userID, userPW, name, birth, phone, weight, height, point, admin);
                                    Intent intent = new Intent(LoginActivity.this, TrainerMainActivity.class);
                                    intent.putExtra("userData", userData);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(textUserID, textUserPW, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

@Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

class LoginRequest extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserLogin.php";
    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPW, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null); // 해당 정보를 POST 방식으로 URL에 전송
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPW", userPW);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}