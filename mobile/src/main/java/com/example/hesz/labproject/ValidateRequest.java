package com.example.hesz.labproject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shgol on 09/01/2018.
 */

public class ValidateRequest extends StringRequest {

    final static private String LOGIN_SERVER_ADDRESS = "http://128.179.163.163/android_proj/UserValidate.php";
    private Map<String, String> parameters;

    public ValidateRequest(String userID, Response.Listener<String> listener){
        super(Method.POST, LOGIN_SERVER_ADDRESS, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
