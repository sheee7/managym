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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserModifyActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserListData userListData;
    private AlertDialog dialog;
    private UserManagementActivity userManagementActivity = (UserManagementActivity) UserManagementActivity.userManagementActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modify);

        final boolean adminState;
        bundle = getIntent().getExtras();
        userListData = bundle.getParcelable("userListData");
        final TextView userIDText = findViewById(R.id.modifyUserID);
        final EditText nameText = findViewById(R.id.modifyName);
        final EditText birthText = findViewById(R.id.modifyBirth);
        final EditText phoneText = findViewById(R.id.modifyPhone);
        final EditText weightText = findViewById(R.id.modifyWeight);
        final EditText heightText = findViewById(R.id.modifyHeight);
        final CheckBox adminCheck = findViewById(R.id.modifyAdmin);
        final Button modifyAcceptButton = findViewById(R.id.modifyAcceptButton);

        if (userListData.getAdmin() != 0) {
            adminState = true;
        }
        else {
            adminState = false;
        }

        userIDText.setText(userListData.getUserID());
        nameText.setText(userListData.getName());
        birthText.setText(userListData.getBirth());
        phoneText.setText(userListData.getPhone());
        weightText.setText(String.valueOf(userListData.getWeight()));
        heightText.setText(String.valueOf(userListData.getHeight()));
        adminCheck.setChecked(adminState);

        modifyAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userListData.getUserID();
                final String name = nameText.getText().toString();
                final String birth = birthText.getText().toString();
                final String phone = phoneText.getText().toString();
                final String weight = weightText.getText().toString();
                final String height = heightText.getText().toString();
                final String admin;
                if (adminCheck.isChecked())
                    admin = "1";
                else
                    admin = "0";

                if (name.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserModifyActivity.this);
                    dialog = builder.setMessage("이름을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (birth.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserModifyActivity.this);
                    dialog = builder.setMessage("생년월일을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (phone.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserModifyActivity.this);
                    dialog = builder.setMessage("전화번호를 입력하세요.").setNegativeButton("OK", null).create();
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserModifyActivity.this);
                                builder.setMessage("변경사항을 저장하시겠습니까?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        gotoUserManagementActivity(userListData);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserModifyActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                UserModify userModify = new UserModify(userID, name, birth, phone, weight, height, admin, responseListener);
                RequestQueue queue = Volley.newRequestQueue(UserModifyActivity.this);
                queue.add(userModify);
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
                gotoUserManagementActivity(userListData);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoUserManagementActivity(userListData);
    }

    private void gotoUserManagementActivity(UserListData userListData) {
        Intent intent = new Intent(UserModifyActivity.this, UserManagementActivity.class);
        intent.putExtra("userListData", userListData);
        startActivity(intent);
        userManagementActivity.finish();
        finish();
    }
}

class UserModify extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserModify.php";
    private Map<String, String> parameters;

    public UserModify(String userID, String name, String birth, String phone, String weight, String height, String admin, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("name", name);
        parameters.put("birth", birth);
        parameters.put("phone", phone);
        parameters.put("weight", weight);
        parameters.put("height", height);
        parameters.put("admin", admin);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
