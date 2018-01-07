package com.example.hesz.labproject;

import com.google.android.gms.common.api.Response;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stella on 07/01/2018.
 * TODO : Update the Response.Class
 * TODO : Add ValidateRequest to check unique ID if necessary
 */

public class RegisterRequest extends StringRequest{

    final static private String URL = "http://webserver address added require";
    private Map<String, String> parameters;

    public RegisterRequest(String userEmail, String userPassword, String userLab, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userEmail", userEmail);
        parameters.put("userPassword", userPassword);
        parameters.put("userLab", userLab);
    }

    @Override
    public Map<String, String> getParameters(){
        return parameters;
    }
}
