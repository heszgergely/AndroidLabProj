package com.example.hesz.labproject;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

//import com.google.android.gms.common.api.Response;
//import java.lang.String;

/**
 * Created by stella on 07/01/2018.
 */

public class RegisterRequest extends StringRequest {


    private static final String REGISTER_URL = Constants.REST_SERVER_ADDRESS + Constants.REGISTER_ADDRESS_SUFFIX;

    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userEmail, String userPassword, String userLab, Response.Listener<String> listener){
        super(Method.POST, REGISTER_URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userEmail", userEmail);
        parameters.put("userPassword", userPassword);
        parameters.put("userLab", userLab);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
