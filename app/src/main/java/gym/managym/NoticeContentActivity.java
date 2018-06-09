package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NoticeContentActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private NoticeData noticeData;
    private AlertDialog dialog;
    private NoticeActivity noticeActivity = (NoticeActivity) NoticeActivity.noticeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_content);

        final TextView titleText = findViewById(R.id.noticeTitleText);
        final TextView nameText = findViewById(R.id.noticeNameText);
        final TextView dateText = findViewById(R.id.noticeDateText);
        final TextView contentText = findViewById(R.id.noticeContentText);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        noticeData = bundle.getParcelable("noticeData");

        titleText.setText(noticeData.getTitle());
        nameText.setText(noticeData.getName());
        dateText.setText(noticeData.getDate());
        contentText.setText(noticeData.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (userData.getAdmin() == 0)
            getMenuInflater().inflate(R.menu.activity_menu_default, menu);
        else
            getMenuInflater().inflate(R.menu.activity_menu_content, menu); // Admin일 경우 수정 가능
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.menu_action_modify:
                modifyNotice();
                break;
            case R.id.menu_action_delete:
                deleteNotice();
                break;
            case R.id.menu_back:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyNotice() { // Modify Notice
        Intent intent = new Intent(NoticeContentActivity.this, NoticeWriteActivity.class);
        intent.putExtra("noticeData", noticeData);
        intent.putExtra("userData", userData);
        intent.putExtra("write", false);
        startActivity(intent);
        finish();
    }

    private void deleteNotice() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NoticeContentActivity.this);
                        builder.setMessage("공지사항을 삭제하시겠습니까?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                refreshNoticeIntent();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create();
                        builder.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NoticeContentActivity.this);
                        dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                        dialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        NoticeDelete noticeDelete = new NoticeDelete(noticeData.getDate(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(NoticeContentActivity.this);
        queue.add(noticeDelete);
    }

    private void refreshNoticeIntent() {
        noticeActivity.finish();
        Intent intent = new Intent(NoticeContentActivity.this, NoticeActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
        finish();
    }
}

class NoticeDelete extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/NoticeDelete.php";
    private Map<String, String> parameters;

    public NoticeDelete(String noticeDate, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("noticeDate", noticeDate);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class NoticeData implements Parcelable {
    private String title;
    private String name;
    private String date;
    private String content;

    public NoticeData() { }

    public NoticeData(Parcel in) {
        readFromParcel(in);
    }

    public NoticeData(String title, String name, String date, String content) {
        this.title = title;
        this.name = name;
        this.date = date;
        this.content = content;
    }

    public static final Parcelable.Creator<NoticeData> CREATOR = new Parcelable.Creator<NoticeData>() {
        public NoticeData createFromParcel(Parcel in) {
            return new NoticeData(in);
        }

        public NoticeData[] newArray (int size) {
            return new NoticeData[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public String getTitle() {
        return title;
    }
    public String getName() {
        return name;
    }
    public String getDate() {
        return date;
    }
    public String getContent() {
        return content;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.name);
        dest.writeString(this.date);
        dest.writeString(this.content);
    }
    private void readFromParcel(Parcel in) {
        title = in.readString();
        name = in.readString();
        date = in.readString();
        content = in.readString();
    }
}