package com.example.hesz.labproject;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by hesz on 1/9/2018.
 */

public class JobContentFragment extends Fragment implements DownloadCallback<String> , InfoDialog.InfoDialogListener{
    private static final String TAG = "JobContentFragment";

    public static final String SEND_JOB_URI = Constants.REST_SERVER_ADDRESS + Constants.SEND_REQUEST_ADDRESS_SUFFIX;



    private RecyclerView recyclerView;

    private MyItemRecyclerViewAdapter adapter;

    private List mItems;


    private SendDataTask mSendDataTask;
    private boolean mSendingJobs = false;

    private ProgressDialog progressDialog;



    public JobContentFragment() {
    }

    public void notifyDatasetChanged(){
        adapter.notifyDataSetChanged();
    }


    public void setList(List itemsList){
        mItems = itemsList;
        adapter = new MyItemRecyclerViewAdapter(mItems);

    }

    public static JobContentFragment newInstance(List itemsList) {
        JobContentFragment fragment = new JobContentFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tabbed, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        Context context = rootView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

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
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void sendJob(String sendData) {
        if (!mSendingJobs) {
            // Execute the async download.
            mSendingJobs = true;

            //updateFromDownload(Content.jobs);

            mSendDataTask = new SendDataTask(this, sendData);
            mSendDataTask.execute(SEND_JOB_URI);
        }
    }

    private void makeToast(String message){
        Toast.makeText(getContext(), message,
                Toast.LENGTH_LONG).show();

    }

    private void showInfoDialog(int jobId, String action) {
        InfoDialog dialogFragment = new InfoDialog();
        dialogFragment.setCallback(this);
        Bundle args = new Bundle();
        args.putInt("JOB_ID", jobId);
        args.putString("ACTION", action);
        dialogFragment.setArguments(args);
        dialogFragment.show(getActivity().getFragmentManager(), "InfoDialog");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int jobId, String action) {
        progressDialog = new ProgressDialog(getContext(),
                R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending...");
        progressDialog.show();

        String body = "";
        JSONObject object = new JSONObject();

        try {
            object.put("method",action);
            JSONObject params = new JSONObject();
            params.put("jobId", jobId);
            object.put("params",params);
            body = object.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendJob(body);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }


    private class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder> implements View.OnLongClickListener {

        public final List<JobItem> mValues;


        public MyItemRecyclerViewAdapter(List<JobItem> items) {
            mValues = items;
        }

        @Override
        public com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_job, parent, false);
            return new com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final com.example.hesz.labproject.JobContentFragment.MyItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTVjobid.setText(""+mValues.get(position).jobId);
            holder.mTVpartition.setText(mValues.get(position).partitionName);
            holder.mTVname.setText(mValues.get(position).jobName);
            holder.mTVuser.setText(mValues.get(position).submittingUser);
            holder.mTVstatus.setText(mValues.get(position).status);
            holder.mTVtime.setText(mValues.get(position).time);
            holder.mTVnodelist.setText(mValues.get(position).nodelist);
            if(holder.mItem.status.equals("running"))
                holder.mTVstatus.setTextColor(getResources().getColor(R.color.secondaryLightColor));
            else if(holder.mItem.status.equals("completed"))
                holder.mTVstatus.setTextColor(getResources().getColor(R.color.primaryLightColor));
            else
                holder.mTVstatus.setTextColor(Color.GRAY);

            if (mValues.get(position).status.equals("running") ||
                    mValues.get(position).status.equals("failed") ||
                    mValues.get(position).status.equals("completed"))
                holder.mView.setOnLongClickListener(this);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public boolean onLongClick(View v) {



            int jobId = Integer.valueOf(((TextView)v.findViewById(R.id.jobid)).getText().toString());
            String status = ((TextView)v.findViewById(R.id.status)).getText().toString();
            String action;
            if (status.equals("running")){
                action = "Cancel";
            }
            else
                action = "Restart";

            showInfoDialog(jobId,action);

            return false;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mTVjobid;
            public final TextView mTVpartition;
            public final TextView mTVname;
            public final TextView mTVuser;
            public final TextView mTVstatus;
            public final TextView mTVtime;
            public final TextView mTVnodelist;
            public JobItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTVjobid = (TextView) view.findViewById(R.id.jobid);
                mTVpartition = (TextView) view.findViewById(R.id.partition);
                mTVname = (TextView) view.findViewById(R.id.name);
                mTVuser = (TextView) view.findViewById(R.id.user);
                mTVstatus = (TextView) view.findViewById(R.id.status);
                mTVtime = (TextView) view.findViewById(R.id.time);
                mTVnodelist = (TextView) view.findViewById(R.id.nodelist);

            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTVname.getText() + "'";
            }


        }
    }








}

