package com.ravimandala.labs.nytimessearch.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.ravimandala.labs.nytimessearch.R;
import com.ravimandala.labs.nytimessearch.fragment.DatePickerFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @Bind(R.id.etBeginDate)
    EditText etBeginDate;

    @Bind(R.id.spSortOrder)
    Spinner spSortOrder;

    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);
        Intent inputIntent = getIntent();
        cal.set(inputIntent.getIntExtra("begin_date_year", 2016),
                inputIntent.getIntExtra("begin_date_month", 1),
                inputIntent.getIntExtra("begin_date_day", 1));
        setSpinnerToValue(spSortOrder, inputIntent.getStringExtra("sort_order"));
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


        cal.set(year, monthOfYear, dayOfMonth);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        etBeginDate.setText(df.format(cal.getTime()));
    }

    public void onCheckBoxSelected(View view) {
        // Do something
    }

    public void onSaveSettingsClicked(View view) {
        // Prepare data intent
        Intent data = new Intent();
        // Pass relevant data back as a result
        data.putExtra("begin_date_year", cal.get(Calendar.YEAR));
        data.putExtra("begin_date_month", cal.get(Calendar.MONTH));
        data.putExtra("begin_date_day", cal.get(Calendar.DAY_OF_MONTH));
        data.putExtra("sort_order", spSortOrder.getSelectedItem().toString());
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
