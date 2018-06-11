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

public class BodyDataActivity extends AppCompatActivity {
    private ListView bodyDataListView;
    private BodyListAdapter adapter2;
    private List<BodyDataListView> bodyDataList;
    private Bundle bundle;
    private UserData userData;
    public static Activity bodyDataActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bodydata);
        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        bodyDataActivity = BodyDataActivity.this;

        final Button writeButton = findViewById(R.id.writeButton);

        bodyDataListView = findViewById(R.id.bodyDataListView);
        bodyDataList = new ArrayList<BodyDataListView>();

        adapter2 = new BodyListAdapter(getApplicationContext(), bodyDataList);
        bodyDataListView.setAdapter(adapter2);

        new BackgroundTask().execute();

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write Notice
                Intent intent = new Intent(BodyDataActivity.this, BodyDataWriteActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("write", true);
                startActivity(intent);
                finish();
            }
        });

        bodyDataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // Read Notice
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Read Notice
                String userID = bodyDataList.get(position).getUserID();
                double height = bodyDataList.get(position).getHeight();
                double weight = bodyDataList.get(position).getWeight();
                String date = bodyDataList.get(position).getDate();
                double bmi = bodyDataList.get(position).getBmi();

                BodyData bodyData = new BodyData(userID, height, weight, date, bmi); // parcelable
                Intent intent = new Intent(BodyDataActivity.this, BodyDataContentActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("BodyData", bodyData);
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
            target = "http://jeffjks.cafe24.com/BodyData.php";
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
                String userID, recordDate;
                double height, weight, bmi;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    userID = object.getString("UserID");
                    height = object.getDouble("Height");
                    weight = object.getDouble("Weight");
                    recordDate = object.getString("Date");
                    bmi = object.getDouble("BMI");
                    BodyDataListView bodyData = new BodyDataListView(userID, height, weight, recordDate,bmi);
                    bodyDataList.add(bodyData);
                    adapter2.notifyDataSetChanged();
                    count++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class BodyDataListView {
    String userID;
    double height;
    double weight;
    String date;
    double bmi;

    public BodyDataListView(String userID, double height, double weight, String date, double bmi){
        this.userID = userID;
        this.height = height;
        this.weight = weight;
        this.date = date;
        this.bmi = weight/(height*height);
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) { this.userID = userID;}

    public double getHeight() { return height; }

    public void setHeight(double height) { this.height = height; }

    public double getWeight() { return weight; }

    public void setWeight(double weight) { this.weight = weight; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBmi() { return bmi; }

    public void setBmi(double bmi) { this.bmi = bmi; }
}

class BodyListAdapter extends BaseAdapter {
    private Context context;
    private List<BodyDataListView>  bodyDataList;

    public BodyListAdapter(Context context, List<BodyDataListView> bodyDataList) {
        this.context = context;
        this. bodyDataList =  bodyDataList;
    }


    @Override
    public int getCount() {
        return bodyDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return bodyDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_bodydata_listview, null);
        TextView userIDText = v.findViewById(R.id.userIDText);
        TextView heightText = v.findViewById(R.id.heightText);
        TextView weightText = v.findViewById(R.id.weightText);
        TextView recordDateText = v.findViewById(R.id.recordDateText);
        TextView bmiText = v.findViewById(R.id.bmiText);

        userIDText.setText(bodyDataList.get(i).getUserID());
        heightText.setText(String.valueOf(bodyDataList.get(i).getHeight()));
        weightText.setText(String.valueOf(bodyDataList.get(i).getWeight()));
        recordDateText.setText(bodyDataList.get(i).getDate());
        bmiText.setText(String.valueOf(bodyDataList.get(i).getBmi()));

        v.setTag(bodyDataList.get(i).getUserID());
        return v;
    }
}