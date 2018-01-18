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

public class TimePickerDialog extends DialogFragment {

    TimePickerDialogListener mListener;


    public interface TimePickerDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int day, int hour, int minute);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_time_picker, null);
        builder.setView(view);


        final NumberPicker dayPicker = (NumberPicker) view.findViewById(R.id.day_picker);
        final NumberPicker hourPicker = (NumberPicker) view.findViewById(R.id.hour_picker);
        final NumberPicker minutePicker = (NumberPicker) view.findViewById(R.id.minute_picker);
        dayPicker.setMinValue(0);   // min value 0
        dayPicker.setMaxValue(20); // max value 100
        dayPicker.setWrapSelectorWheel(false);

        hourPicker.setMinValue(0);   // min value 0
        hourPicker.setMaxValue(23); // max value 100
        hourPicker.setWrapSelectorWheel(false);

        minutePicker.setMinValue(0);   // min value 0
        minutePicker.setMaxValue(59); // max value 100
        minutePicker.setWrapSelectorWheel(false);


        builder.setTitle("Chose timelimit")
                .setMessage("Set 0-0:0 for unlimited")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(TimePickerDialog.this,
                                dayPicker.getValue(), hourPicker.getValue(), minutePicker.getValue());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(TimePickerDialog.this);
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
            mListener = (TimePickerDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }



}

