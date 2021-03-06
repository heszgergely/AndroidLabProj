package com.example.hesz.labproject;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

//import com.google.android.gms.common.api.Response;



public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;
    private String userID;
    private String userEmail;
    private String userPassword;
    private String userLab;
    private boolean validate = false; // For ID check not used at the moment
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        spinner = (Spinner) findViewById(R.id.lab_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.labs, android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        final EditText idText = (EditText) findViewById(R.id.userID);
        final EditText emailText = (EditText) findViewById(R.id.email);
        final EditText passwordText = (EditText) findViewById(R.id.password);

        Button register_button = (Button)findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                userID = idText.getText().toString();
                userEmail = emailText.getText().toString();
                userPassword = passwordText.getText().toString();
                userLab = spinner.getSelectedItem().toString();


                // Checking for the empty space
                if(userID.equals("")|| userEmail.equals("")||userPassword.equals("")||userLab.equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("Please fill out all the forms.")
                            .setNegativeButton("Back", null)
                            .create();
                    dialog.show();
                    return;
                }

                // Successfully registered
                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response){
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("Registered Successfully.")
                                        .setPositiveButton("OK", null)
                                        .create();
                                dialog.show();

                                Toast.makeText(RegisterActivity.this, "User registration successful!", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("Failed to register.")
                                        .setNegativeButton("Back", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                RegisterRequest registerRequest = new RegisterRequest(userID, userEmail, userPassword, userLab, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(dialog != null)
        {
            dialog.dismiss();
            dialog = null;
        }
    }
}

