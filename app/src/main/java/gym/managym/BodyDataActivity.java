package gym.managym;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BodyDataActivity extends AppCompatActivity {
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
        final LineChart lineChartWeight = findViewById(R.id.chartWeight);
        //final LineChart lineChartHeight = findViewById(R.id.chartHeight);
        final LineChart lineChartBMI = findViewById(R.id.chartBMI);
        final ArrayList<Entry> entriesHeight = new ArrayList<>();
        final ArrayList<Entry> entriesWeight = new ArrayList<>();
        final ArrayList<Entry> entriesBMI = new ArrayList<>();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    int count = 0, height, weight;
                    String date;
                    float BMI;
                    while(count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        height = object.getInt("height");
                        weight = object.getInt("weight");
                        date = object.getString("recordDate");
                        BMI = (float) object.getDouble("BMI");

                        entriesHeight.add(new Entry(count+1, height));
                        entriesWeight.add(new Entry(count+1, weight));
                        entriesBMI.add(new Entry(count+1, BMI));
                        count++;
                    }
                    drawChart(entriesWeight, lineChartWeight, "Weight");
                    //drawChart(entriesHeight, lineChartHeight, "Height");
                    drawChart(entriesBMI, lineChartBMI, "BMI");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        BodyDataReceive bodyDataReceive = new BodyDataReceive(userData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(BodyDataActivity.this);
        queue.add(bodyDataReceive);

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write BodyData
                Intent intent = new Intent(BodyDataActivity.this, BodyDataWriteActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
                finish();
            }
        });
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

class BodyDataReceive extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/BodyDataReceive.php";
    private Map<String, String> parameters;

    public BodyDataReceive(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("table", "BODYDATA_"+userID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}