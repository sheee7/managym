package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserModifyActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserListData userListData;
    private ArrayList<String> trainerList = new ArrayList<String>();
    private AlertDialog dialog;
    private UserManagementActivity userManagementActivity = (UserManagementActivity) UserManagementActivity.userManagementActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_modify);

        //new UserModifyActivity.BackgroundTask().execute();

        final boolean adminState;
        bundle = getIntent().getExtras();
        userListData = bundle.getParcelable("userListData");
        final TextView userIDText = findViewById(R.id.modifyUserID);
        final EditText nameText = findViewById(R.id.modifyName);
        final EditText birthText = findViewById(R.id.modifyBirth);
        final EditText phoneText = findViewById(R.id.modifyPhone);
        final EditText weightText = findViewById(R.id.modifyWeight);
        final EditText heightText = findViewById(R.id.modifyHeight);
        final Spinner trainerSpinner = findViewById(R.id.trainerSpinner);
        final CheckBox adminCheck = findViewById(R.id.modifyAdmin);
        final Button modifyAcceptButton = findViewById(R.id.modifyAcceptButton);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainerList);
        final RelativeLayout modifyTrainer = findViewById(R.id.modifyTrainer);

        if (userListData.getAdmin() != 0) {
            adminCheck.setChecked(true);
            modifyTrainer.setVisibility(View.GONE);
        } else {
            adminCheck.setChecked(false);
            modifyTrainer.setVisibility(View.VISIBLE);
        }

        userIDText.setText(userListData.getUserID());
        nameText.setText(userListData.getName());
        birthText.setText(userListData.getBirth());
        phoneText.setText(userListData.getPhone());
        weightText.setText(String.valueOf(userListData.getWeight()));
        heightText.setText(String.valueOf(userListData.getHeight()));

        Response.Listener<String> responseListener = new Response.Listener<String> () {
            public void onResponse(String response) {
                try {
                    Log.d("TAG", "Response");
                    JSONObject jsonObject = new JSONObject(response);
                    //trainerList.add(jsonResponse.getString("trainer"));

                    //JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    int count = 0;
                    //String trainer;
                    while(count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        //trainer = object.getString("trainer");
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
        RequestQueue queue = Volley.newRequestQueue(UserModifyActivity.this);
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

        adminCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    modifyTrainer.setVisibility(View.GONE);
                }
                else {
                    modifyTrainer.setVisibility(View.VISIBLE);
                }
            }
        });

        modifyAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userListData.getUserID();
                final String name = nameText.getText().toString();
                final String birth = birthText.getText().toString();
                final String phone = phoneText.getText().toString();
                final String weight = weightText.getText().toString();
                final String height = heightText.getText().toString();
                final String trainer;
                final String admin;
                if (adminCheck.isChecked()) {
                    admin = "1";
                    trainer = "";
                }
                else {
                    admin = "0";
                    trainer = trainerList.get(trainerSpinner.getSelectedItemPosition());
                }

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
                if (trainer.equals("") && admin.equals("0")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserModifyActivity.this);
                    dialog = builder.setMessage("담당 트레이너를 선택하세요.").setNegativeButton("OK", null).create();
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
                UserModify userModify = new UserModify(userID, name, birth, phone, weight, height, trainer, admin, responseListener);
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

    public UserModify(String userID, String name, String birth, String phone, String weight, String height, String trainer, String admin, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("name", name);
        parameters.put("birth", birth);
        parameters.put("phone", phone);
        parameters.put("weight", weight);
        parameters.put("height", height);
        parameters.put("trainer", trainer);
        parameters.put("admin", admin);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class TrainerSearch extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/TrainerSearch.php";

    public TrainerSearch(Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null); // 해당 정보를 POST 방식으로 URL에 전송
    }
}