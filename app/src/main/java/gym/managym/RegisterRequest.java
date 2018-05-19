package gym.managym;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserRegister.php";
    private HashMap<String, String> parameters;

    public RegisterRequest(String userID, String userPW, String name, String birth, String phone, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPW", userPW);
        parameters.put("name", name);
        parameters.put("birth", birth);
        parameters.put("phone", phone);
    }

    @Override
    public HashMap<String, String> getParams() {
        return parameters;
    }
}
