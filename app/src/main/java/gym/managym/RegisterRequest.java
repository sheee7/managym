package gym.managym;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://jeffjks.cafe24.com/UserRegister.php";
    private HashMap<String, String> parameters;

    public RegisterRequest(String userID, String userPW, String info1, String info2, String info3, String info4, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPW", userPW);
        parameters.put("info1", info1);
        parameters.put("info2", info2);
        parameters.put("info3", info3);
        parameters.put("info4", info4);
    }

    @Override
    public HashMap<String, String> getParams() {
        return parameters;
    }
}
