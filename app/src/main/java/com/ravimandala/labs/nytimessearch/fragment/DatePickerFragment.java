package com.ravimandala.labs.nytimessearch.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    private static final String TAG = "NYTSearch";
    Date beginDate;

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if (beginDate == null)
            beginDate = Calendar.getInstance().getTime();

        // Activity needs to implement this interface
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();

        Log.d(TAG, "Opening DatePickerDialog with date: " + beginDate.toString());
        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, beginDate.getYear(), beginDate.getMonth(), beginDate.getDay());
    }
}