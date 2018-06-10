package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserRegisterActivity extends AppCompatActivity {
    private AlertDialog dialog;
    boolean validate = false;
    private ArrayList<String> trainerList = new ArrayList<>();
    private UserManagementActivity userManagementActivity = (UserManagementActivity) UserManagementActivity.userManagementActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        final EditText idText = findViewById(R.id.idText);
        final EditText pwText = findViewById(R.id.pwText);
        final EditText pwCheckText = findViewById(R.id.pwCheckText);
        final EditText nameText = findViewById(R.id.nameText);
        final EditText birthText = findViewById(R.id.birthText);
        final EditText phoneText = findViewById(R.id.phoneText);
        final Spinner trainerSpinner = findViewById(R.id.trainerSpinner);
        //final EditText weightText = findViewById(R.id.weightText);
        //final EditText heightText = findViewById(R.id.heightText);
        final Button validateButton = findViewById(R.id.validateButton);
        final Button registerButton = findViewById(R.id.registerAcceptButton);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainerList);

        Response.Listener<String> responseListener = new Response.Listener<String> () {
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    int count = 0;
                    while(count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        trainerList.add(object.getString("trainer"));
                        adapter.notifyDataSetChanged();
                        count++;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        TrainerSearch trainerSearch = new TrainerSearch(responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserRegisterActivity.this);
        queue.add(trainerSearch);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        trainerSpinner.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        trainerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //testTrainer.setText(trainerList.get(position));
                trainerSpinner.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        validateButton.setOnClickListener(new View.OnClickListener() { // Validate ID
            @Override
            public void onClick(View view) {
                final String userID = idText.getText().toString();
                if (validate) {
                    return;
                }
                if (userID.length() < 5) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                    dialog = builder.setMessage("아이디는 5자 이상이어야 합니다.")
                            .setPositiveButton("OK", null)
                            .create();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.").setPositiveButton("OK", null).create();
                                dialog.show();
                                idText.setEnabled(false);
                                validate = true;
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.").setNegativeButton("OK", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserRegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String userID = idText.getText().toString();
                final String userPW = pwText.getText().toString();
                final String userPWCheck = pwCheckText.getText().toString();
                final String name = nameText.getText().toString();
                final String birth = birthText.getText().toString();
                final String phone = phoneText.getText().toString();
                final String trainer = trainerList.get(trainerSpinner.getSelectedItemPosition());
                //final String userWeight = weightText.getText().toString();
                //final String userHeight = heightText.getText().toString();

                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                    dialog = builder.setMessage("아이디 중복 체크를 해주세요.").setPositiveButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (userPW.length() < 3) { // 임시 (6자 이상으로 변경 예정)
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                    dialog = builder.setMessage("비밀번호는 6자 이상이어야 합니다.").setPositiveButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (!userPW.equals(userPWCheck)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                    dialog = builder.setMessage("비밀번호 확인이 올바르지 않습니다.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (userID.equals("") || userPW.equals("") || userPWCheck.equals("") || name.equals("") || birth.equals("") || phone.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력해주세요.").setNegativeButton("OK", null).create();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                                builder.setMessage("Registered!");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        gotoUserManagementActivity();
                                    }
                                });
                                builder.create();
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserRegisterActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userPW, name, birth, phone, trainer, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserRegisterActivity.this);
                queue.add(registerRequest);
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
                gotoUserManagementActivity();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoUserManagementActivity();
    }

    private void gotoUserManagementActivity() {
        Intent intent = new Intent(UserRegisterActivity.this, UserManagementActivity.class);
        startActivity(intent);
        userManagementActivity.finish();
        finish();
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
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userPW, String name, String birth, String phone, String trainer, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPW", userPW);
        parameters.put("name", name);
        parameters.put("birth", birth);
        parameters.put("phone", phone);
        parameters.put("trainer", trainer);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class ValidateRequest extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserValidate.php";
    private Map<String, String> userInfo;

    public ValidateRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        userInfo = new HashMap<>();
        userInfo.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return userInfo;
    }
}