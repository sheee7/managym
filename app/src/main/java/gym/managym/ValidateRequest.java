package gym.managym;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

class ValidateRequest extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserValidate.php";
    private Map<String, String> userInfo;

    public ValidateRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        userInfo = new HashMap<>();
        userInfo.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return userInfo;
    }
/*
    Response.Listener<String> responseListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
                new RegisterRequest(12345, 1234, "a", "b", "c", "d". responseListener);
                */
}
