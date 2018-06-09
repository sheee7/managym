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

public class NoticeActivity extends AppCompatActivity {
    private ListView noticeListView;
    private NoticeListAdapter adapter;
    private List<NoticeListView> noticeList;
    private Bundle bundle;
    private UserData userData;
    public static Activity noticeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        noticeActivity = NoticeActivity.this;

        final Button writeButton = findViewById(R.id.writeButton);

        noticeListView = findViewById(R.id.noticeListView);
        noticeList = new ArrayList<NoticeListView>();

        adapter = new NoticeListAdapter(getApplicationContext(), noticeList);
        noticeListView.setAdapter(adapter);

        new BackgroundTask().execute();

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write Notice
                Intent intent = new Intent(NoticeActivity.this, NoticeWriteActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("write", true);
                startActivity(intent);
                finish();
            }
        });
        if (userData.getAdmin() == 0)
            writeButton.setVisibility(View.GONE);

        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // Read Notice
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Read Notice
                String title = noticeList.get(position).getTitle();
                String name = noticeList.get(position).getName();
                String date = noticeList.get(position).getDate();
                String content = noticeList.get(position).getContent();

                NoticeData noticeData = new NoticeData(title, name, date, content); // parcelable
                Intent intent = new Intent(NoticeActivity.this, NoticeContentActivity.class);
                intent.putExtra("userData", userData);
                intent.putExtra("noticeData", noticeData);
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
            target = "http://jeffjks.cafe24.com/NoticeList.php";
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
                String noticeTitle, noticeName, noticeDate, noticeContent;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    noticeTitle = object.getString("noticeTitle");
                    noticeName = object.getString("noticeName");
                    noticeDate = object.getString("noticeDate");
                    noticeContent = object.getString("noticeContent");
                    NoticeListView notice = new NoticeListView(noticeTitle, noticeName, noticeDate, noticeContent);
                    noticeList.add(notice);
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

class NoticeListView {
    String title;
    String name;
    String date;
    String content;

    public NoticeListView(String title, String name, String date, String content) {
        this.title = title;
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class NoticeListAdapter extends BaseAdapter {
    private Context context;
    private List<NoticeListView> noticeList;

    public NoticeListAdapter(Context context, List<NoticeListView> noticeList) {
        this.context = context;
        this.noticeList = noticeList;
    }

    @Override
    public int getCount() {
        return noticeList.size();
    }

    @Override
    public Object getItem(int i) {
        return noticeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_notice_listview, null);
        TextView titleText = v.findViewById(R.id.noticeTitleText);
        TextView nameText = v.findViewById(R.id.noticeNameText);
        TextView dateText = v.findViewById(R.id.noticeDateText);

        titleText.setText(noticeList.get(i).getTitle());
        nameText.setText(noticeList.get(i).getName());
        dateText.setText(noticeList.get(i).getDate());

        v.setTag(noticeList.get(i).getTitle());
        return v;
    }
}