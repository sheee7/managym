package gym.managym;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class NoticeActivity extends AppCompatActivity {
    private ListView noticeListView;
    private NoticeListAdapter adapter;
    private List<NoticeListView> noticeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        final Bundle bundle = getIntent().getExtras();
        final UserData userData = bundle.getParcelable("userData");

        final Button writeButton = findViewById(R.id.writeButton);
        final Button previousButton = findViewById(R.id.previousButton);

        noticeListView = findViewById(R.id.noticeListView);
        noticeList = new ArrayList<NoticeListView>();

        adapter = new NoticeListAdapter(getApplicationContext(), noticeList);
        noticeListView.setAdapter(adapter);

        new BackgroundTask().execute();

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write Post
                Intent intent = new Intent(NoticeActivity.this, NoticeWriteActivity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
                finish();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        noticeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = noticeList.get(position).getTitle();
                String name = noticeList.get(position).getName();
                String date = noticeList.get(position).getDate();
                String content = noticeList.get(position).getContent();

                NoticeData noticeData = new NoticeData(title, name, date, content); // parcelable
                Intent intent = new Intent(NoticeActivity.this, NoticeContentActivity.class);
                intent.putExtra("noticeData", noticeData);
                startActivity(intent);
            }
        });
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
