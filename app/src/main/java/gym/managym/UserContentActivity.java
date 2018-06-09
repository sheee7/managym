package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class UserContentActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserListData userListData;
    private AlertDialog dialog;
    private UserManagementActivity userManagementActivity = (UserManagementActivity) UserManagementActivity.userManagementActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_content);

        final TextView userIDText = findViewById(R.id.userIDText);
        final TextView nameText = findViewById(R.id.nameText);
        final TextView birthText = findViewById(R.id.birthText);
        final TextView phoneText = findViewById(R.id.phoneText);
        final TextView weightText = findViewById(R.id.weightText);
        final TextView heightText = findViewById(R.id.heightText);
        final TextView pointText = findViewById(R.id.pointText);
        final TextView trainerText = findViewById(R.id.trainerText);
        final TextView adminText = findViewById(R.id.adminText);

        bundle = getIntent().getExtras();
        userListData = bundle.getParcelable("userListData");

        userIDText.setText("ID : "+userListData.getUserID());
        nameText.setText("Name : "+userListData.getName());
        birthText.setText("Birth : "+userListData.getBirth());
        phoneText.setText("Phone : "+userListData.getPhone());
        weightText.setText("Weight : "+String.valueOf(userListData.getWeight()));
        heightText.setText("Height : "+String.valueOf(userListData.getHeight()));
        pointText.setText("Point : "+String.valueOf(userListData.getPoint()));
        trainerText.setText("Trainer : "+userListData.getTrainer());
        adminText.setText("Admin : "+String.valueOf(userListData.getAdmin()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu_content, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_action_modify:
                modifyUser();
                break;
            case R.id.menu_action_delete:
                deleteUser();
                break;
            case R.id.menu_back:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyUser() { // Modify User
        Intent intent = new Intent(UserContentActivity.this, UserModifyActivity.class);
        intent.putExtra("userListData", userListData);
        startActivity(intent);
        finish();
    }

    private void deleteUser() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserContentActivity.this);
                        builder.setMessage("해당 사용자 정보를 삭제하시겠습니까?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                refreshUserManagementIntent();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserContentActivity.this);
                        dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        UserDelete userDelete = new UserDelete(userListData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserContentActivity.this);
        queue.add(userDelete);
    }

    private void refreshUserManagementIntent() {
        userManagementActivity.finish();
        Intent intent = new Intent(UserContentActivity.this, UserManagementActivity.class);
        intent.putExtra("userListData", userListData);
        startActivity(intent);
        finish();
    }
}

class UserDelete extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserDelete.php";
    private Map<String, String> parameters;

    public UserDelete(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class UserListData implements Parcelable {
    private String userID;
    private String name;
    private String birth;
    private String phone;
    private int weight;
    private int height;
    private int point;
    private String trainer;
    private int admin;

    public UserListData() { }

    public UserListData(Parcel in) {
        readFromParcel(in);
    }

    public UserListData(String userID, String name, String birth, String phone, int weight, int height, int point, String trainer, int admin) {
        this.userID = userID;
        this.name = name;
        this.birth = birth;
        this.phone = phone;
        this.weight = weight;
        this.height = height;
        this.point = point;
        this.trainer = trainer;
        this.admin = admin;
    }

    public static final Parcelable.Creator<UserListData> CREATOR = new Parcelable.Creator<UserListData>() {
        public UserListData createFromParcel(Parcel in) {
            return new UserListData(in);
        }

        public UserListData[] newArray (int size) {
            return new UserListData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public String getUserID() {
        return userID;
    }
    public String getName() {
        return name;
    }
    public String getBirth() {
        return birth;
    }
    public String getPhone() {
        return phone;
    }
    public int getWeight() {
        return weight;
    }
    public int getHeight() {
        return height;
    }
    public int getPoint() {
        return point;
    }
    public String getTrainer() {
        return trainer;
    }
    public int getAdmin() {
        return admin;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userID);
        dest.writeString(this.name);
        dest.writeString(this.birth);
        dest.writeString(this.phone);
        dest.writeInt(this.weight);
        dest.writeInt(this.height);
        dest.writeInt(this.point);
        dest.writeString(this.trainer);
        dest.writeInt(this.admin);
    }

    private void readFromParcel(Parcel in) {
        userID = in.readString();
        name = in.readString();
        birth = in.readString();
        phone = in.readString();
        weight = in.readInt();
        height = in.readInt();
        point = in.readInt();
        trainer = in.readString();
        admin = in.readInt();
    }
}