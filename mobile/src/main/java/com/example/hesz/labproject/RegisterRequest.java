package com.example.hesz.labproject;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
//import com.google.android.gms.common.api.Response;

import java.lang.reflect.Method;
//import java.lang.String;
import java.util.HashMap;
import java.util.Map;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

/**
 * Created by stella on 07/01/2018.
 */

public class RegisterRequest extends StringRequest {

    final static private String LOGIN_SERVER_ADDRESS = "http://128.179.163.163/android_proj/UserRegister.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userID, String userEmail, String userPassword, String userLab, Response.Listener<String> listener){
        super(Method.POST, LOGIN_SERVER_ADDRESS, listener, null);
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
