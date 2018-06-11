package gym.managym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyDataActivity extends AppCompatActivity {
    private ListView bodyDataListView;
    private BodyListAdapter adapter;
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

        final TextView textEmpty = findViewById(R.id.listEmpty);
        final Button writeButton = findViewById(R.id.writeButton);

        bodyDataListView = findViewById(R.id.bodyDataListView);
        bodyDataList = new ArrayList<BodyDataListView>();

        adapter = new BodyListAdapter(getApplicationContext(), bodyDataList);
        bodyDataListView.setAdapter(adapter);
        textEmpty.setVisibility(View.GONE);


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //boolean success = jsonResponse.getBoolean("success");

                    //JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    int count = 0;
                    String recordDate;
                    int height, weight;
                    double bmi;
                    while(count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        recordDate = object.getString("recordDate");
                        height = object.getInt("Height");
                        weight = object.getInt("Weight");
                        bmi = object.getDouble("BMI");
                        BodyDataListView bodyData = new BodyDataListView(height, weight, recordDate,bmi);
                        bodyDataList.add(bodyData);
                        adapter.notifyDataSetChanged();
                        count++;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (bodyDataList.size() != 0)
                    textEmpty.setVisibility(View.GONE);
                else
                    textEmpty.setVisibility(View.VISIBLE);
            }
        };
        BodyDataList bodyDataList = new BodyDataList("BodyData_"+userData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(BodyDataActivity.this);
        queue.add(bodyDataList);

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write Data
                Intent intent = new Intent(BodyDataActivity.this, BodyDataWriteActivity.class);
                intent.putExtra("userData", userData);
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
}

class BodyDataListView {
    int height;
    int weight;
    String recordDate;
    double bmi;

    public BodyDataListView(int height, int weight, String recordDate, double bmi){
        this.height = height;
        this.weight = weight;
        this.recordDate = recordDate;
        this.bmi = weight/(height*height);
    }

    public int getHeight() { return height; }

    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }

    public void setWeight(int weight) { this.weight = weight; }

    public String getDate() {
        return recordDate;
    }

    public void setDate(String date) {
        this.recordDate = date;
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
        TextView recordDateText = v.findViewById(R.id.recordDateText);
        TextView heightText = v.findViewById(R.id.heightText);
        TextView weightText = v.findViewById(R.id.weightText);
        TextView bmiText = v.findViewById(R.id.bmiText);

        heightText.setText(String.valueOf(bodyDataList.get(i).getHeight()));
        weightText.setText(String.valueOf(bodyDataList.get(i).getWeight()));
        recordDateText.setText(bodyDataList.get(i).getDate());
        bmiText.setText(String.valueOf(bodyDataList.get(i).getBmi()));

        v.setTag(bodyDataList.get(i).getDate());
        return v;
    }
}

class BodyDataList extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/BodyDataList.php";
    private Map<String, String> parameters;

    public BodyDataList(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
