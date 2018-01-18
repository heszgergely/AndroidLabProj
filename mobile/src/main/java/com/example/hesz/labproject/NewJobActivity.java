package com.example.hesz.labproject;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewJobActivity extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.TimePickerDialogListener, View.OnFocusChangeListener {

    private static final String TAG = "NewJobActivity";


    public static final String PARTITIONS_URI = Constants.REST_SERVER_ADDRESS + Constants.PARTITIONS_ADDRESS_SUFFIX;
    public static final String NODES_URI = Constants.REST_SERVER_ADDRESS + Constants.NODES_ADDRESS_SUFFIX;
    public static final String SEND_JOB_URI = Constants.REST_SERVER_ADDRESS + Constants.SEND_REQUEST_ADDRESS_SUFFIX;

    //private DownloadCallback mCallback;
    private DownloadTask mDownloadPartitions;
    private DownloadCallback<String> downloadPartitionsCallback = null;

    private DownloadTask mDownloadNodes;
    private DownloadCallback<String> downloadNodesCallback = null;


    private SendDataTask mSendDataTask;
    private DownloadCallback<String> sendingJobsCallback = null;


    private boolean mDownloadingPartitions = false;
    private boolean mDownloadingNodes = false;
    private boolean mSendingJobs = false;

    private ProgressDialog progressDialog;


    private List<NodeItem> nodesList= null;
    private List<PartitionItem> partitionsList= null;


    private static String [] partitionsStringArray ;
    private Button setTime;
    private AutoCompleteTextView partitionsEditText;
    private SeekBar memoryBar;
    private TextView memoryText;
    private TextInputLayout jobNameWrapper;
    private TextInputLayout commandWrapper;
    private Button sendJobButton;
    private FloatingActionButton fab;
    private TextView timeLimitText;
    private EditText nodeNumber;
    private EditText cpuNumber;
    int memoryBarProgress = 0;
    int maxMemory = 4000; //Start with 4GB



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Start a new job");

        setSupportActionBar(toolbar);

        setTime = (Button) findViewById(R.id.set_time);

        partitionsEditText = findViewById(R.id.partition_autocomplete);
        partitionsEditText.setOnFocusChangeListener(this);

        memoryBar =findViewById(R.id.memory_bar);
        memoryText = findViewById(R.id.memory_size);
        nodeNumber = findViewById(R.id.node_number);
        cpuNumber = findViewById(R.id.cpu_number);
        jobNameWrapper = findViewById(R.id.job_name_text_input);
        jobNameWrapper.setOnFocusChangeListener(this);
        commandWrapper = findViewById(R.id.command_text_input);
        commandWrapper.setOnFocusChangeListener(this);
        timeLimitText = findViewById(R.id.time_limit_text);

        sendJobButton = findViewById(R.id.send_job_buttton);

        setSeekbarText(memoryBarProgress *maxMemory/100);


        fab = (FloatingActionButton) findViewById(R.id.fab);

        setOnclickListeners();

        startDownload();


    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }



    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void setOnclickListeners(){

        sendJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    progressDialog = new ProgressDialog(NewJobActivity.this,
                            R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Sending...");
                    progressDialog.show();

                    sendJob(getJSONStringFromUI());

                }

            }
        });


        memoryBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                memoryBarProgress = progress;
                setSeekbarText(progress*maxMemory/100);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Open ConnectBot?", Snackbar.LENGTH_LONG)
                        .setAction("OPEN", NewJobActivity.this).show();
            }
        });
    }

    private String getJSONStringFromUI() {
        JSONObject object = new JSONObject();

        String jobName = jobNameWrapper.getEditText().getText().toString();
        String command = commandWrapper.getEditText().getText().toString();
        String partition = partitionsEditText.getText().toString();
        int nodes;
        if(nodeNumber.getText().toString().equals(""))
            nodes = 0;
        else
            nodes = Integer.valueOf(nodeNumber.getText().toString());
        int cpus;
        if(cpuNumber.getText().toString().equals(""))
            cpus = 0;
        else
            cpus = Integer.valueOf(cpuNumber.getText().toString());
        int memory = memoryBarProgress *maxMemory/100;
        String timeLimit = timeLimitText.getText().toString();

        try {
            object.put("method","NEW_JOB");
            JSONObject params = new JSONObject();
            params.put("jobName", jobName);
            params.put("command", command);
            params.put("partition", partition);
            params.put("nodes", nodes);
            params.put("cpus", cpus);
            params.put("memory", memory);
            params.put("timelimit", timeLimit);
            object.put("params",params);
            return object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    private void sendJob(String sendData) {
        if (!mSendingJobs) {
            // Execute the async download.
            mSendingJobs = true;

            //updateFromDownload(Content.jobs);

            createSendJobCallback();
            mSendDataTask = new SendDataTask(sendingJobsCallback, sendData);
            mSendDataTask.execute(SEND_JOB_URI);
        }
    }

    private void createSendJobCallback() {
        sendingJobsCallback = new DownloadCallback<String>() {
            boolean error = false;
            @Override
            public void updateFromDownload(String result) {
                if(error)
                    makeToast("Error while processing request!");
                if (progressDialog!=null)
                    progressDialog.dismiss();


                makeToast("Result: " + result);

                Log.d(TAG,"Download result: " + result);
            }

            @Override
            public NetworkInfo getActiveNetworkInfo() {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) NewJobActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo;
            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {
                switch(progressCode) {
                    //TODO:Make this useful
                    // You can add UI behavior for memoryBarProgress updates here.
                    case Progress.ERROR:
                        Log.d(TAG,"Error memoryBarProgress update");
                        error = true;
                        finishDownloading();
                        break;
                    case Progress.CONNECT_SUCCESS:
                        Log.d(TAG,"Succesful connection!");
                        break;
                    case Progress.GET_INPUT_STREAM_SUCCESS:
                        Log.d(TAG,"Input stream successfully read");
                        break;
                    case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                        Log.d(TAG,"Processing input stream...");
                        break;
                    case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                        Log.d(TAG,"Input stream succesfully processed!");
                        break;
                }
            }

            @Override
            public void finishDownloading() {
                if (mSendDataTask != null) {
                    mSendDataTask.cancel(true);
                }
            }
        };
    }

    void setSeekbarText(int mb ){
        if (mb==0)
            memoryText.setText("Undefined");
        else
            memoryText.setText(mb + " MB");
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("ssh://user@host:port/#nickname")));
    }

    private void makeToast(String message){
        Toast.makeText(getApplicationContext(), message,
                Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPause() {
        finishDownloads();
        super.onPause();
    }

    private void finishDownloads() {
        if(downloadPartitionsCallback != null){
            downloadPartitionsCallback.finishDownloading();
        }

        if(downloadNodesCallback != null){
            downloadNodesCallback.finishDownloading();
        }
    }


    private void startDownload() {
        if (!mDownloadingPartitions) {
            // Execute the async download.
            mDownloadingPartitions = true;

            //updateFromDownload(Content.jobs);

            createPartitionsCallback();
            mDownloadPartitions = new DownloadTask(downloadPartitionsCallback);
            mDownloadPartitions.execute(PARTITIONS_URI);
        }

        if (!mDownloadingNodes) {
            // Execute the async download.
            mDownloadingNodes = true;

            //updateFromDownload(Content.jobs);
            createNodeCallback();
            mDownloadNodes = new DownloadTask(downloadNodesCallback);
            mDownloadNodes.execute(NODES_URI);
        }
    }

    private void createPartitionsCallback() {
        downloadPartitionsCallback = new DownloadCallback<String>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void updateFromDownload(String result) {
                new AsyncTask<String,Void,List<PartitionItem>>(){

                    @Override
                    protected List doInBackground(String... result) {
                        try {
                            JSONArray jsonArray = new JSONArray(result[0]);

                            List<PartitionItem> newValues = PartitionItem.parsePartitionsFromJSON(jsonArray);
                            partitionsStringArray = new String[newValues.size()];
                            for(int i= 0; i<newValues.size(); i++){
                                partitionsStringArray[i] = newValues.get(i).partitionName;
                            }
                            return newValues;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<PartitionItem> newValues){
                        makeToast("Partitions downloaded!");
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_dropdown_item_1line,partitionsStringArray );
                        partitionsEditText.setAdapter(adapter);
                        partitionsList = newValues;
                        //adapter.swapValues(newValues);
                    }
                }.execute(result);
            }

            @Override
            public NetworkInfo getActiveNetworkInfo() {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) NewJobActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo;
            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {
                switch(progressCode) {
                    //TODO:Make this useful
                    // You can add UI behavior for memoryBarProgress updates here.
                    case Progress.ERROR:
                        Log.d(TAG,"Error memoryBarProgress update");
                        break;
                    case Progress.CONNECT_SUCCESS:
                        Log.d(TAG,"Succesful connection!");
                        break;
                    case Progress.GET_INPUT_STREAM_SUCCESS:
                        Log.d(TAG,"Input stream successfully read");
                        break;
                    case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                        Log.d(TAG,"Processing input stream...");
                        break;
                    case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                        Log.d(TAG,"Input stream succesfully processed!");
                        break;
                }
            }

            @Override
            public void finishDownloading() {
                if (mDownloadPartitions != null) {
                    mDownloadPartitions.cancel(true);
                }
            }
        };
    }

    private void createNodeCallback() {
        downloadNodesCallback = new DownloadCallback<String>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void updateFromDownload(String result) {
                new AsyncTask<String, Void, List<NodeItem>>() {

                    @Override
                    protected List doInBackground(String... result) {
                        try {
                            JSONArray jsonArray = new JSONArray(result[0]);

                            List<NodeItem> newValues = NodeItem.parseNodesFromJSON(jsonArray);
                            int newMaxMemory = 0;
                            for(NodeItem item: newValues) {
                                if (newMaxMemory < item.memory) {
                                    newMaxMemory = item.memory;
                                }
                            }

                            maxMemory=newMaxMemory;


                            return newValues;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<NodeItem> newValues) {
                        nodesList = newValues;
                        setSeekbarText(memoryBarProgress *maxMemory/100);
                        makeToast("Nodes downloaded!");
                        //adapter.swapValues(newValues);
                    }
                }.execute(result);
            }

            @Override
            public NetworkInfo getActiveNetworkInfo() {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) NewJobActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo;
            }

            @Override
            public void onProgressUpdate(int progressCode, int percentComplete) {
                switch (progressCode) {
                    //TODO:Make this useful
                    // You can add UI behavior for memoryBarProgress updates here.
                    case Progress.ERROR:
                        Log.d(TAG, "Error memoryBarProgress update");
                        break;
                    case Progress.CONNECT_SUCCESS:
                        Log.d(TAG, "Succesful connection!");
                        break;
                    case Progress.GET_INPUT_STREAM_SUCCESS:
                        Log.d(TAG, "Input stream successfully read");
                        break;
                    case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                        Log.d(TAG, "Processing input stream...");
                        break;
                    case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                        Log.d(TAG, "Input stream succesfully processed!");
                        break;
                }
            }

            @Override
            public void finishDownloading() {
                if (mDownloadNodes != null) {
                    mDownloadNodes.cancel(true);
                }
            }
        };
    }

    private void showTimePickerDialog() {
        TimePickerDialog dialogFragment = new TimePickerDialog();
        dialogFragment.show(getFragmentManager(), "TimePickerDialog");
    }




    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int day, int hour, int minute) {
        setTimeLimitText(day,hour,minute);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    void setTimeLimitText(int day, int hour, int minute){
        if(day==0 && hour ==0 && minute == 0)
            timeLimitText.setText("unlimited");
        else
            timeLimitText.setText(day+"-"+hour+":"+minute);
    }


    public boolean validateForm() {
        boolean valid = true;

        String jobName = jobNameWrapper.getEditText().getText().toString();
        String command = commandWrapper.getEditText().getText().toString();

        if (jobName.isEmpty()) {
            jobNameWrapper.setError("This field cannot be empty");
            valid = false;
        } else {
            jobNameWrapper.setError(null);
        }

        if (command.isEmpty()) {
            commandWrapper.setError("This field cannot be empty");
            valid = false;
        } else {
            commandWrapper.setError(null);
        }

        return valid;
    }


}
