package com.tac.reportingDemo.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tac.reportingDemo.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddVisitActivity extends AppCompatActivity {

    @BindView(R.id.datePicker)
    TextView datePicker;

    @BindView(R.id.timePicker)
    TextView timePicker;

    @BindView(R.id.custom_spinner)
    TextView custom_spinner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visit);
        ButterKnife.bind(this);

        // Date Picker Code
        // Get current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Format the current date
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, dd", Locale.US); // Day of the week, day of the month
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.US); // Month name
        String dayAndYear = dayFormat.format(c.getTime()).toUpperCase();
        String monthStr = monthFormat.format(c.getTime()).toUpperCase(); // Convert month to uppercase

        // Combine the parts
        String currentDate = dayAndYear + " " + monthStr;

        // Set the current date as the text of the TextView
        datePicker.setText(currentDate);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current date
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddVisitActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.set(year, monthOfYear, dayOfMonth);

                                // Format day and year, and get month name separately
                                SimpleDateFormat dayFormat = new SimpleDateFormat("EEE, dd", Locale.US); // Day of the week, day of the month
                                SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.US); // Month name
                                String dayAndYear = dayFormat.format(calendar.getTime()).toUpperCase();
                                String month = monthFormat.format(calendar.getTime()).toUpperCase(); // Convert month to uppercase

                                // Combine the parts
                                String selectedDate = dayAndYear + " " + month;

                                datePicker.setText(selectedDate); // Assuming 'datePicker' is a TextView or similar
                            }
                        }, year, month, day);

                // Prevent future dates from being selected
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

                datePickerDialog.show();
            }
        });

        //TimePicker Code
        // Format and display the current time by default
        final Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String formattedTime = dateFormat.format(calendar.getTime()).toUpperCase(); // Ensure AM/PM is in uppercase
        timePicker.setText(formattedTime); // Display the current time on the TextView


        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get current time
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // TimePickerDialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(AddVisitActivity.this,
                        (view, hourOfDay, minutee) -> {
                            // Calendar instance to handle AM/PM
                            Calendar selectedTime = Calendar.getInstance();
                            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            selectedTime.set(Calendar.MINUTE, minutee);

                            // Formatting time with AM/PM in uppercase
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            String formattedTime = dateFormat.format(selectedTime.getTime()).toUpperCase();

                            // Setting formatted time on TextView
                            timePicker.setText(formattedTime);
                        }, hour, minute, false); // false for 12-hour format
                timePickerDialog.show();
            }
        });

        custom_spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddVisitActivity.this,ProductActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
