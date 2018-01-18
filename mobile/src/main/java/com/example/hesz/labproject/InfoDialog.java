package com.example.hesz.labproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by hesz on 1/10/2018.
 */

public class InfoDialog extends DialogFragment {

    InfoDialogListener mListener;


    public interface InfoDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int jobId, String action);
        public void onDialogNegativeClick(DialogFragment dialog);
    }


    public void setCallback(InfoDialogListener callback){
        mListener = callback;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final int jobId = getArguments().getInt("JOB_ID",0);
        final String action = getArguments().getString("ACTION","Cancel");



        builder.setTitle(action + " job?")
                .setMessage("Job id: " + jobId )
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(InfoDialog.this, jobId, action);
                    }
                })
                .setNegativeButton("Cancel dialog", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(InfoDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
     /*   try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (InfoDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }


        */
    }



}

