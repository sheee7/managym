package gym.managym;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessengerActivity extends AppCompatActivity {
    private ListView messengerListView;
    private MessengerListAdapter adapter;
    private List<MessengerListView> messengerList;
    private Bundle bundle;
    private UserData userData;
    public static Activity messengerActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        messengerActivity = MessengerActivity.this;

        final TextView textEmpty = findViewById(R.id.messageListEmpty);
        final Button writeButton = findViewById(R.id.writeButton);

        messengerListView = findViewById(R.id.messageListView);
        messengerList = new ArrayList<MessengerListView>();
        adapter = new MessengerListAdapter(getApplicationContext(), messengerList);
        messengerListView.setAdapter(adapter);
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
                    String sender, content, date;
                    while(count < jsonArray.length()) {
                        JSONObject object = jsonArray.getJSONObject(count);
                        sender = object.getString("sender");
                        content = object.getString("content");
                        date = object.getString("date");
                        MessengerListView messenger = new MessengerListView(sender, content ,date);
                        messengerList.add(messenger);
                        adapter.notifyDataSetChanged();
                        count++;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (messengerList.size() != 0)
                    textEmpty.setVisibility(View.GONE);
                else
                    textEmpty.setVisibility(View.VISIBLE);
            }
        };
        MessengerReceive messengerReceive = new MessengerReceive("MESENGER_"+userData.getUserID(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(MessengerActivity.this);
        queue.add(messengerReceive);

        writeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // Write Messenger
                Intent intent = new Intent(MessengerActivity.this, MessengerWriteActivity.class);
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

class MessengerListView {
    String sender;
    String content;
    String date;

    public MessengerListView(String sender, String content, String date) {
        this.sender = sender;
        this.content = content;
        this.date = date;
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

class MessengerListAdapter extends BaseAdapter {
    private Context context;
    private List<MessengerListView> messageList;

    public MessengerListAdapter(Context context, List<MessengerListView> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int i) {
        return messageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(context, R.layout.activity_messenger_listview, null);
        TextView senderText = v.findViewById(R.id.messageSenderText);
        TextView contentText = v.findViewById(R.id.messageContentText);
        TextView dateText = v.findViewById(R.id.messageDateText);
        senderText.setText("보낸 사람 : "+messageList.get(i).getSender());
        contentText.setText(messageList.get(i).getContent());
        dateText.setText(messageList.get(i).getDate());

        v.setTag(messageList.get(i).getContent());
        return v;
    }
}

class MessengerReceive extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/MessengerReceive.php";
    private Map<String, String> parameters;

    public MessengerReceive(String recipient, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("recipient", recipient);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}