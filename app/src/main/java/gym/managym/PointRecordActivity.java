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
import android.widget.Button;
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

public class PointRecordActivity extends AppCompatActivity {
    private Bundle bundle;
    private ListView pointListView;
    private UserData userData;
    public static Activity PointRecordActivity;
    private List<PointListView> pointList;
    private PointListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pointrecord);
        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        PointRecordActivity = PointRecordActivity.this;

        final TextView textEmpty = findViewById(R.id.pointListEmpty);

        pointListView = findViewById(R.id.pointListView);
        pointList = new ArrayList<PointListView>();
        adapter = new PointListAdapter(getApplicationContext(), pointList);
        pointListView.setAdapter(adapter);
        textEmpty.setVisibility(View.GONE);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");
                    int count = 0, pointUse, totalPoint, remainPoint;
                    String date;

                    while (count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        totalPoint = object.getInt("totalPoint");
                        remainPoint = object.getInt("remainPoint");
                        pointUse = object.getInt("changedPoint");
                        date = object.getString("recordDate");
                        PointListView point = new PointListView(date, pointUse, totalPoint, remainPoint);
                        adapter.notifyDataSetChanged();
                        count++;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (pointList.size() != 0)
                    textEmpty.setVisibility(View.GONE);
                else
                    textEmpty.setVisibility(View.VISIBLE);
            }
        };
        PointRecordReceive pointRecordReceive = new PointRecordReceive(userData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(PointRecordActivity.this);
        queue.add(pointRecordReceive);

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


class PointListView {
    int usePoint;
    static int totalPoint;
    static int remainPoint;
    String date;

    public PointListView(String date, int usePoint, int totalPoint,int remainPoint) {
        this.date = date;
        this.usePoint = usePoint;
        this.remainPoint = remainPoint;
        this.totalPoint = totalPoint;
    }
    public int getUsePoint() {
        return usePoint;
    }

    public void usePoint(int usePoint) {
        this.usePoint = usePoint;
    }

    public int getTotalPoint() {
        return totalPoint;
    }

    public void totalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public int getRemainPoint() {
        return remainPoint;
    }

    public void remainPoint(int remainPoint) {
        this.remainPoint = remainPoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

class PointListAdapter extends BaseAdapter {
    private Context context;
    private List<PointListView> pointList;

    public PointListAdapter(Context context, List<PointListView> pointList) {
        this.context = context;
        this.pointList = pointList;
    }

    @Override
    public int getCount() {
        return pointList.size();
    }

    @Override
    public Object getItem(int i) {
        return pointList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_point_listview, null);
        TextView usePointText = v.findViewById(R.id.usePointText);
        TextView remainPointText = v.findViewById(R.id.remainPointText);
        TextView totalPointText = v.findViewById(R.id.totalPointText);
        TextView dateText = v.findViewById(R.id.pointDateText);

        usePointText.setText(pointList.get(i).getUsePoint());
        remainPointText.setText(pointList.get(i).getRemainPoint());
        totalPointText.setText(pointList.get(i).getTotalPoint());
        dateText.setText(pointList.get(i).getDate());

        v.setTag(pointList.get(i).getTotalPoint());
        return v;
    }
}

class PointRecordReceive extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/PointRecordReceive.php";
    private Map<String, String> parameters;

    public PointRecordReceive(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("table", "PointRecord_"+userID);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}