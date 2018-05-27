package gym.managym;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class UserManagementActivity extends AppCompatActivity {
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        final EditText idText = findViewById(R.id.idText);
        final EditText pwText = findViewById(R.id.pwText);
        final EditText pwCheckText = findViewById(R.id.pwCheckText);
        final EditText nameText = findViewById(R.id.nameText);
        final EditText birthText = findViewById(R.id.birthText);
        final EditText phoneText = findViewById(R.id.phoneText);
        final EditText weightText = findViewById(R.id.weightText);
        final EditText heightText = findViewById(R.id.heightText); // 몸무게, 키 입력은 임시
        final Button previousButton = findViewById(R.id.previousButton);
        final Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String userID = idText.getText().toString();
                final String userPW = pwText.getText().toString();
                final String userPWCheck = pwCheckText.getText().toString();
                final String userName = nameText.getText().toString();
                final String userBirth = birthText.getText().toString();
                final String userPhone = phoneText.getText().toString();
                final String userWeight = weightText.getText().toString();
                final String userHeight = heightText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (userPW.equals(userPWCheck)) {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if (success) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
                                    dialog = builder.setMessage("Registered!").setPositiveButton("OK", null).create();
                                    dialog.show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
                                    dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                    dialog.show();
                                }
                            } else { // password check failed
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserManagementActivity.this);
                                dialog = builder.setMessage("Password Check Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPW, userName, userBirth, userPhone, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserManagementActivity.this);
                queue.add(registerRequest);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
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

class RegisterRequest extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserRegister.php";
    private HashMap<String, String> parameters;

    public RegisterRequest(String userID, String userPW, String name, String birth, String phone, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPW", userPW);
        parameters.put("name", name);
        parameters.put("birth", birth);
        parameters.put("phone", phone);
    }

    @Override
    public HashMap<String, String> getParams() {
        return parameters;
    }
}
