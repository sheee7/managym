package gym.managym;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText idText = findViewById(R.id.idText);
        final EditText pwText = findViewById(R.id.pwText);
        final Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String textUserID = idText.getText().toString(); // id칸에 적힌 스트링 불러오기
                String textUserPW = pwText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String> () {
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                                String userID = jsonResponse.getString("userID");
                                String userPW = jsonResponse.getString("userPW");
                                String name = jsonResponse.getString("name");
                                String birth = jsonResponse.getString("birth");
                                String phone = jsonResponse.getString("phone");
                                int weight = jsonResponse.getInt("weight");
                                int height = jsonResponse.getInt("height");
                                int point = jsonResponse.getInt("point");
                                int admin = jsonResponse.getInt("admin");


                                dialog = builder.setMessage("Success!").setPositiveButton("OK", null).create();
                                dialog.show();

                                UserData userData = new UserData(userID, userPW, name, birth, phone, weight, height, point, admin); // parcelable
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userData", userData);
                                startActivity(intent);
                                finish();
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

        TextView registerButton = (TextView) findViewById(R.id.registerButton); // User Register (나중에 옮길 예정)
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                //LoginActivity.this.startActivity(registerIntent);
                String userID = idText.getText().toString();
                String userPW = pwText.getText().toString();
                String name = idText.getText().toString();
                String birth = idText.getText().toString();
                String phone = idText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("Success!").setPositiveButton("OK", null).create();
                                dialog.show();
                            } else {
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
                RegisterRequest registerRequest = new RegisterRequest(userID, userPW, name, birth, phone, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(registerRequest);
            }
        });
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
