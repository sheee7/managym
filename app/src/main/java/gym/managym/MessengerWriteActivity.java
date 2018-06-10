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

public class MessengerWriteActivity extends AppCompatActivity {
    private Bundle bundle;
    private UserData userData;
    private AlertDialog dialog;
    private boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger_write);

        bundle = getIntent().getExtras();
        userData = bundle.getParcelable("userData");
        final EditText messengerWriteTo = findViewById(R.id.messageWriteTo);
        final EditText messengerWriteContent = findViewById(R.id.messageWriteContent);
        final Button postButton = findViewById(R.id.sendButton);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = userData.getUserID();
                final String recipient = messengerWriteTo.getText().toString();
                final String content = messengerWriteContent.getText().toString();

                if (recipient.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessengerWriteActivity.this);
                    dialog = builder.setMessage("받을 사람의 ID를 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                else if (content.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessengerWriteActivity.this);
                    dialog = builder.setMessage("내용을 입력하세요.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }
                else if (content.length() >= 100) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessengerWriteActivity.this);
                    dialog = builder.setMessage("내용은 100자 이내어야 합니다.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListenerA = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            success = jsonResponse.getBoolean("success");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListenerA);
                RequestQueue queueA = Volley.newRequestQueue(MessengerWriteActivity.this);
                queueA.add(validateRequest);

                if (success) { // 존재하면 success = false, 없으면 true
                    AlertDialog.Builder builder = new AlertDialog.Builder(MessengerWriteActivity.this);
                    dialog = builder.setMessage("존재하지 않는 아이디입니다.").setNegativeButton("OK", null).create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListenerB = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MessengerWriteActivity.this);
                                builder.setMessage("메시지를 전송하였습니다.");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                                builder.create();
                                builder.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MessengerWriteActivity.this);
                                dialog = builder.setMessage("Failed").setNegativeButton("Retry", null).create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                MessengerSend messengerSend = new MessengerSend(userID, "MESENGER_"+recipient, content, responseListenerB);
                RequestQueue queueB = Volley.newRequestQueue(MessengerWriteActivity.this);
                queueB.add(messengerSend);
            }
        });
    }
}

class MessengerSend extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/MessengerSend.php";
    private Map<String, String> parameters;

    public MessengerSend(String userID, String recipient, String content, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null); // 해당 정보를 POST 방식으로 URL에 전송
        parameters = new HashMap<>();
        parameters.put("sender", userID);
        parameters.put("recipient", recipient);
        parameters.put("content", content);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
