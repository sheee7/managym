package gym.managym;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_write);

        final Bundle bundle = getIntent().getExtras();
        final UserData userData = bundle.getParcelable("userData");
        final EditText noticeWriteTitle = findViewById(R.id.noticeWriteTitle);
        final EditText noticeWriteContent = findViewById(R.id.noticeWriteContent);
        final Button postButton = findViewById(R.id.postButton);
        final Button previousButton = findViewById(R.id.previousButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = userData.getName();
                final String noticeTitle = noticeWriteTitle.getText().toString();
                final String noticeContent = noticeWriteContent.getText().toString();

                if (noticeTitle.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                    dialog = builder.setMessage("제목을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                if (noticeContent.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoticeWriteActivity.this);
                    dialog = builder.setMessage("제목을 입력하세요.").setNegativeButton("OK", null).create();
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
                                builder.setMessage("공지사항을 게시하겠습니까?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(NoticeWriteActivity.this, NoticeActivity.class);
                                        intent.putExtra("userData", userData);
                                        startActivity(intent);
                                        finish();
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
                NoticePost noticePost = new NoticePost(noticeTitle, userName, noticeContent, responseListener);
                RequestQueue queue = Volley.newRequestQueue(NoticeWriteActivity.this);
                queue.add(noticePost);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
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
