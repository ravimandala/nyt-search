package com.ravimandala.labs.nytimessearch.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.ravimandala.labs.nytimessearch.Constants;
import com.ravimandala.labs.nytimessearch.R;
import com.ravimandala.labs.nytimessearch.fragment.DatePickerFragment;
import com.ravimandala.labs.nytimessearch.model.Settings;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @Bind(R.id.etBeginDate)
    EditText etBeginDate;

    @Bind(R.id.spSortOrder)
    Spinner spSortOrder;

    @Bind(R.id.cbArts)
    CheckBox cbArts;

    @Bind(R.id.cbFashionAndStyle)
    CheckBox cbFashionAndStyle;

    @Bind(R.id.cbSports)
    CheckBox cbSports;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);
        settings = getIntent().getParcelableExtra("settings");
        if (settings == null) settings = new Settings();
        if (settings.isOldestFirst()) {
            spSortOrder.setSelection(1);
        } else {
            spSortOrder.setSelection(0);
        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        etBeginDate.setText(df.format(settings.getBeginDate()));
    }

    // attach to an onclick handler to show the date picker
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setBeginDate(settings.getBeginDate());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // handle the date selected
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        settings.setBeginDate(cal.getTime());
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        etBeginDate.setText(df.format(settings.getBeginDate()));
    }

    public void onSaveSettingsClicked(View view) {
        settings.setIsOldestFirst(spSortOrder.getSelectedItemId() == 1 );

        int newsDeskValues = 0;
        if (cbArts.isChecked()) newsDeskValues += Constants.ARTS;
        if (cbFashionAndStyle.isChecked()) newsDeskValues += Constants.FASHION_AND_STYLE;
        if (cbSports.isChecked()) newsDeskValues += Constants.SPORTS;
        settings.setNewsDeskValues(newsDeskValues);

        // Prepare data intent
        Intent data = new Intent();
        data.putExtra("settings", settings);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish(); // closes the activity, pass data to parent
    }
}
