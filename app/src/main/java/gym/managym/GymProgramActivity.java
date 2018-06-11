package gym.managym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GymProgramActivity extends AppCompatActivity {
    private ListView programListView;
    private ListView myProgramListView;
    private GymProgramListAdapter adapter;
    private GymProgramListAdapter myAdapter;
    private ArrayList<GymProgramListView> programList;
    private ArrayList<GymProgramListView> myProgramList;
    private ArrayList<Integer> myProgramNum;
    private Bundle bundle;
    private UserData userData;
    public static Activity gymProgramActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gymprogram);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        gymProgramActivity = GymProgramActivity.this;

        final Button createButton = findViewById(R.id.createButton);
        final TextView myProgramListText = findViewById(R.id.myGymProgramText);

        programListView = findViewById(R.id.programListView);
        programList = new ArrayList<>();
        myProgramListView = findViewById(R.id.myProgramListView);
        myProgramList = new ArrayList<>();

        adapter = new GymProgramListAdapter(getApplicationContext(), programList);
        programListView.setAdapter(adapter);
        myAdapter = new GymProgramListAdapter(getApplicationContext(), myProgramList);
        myProgramListView.setAdapter(myAdapter);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("response");
                    int count = 0;
                    while (count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        myProgramNum.add(object.getInt("programNum"));
                        adapter.notifyDataSetChanged();
                        count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        MyProgramSearch myProgramSearch = new MyProgramSearch(userData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(GymProgramActivity.this);
        queue.add(myProgramSearch);

        new GymProgramActivity.BackgroundTask().execute();

        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write Notice
                Intent intent = new Intent(GymProgramActivity.this, GymProgramCreateActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("create", true);
                startActivity(intent);
                finish();
            }
        });
        if (userData.getAdmin() == 0)
            createButton.setVisibility(View.GONE);
        else {
            myProgramListText.setVisibility(View.GONE);
            myProgramListView.setVisibility(View.GONE);
        }

        programListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // Read Notice
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Read Notice
                int num = programList.get(position).getNum();
                String name = programList.get(position).getName();
                String ID = programList.get(position).getID();
                String startTime = programList.get(position).getStartTime();
                String endTime = programList.get(position).getEndTime();
                String frequency = programList.get(position).getFrequency();
                String contents = programList.get(position).getContents();

                GymProgramData programData = new GymProgramData(num, name, contents,  ID, startTime, endTime, frequency); // parcelable
                Intent intent = new Intent(GymProgramActivity.this, GymProgramContentsActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("programData", programData);
                intent.putExtra("myProgram", false);
                startActivity(intent);
            }
        });

        myProgramListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num = myProgramList.get(i).getNum();
                String name = myProgramList.get(i).getName();
                String ID = myProgramList.get(i).getID();
                String startTime = myProgramList.get(i).getStartTime();
                String endTime = myProgramList.get(i).getEndTime();
                String frequency = myProgramList.get(i).getFrequency();
                String contents = myProgramList.get(i).getContents();

                GymProgramData programData = new GymProgramData(num, name, contents, ID, startTime, endTime, frequency);
                Intent intent = new Intent(GymProgramActivity.this, GymProgramContentsActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("programData", programData);
                intent.putExtra("myProgram", true);
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
            target = "http://jeffjks.cafe24.com/ProgramList.php";
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
                int programNum;
                String programName, trainerID, startTime, endTime, programFrequency, programPeriod, programContents;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    programNum = object.getInt("programNum");
                    programName = object.getString("prgramName");
                    trainerID = object.getString("trainerID");
                    startTime = object.getString("startTime");
                    endTime = object.getString("finishTime");
                    programFrequency = object.getString("frequency");
                    programPeriod = object.getString("startTime") + "~" + object.getString("finishTime") + "(" + changeFrequencyToDay(object.getString("frequency")) + ")";
                    programContents = object.getString("contents");
                    GymProgramListView program = new GymProgramListView(programNum, programName, trainerID, startTime, endTime, programFrequency, programPeriod, programContents);
                    if(myProgramNum.contains(programNum)) {
                        myProgramList.add(program);
                    } else {
                        programList.add(program);
                    }
                    adapter.notifyDataSetChanged();
                    count++;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String changeFrequencyToDay(String frequency) {
            String result = "";
            for(int i = 0; i < 7; i++) {
                if(frequency.charAt(i) == '1') {
                    if(!result.equals("")) {
                        result = result.concat(", ");
                    }
                    switch (i) {
                        case 0:
                            result = result.concat("SUN");
                            break;
                        case 1:
                            result = result.concat("MON");
                            break;
                        case 2:
                            result = result.concat("TUE");
                            break;
                        case 3:
                            result = result.concat("WED");
                            break;
                        case 4:
                            result = result.concat("THR");
                            break;
                        case 5:
                            result = result.concat("FRI");
                            break;
                        case 6:
                            result = result.concat("SAT");
                            break;
                    }
                }
            }
            return result;
        }
    }

    class MyProgramSearch extends StringRequest {
        final static private String URL = "http://jeffjks.cafe24.com/SearchMyProgram.php/";
        private Map<String, String> parameters;

        public MyProgramSearch(String userID, Response.Listener<String> listener) {
            super(Method.POST, URL, listener, null);
            parameters = new HashMap<>();
            parameters.put("userID", userID);
        }
    }
}

class GymProgramListView {
    int num;
    String name;
    String ID;
    String startTime;
    String endTime;
    String frequency;
    String period;
    String contents;

    public GymProgramListView(int num, String name, String ID, String startTime, String endTime, String frequency, String period, String contents) {
        this.num = num;
        this.name = name;
        this.ID = ID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.frequency = frequency;
        this.period = period;
        this.contents = contents;
    }

    public int getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}

class GymProgramListAdapter extends BaseAdapter {
    private Context context;
    private List<GymProgramListView> programList;

    public GymProgramListAdapter(Context context, List<GymProgramListView> programList) {
        this.context = context;
        this.programList = programList;
    }

    @Override
    public int getCount() {
        return programList.size();
    }

    @Override
    public Object getItem(int i) {
        return programList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_gymprogram_listview, null);
        TextView nameText = v.findViewById(R.id.programNameText);
        TextView IDText = v.findViewById(R.id.trainerIDText);
        TextView periodText = v.findViewById(R.id.programPeriodText);

        nameText.setText(programList.get(i).getName());
        IDText.setText(programList.get(i).getID());
        periodText.setText(programList.get(i).getPeriod());

        v.setTag(programList.get(i).getName());
        return v;
    }

}
