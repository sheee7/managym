package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NoticeWriteActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private NoticeData noticeData;
    private boolean write;
    private AlertDialog dialog;
    private NoticeActivity noticeActivity = (NoticeActivity) NoticeActivity.noticeActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        write = getIntent().getBooleanExtra("write", true);
        final EditText noticeWriteTitle = findViewById(R.id.noticeWriteTitle);
        final EditText noticeWriteContent = findViewById(R.id.noticeWriteContent);
        final Button postButton = findViewById(R.id.postButton);

        if (!write) { // if not writing (= notice modify)
            noticeData = bundle.getParcelable("noticeData");
            noticeWriteTitle.setText(noticeData.getTitle());
            noticeWriteContent.setText(noticeData.getContent());
            postButton.setText("Modify");
        }

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName;
                final String noticeTitle = noticeWriteTitle.getText().toString();
                final String noticeDate;
                final String noticeContent = noticeWriteContent.getText().toString();

                if (write) {
                    userName = userData.getName();
                    noticeDate = "";
                    }
                else {
                    userName = "";
                    noticeDate = noticeData.getDate();
                    }

                if (noticeTitle.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                    dialog = builder.setMessage("제목을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (noticeContent.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                    dialog = builder.setMessage("내용을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                                if (write)
                                    builder.setMessage("공지사항을 게시하시겠습니까?");
                                else
                                    builder.setMessage("공지사항을 수정하시겠습니까?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        gotoNoticeActivity(userData);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                if (write) {
                    NoticePost noticePost = new NoticePost(noticeTitle, userName, noticeContent, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(NoticeWriteActivity.this);
                    queue.add(noticePost);
                }
                else  {
                    NoticeModify noticeModify = new NoticeModify(noticeTitle, noticeDate, noticeContent, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(NoticeWriteActivity.this);
                    queue.add(noticeModify);
                }
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
                gotoNoticeActivity(userData);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        gotoNoticeActivity(userData);
    }

    private void gotoNoticeActivity(UserData userData) {
        Intent intent = new Intent(NoticeWriteActivity.this, NoticeActivity.class);
        intent.putExtra("userData", userData);
        startActivity(intent);
        noticeActivity.finish();
        finish();
    }
}

class NoticePost extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/NoticePost.php";
    private Map<String, String> parameters;

    public NoticePost(String noticeTitle, String noticeName, String noticeContent, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("noticeTitle", noticeTitle);
        parameters.put("noticeName", noticeName);
        parameters.put("noticeContent", noticeContent);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}

class NoticeModify extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/NoticeModify.php";
    private Map<String, String> parameters;

    public NoticeModify(String noticeTitle, String noticeDate, String noticeContent, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("noticeTitle", noticeTitle);
        parameters.put("noticeDate", noticeDate);
        parameters.put("noticeContent", noticeContent);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
