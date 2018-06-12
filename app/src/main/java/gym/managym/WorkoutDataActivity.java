package gym.managym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutDataActivity extends AppCompatActivity {
    private ListView dataListView;
    private WorkoutDataListAdapter adapter;
    private ArrayList<WorkoutDataListView> dataList;
    private ArrayList<Entry> entriesTime;
    private Bundle bundle;
    private UserData userData;
    public static Activity workoutDataActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workoutdata);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        workoutDataActivity = WorkoutDataActivity.this;

        final LineChart lineChartTime = findViewById(R.id.chartWorkoutTime);

        dataListView = findViewById(R.id.workoutDataListView);
        dataList = new ArrayList<>();
        entriesTime = new ArrayList<>();

        adapter = new WorkoutDataListAdapter(getApplicationContext(), dataList);
        dataListView.setAdapter(adapter);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    int count = 0;
                    String dataName, startTime, endTime, date;
                    int time, strength;
                    while(count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        dataName = object.getString("dataName");
                        startTime = object.getString("startTime");
                        endTime = object.getString("endTime");
                        date = object.getString("date");
                        time = object.getInt("time");
                        strength = object.getInt("strength");

                        entriesTime.add(new Entry(count+1, time));
                        WorkoutDataListView data = new WorkoutDataListView(dataName, startTime, endTime, strength, date);
                        dataList.add(data);
                        adapter.notifyDataSetChanged();

                        count++;
                    }
                    drawChart(entriesTime, lineChartTime, "Weight");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        WorkoutDataReceive workoutDataReceive = new WorkoutDataReceive(userData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(WorkoutDataActivity.this);
        queue.add(workoutDataReceive);
    }

    private void drawChart(ArrayList<Entry> entriesWeight, LineChart lineChartWeight, String dsc) {
        LineDataSet lineDataSet = new LineDataSet(entriesWeight, dsc);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        lineChartWeight.setData(lineData);

        XAxis xAxis = lineChartWeight.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChartWeight.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChartWeight.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText(dsc);


        lineChartWeight.setDoubleTapToZoomEnabled(false);
        lineChartWeight.setDrawGridBackground(false);
        lineChartWeight.setDescription(description);
        lineChartWeight.animateY(1000, Easing.EasingOption.EaseInCubic);
        lineChartWeight.invalidate();
    }


    public static String getProDay(String date){
        String result = "";
        String start = "2000-01-01".substring(0, 10);
        String end = date.substring(0, 10);
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = formatter.parse(start);
            Date endDate = formatter.parse(end);
            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);
            result = (diffDays+1)+"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
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

class WorkoutDataListView {
    String workoutName;
    String startTime;
    String endTime;
    int time;   //분으로 기록
    int strength;
    String date;

    public WorkoutDataListView(String workoutName, String startTime, String endTime, int strength, String date) {
        this.workoutName = workoutName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.setTime();
        this.strength = strength;
        this.date = date;
    }

    public String getWorkoutName() {
        return this.workoutName;
    }
    public int getTime() {
        return this.time;
    }
    public void setTime() {
        String[] startTimeSet = this.startTime.split(":");
        String[] endTimeSet = this.endTime.split(":");
        this.time = (Integer.parseInt(endTimeSet[0]) - Integer.parseInt(startTimeSet[0])) * 60
                + Integer.parseInt(endTimeSet[1]) - Integer.parseInt(startTimeSet[1]);
    }
    public int getStrength() {
        return this.strength;
    }
    public String getDate() {
        return this.date;
    }
}

class WorkoutDataListAdapter extends BaseAdapter {
    private Context context;
    private List<WorkoutDataListView> dataList;

    public WorkoutDataListAdapter(Context context, List<WorkoutDataListView> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_workoutdata_listview, null);
        TextView nameText = v.findViewById(R.id.workoutDataNameText);
        TextView dateText = v.findViewById(R.id.writeDate);
        TextView timeText = v.findViewById(R.id.workTime);

        nameText.setText(dataList.get(i).getWorkoutName());
        dateText.setText(dataList.get(i).getDate());
        timeText.setText(dataList.get(i).getTime() + "min");

        v.setTag(dataList.get(i).getDate());
        return v;
    }

}

class WorkoutDataReceive extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/WorkoutDataReceive.php"; //need!
    private Map<String, String> parameters;

    public WorkoutDataReceive(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("table", "WORKOUTDATA_"+userID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}