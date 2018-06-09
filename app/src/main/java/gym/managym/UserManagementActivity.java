package gym.managym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    private ListView userListView;
    private UserListAdapter adapter;
    private List<UserListView> userList;
    public static Activity userManagementActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        userManagementActivity = UserManagementActivity.this;

        final Button registerButton = findViewById(R.id.registerButton);

        userListView = findViewById(R.id.userListView);
        userList = new ArrayList<UserListView>();

        adapter = new UserListAdapter(getApplicationContext(), userList);
        userListView.setAdapter(adapter);

        new UserManagementActivity.BackgroundTask().execute();

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Register User
                Intent intent = new Intent(UserManagementActivity.this, UserRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // Read UserList
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userID = userList.get(position).getUserID();
                String name = userList.get(position).getName();
                String birth = userList.get(position).getBirth();
                String phone = userList.get(position).getPhone();
                int weight = userList.get(position).getWeight();
                int height = userList.get(position).getHeight();
                int point = userList.get(position).getPoint();
                String trainer = userList.get(position).getTrainer();
                int admin = userList.get(position).getAdmin();

                UserListData userListData = new UserListData(userID, name, birth, phone, weight, height, point, trainer, admin);
                Intent intent = new Intent(UserManagementActivity.this, UserContentActivity.class);
                intent.putExtra("userListData", userListData);
                startActivity(intent);
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
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> { // Load ListView
        String target; // address to access

        @Override
        protected void onPreExecute() {
            target = "http://jeffjks.cafe24.com/UserList.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                String userID, name, birth, phone ,trainer;
                int weight, height, point, admin;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    userID = object.getString("userID");
                    name = object.getString("name");
                    birth = object.getString("birth");
                    phone = object.getString("phone");
                    weight = object.getInt("weight");
                    height = object.getInt("height");
                    point = object.getInt("point");
                    trainer = object.getString("trainer");
                    admin = object.getInt("admin");
                    UserListView user = new UserListView(userID, name, birth, phone, weight, height, point, trainer, admin);
                    userList.add(user);
                    adapter.notifyDataSetChanged();
                    count++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class UserListView {
    String userID;
    String name;
    String birth;
    String phone;
    String trainer;
    int weight;
    int height;
    int point;
    int admin;

    public UserListView(String userID, String name, String birth, String phone, int weight, int height, int point, String trainer, int admin) {
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
}

class UserListAdapter extends BaseAdapter {
    private Context context;
    private List<UserListView> userList;

    public UserListAdapter(Context context, List<UserListView> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_user_listview, null);
        TextView IDText = v.findViewById(R.id.userID);
        TextView nameText = v.findViewById(R.id.userName);
        TextView birthText = v.findViewById(R.id.userBirth);
        TextView phoneText = v.findViewById(R.id.userPhone);

        IDText.setText(userList.get(i).getUserID());
        nameText.setText(userList.get(i).getName());
        birthText.setText(userList.get(i).getBirth());
        phoneText.setText(userList.get(i).getPhone());

        v.setTag(userList.get(i).getUserID());
        return v;
    }
}