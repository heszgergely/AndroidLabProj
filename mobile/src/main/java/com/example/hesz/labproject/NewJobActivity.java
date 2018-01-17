package com.example.hesz.labproject;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class NewJobActivity extends AppCompatActivity implements View.OnClickListener, InfoDialog.NoticeDialogListener{

    private static final String TAG = "NewJobActivity";


    public static final String PARTITIONS_URI = Constants.REST_SERVER_ADDRESS + Constants.PARTITIONS_ADDRESS_SUFFIX;
    public static final String NODES_URI = Constants.REST_SERVER_ADDRESS + Constants.NODES_ADDRESS_SUFFIX;

    //private DownloadCallback mCallback;
    private DownloadTask mDownloadPartitions;
    private DownloadCallback<String> downloadPartitionsCallback = null;
    private DownloadCallback<String> downloadNodesCallback = null;

    private DownloadTask mDownloadNodes;

    private static DataBaseHelper db;

    private boolean mDownloadingPartitions = false;
    private boolean mDownloadingNodes = false;

    Button setTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Start a new job");

        setSupportActionBar(toolbar);

        setTime = (Button) findViewById(R.id.set_time);

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialog();
            }
        });


        db = new DataBaseHelper(this.getApplicationContext());


        startDownload();



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Open ConnectBot?", Snackbar.LENGTH_LONG)
                        .setAction("OPEN", NewJobActivity.this).show();
            }
        });
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
                            return PartitionItem.parsePartitionsFromJSON(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<PartitionItem> newValues){
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
                    // You can add UI behavior for progress updates here.
                    case Progress.ERROR:
                        Log.d(TAG,"Error progress update");
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
                            return NodeItem.parseNodesFromJSON(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<NodeItem> newValues) {
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
                    // You can add UI behavior for progress updates here.
                    case Progress.ERROR:
                        Log.d(TAG, "Error progress update");
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

    private void showInfoDialog() {
        InfoDialog dialogFragment = new InfoDialog();
        dialogFragment.show(getFragmentManager(), "InfoDialog");
    }




    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int value) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
