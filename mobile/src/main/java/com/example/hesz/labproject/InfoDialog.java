package com.example.hesz.labproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by hesz on 1/10/2018.
 */

public class InfoDialog extends DialogFragment {

    NoticeDialogListener mListener;


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int value);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_info, null);
        builder.setView(view);


        final NumberPicker np1 = (NumberPicker) view.findViewById(R.id.day_picker);
        np1.setMaxValue(70); // max value 100
        np1.setMinValue(20);   // min value 0
        np1.setWrapSelectorWheel(false);


        builder.setTitle("Chose sensitivity")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(InfoDialog.this, np1.getValue());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }



}

