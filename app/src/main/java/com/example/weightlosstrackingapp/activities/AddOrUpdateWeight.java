package com.example.weightlosstrackingapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.weightlosstrackingapp.R;
import com.example.weightlosstrackingapp.WeightData;
import com.example.weightlosstrackingapp.databases.RecordedWeights;

import java.util.Calendar;
import java.util.Date;

public class AddOrUpdateWeight extends Activity {
    private RecordedWeights recordedWeightsDB;
    private Button weightPicker;
    private Button cancelButton;
    private Button addOrUpdate;
    private TextView weightInput;
    private Date selectedDate;
    private TextView title;
    private double userWeight;

    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_or_update_data);

        initializeViews();

        // external data
        recordedWeightsDB = new RecordedWeights(getApplicationContext());
        Bundle bundle = getIntent().getExtras();

        if(bundle != null) {
            id = bundle.getLong("id");
            WeightData weightData = recordedWeightsDB.retrieveByID(id);

            // extract data to display in UI
            weightPicker.setText(android.text.format.DateFormat.format("MM-dd-yyyy", weightData.getDate()));
            weightInput.setText(String.valueOf(weightData.getWeight()));

            addOrUpdate.setOnClickListener(this::updateWeight);

            // update text fields
            title.setText(R.string.update_recorded_weight);
            addOrUpdate.setText(R.string.update);
        }
        else {
            selectedDate = null;
            addOrUpdate.setOnClickListener(this::addWeight);

            // update text fields
            title.setText(R.string.add_new_weight);
            addOrUpdate.setText(R.string.add);
        }

        // set button listeners
        cancelButton.setOnClickListener(view -> goBack());
        weightPicker.setOnClickListener(view -> showDatePicker());
    }

    private void initializeViews() {
        weightPicker = findViewById(R.id.datePicker);
        cancelButton = findViewById(R.id.cancelButton);
        addOrUpdate = findViewById(R.id.addOrUpdateWeightButton);
        weightInput = findViewById(R.id.weightInput);
        title = findViewById(R.id.addOrUpdateTitle);
    }

    private void addWeight(View view) {
        String weight = weightInput.getText().toString();
        if(weight.isEmpty() || selectedDate == null){
            return;
        }
        userWeight = Double.parseDouble(weight);
        recordedWeightsDB.addWeight(selectedDate, userWeight);
        goBack();
    }

    private void updateWeight(View view) {
        String weight = weightInput.getText().toString();
        if(weight.isEmpty() || selectedDate == null){
            return;
        }
        userWeight = Double.parseDouble(weight);
        recordedWeightsDB.update(id, userWeight ,selectedDate);
        goBack();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get((Calendar.MONTH));
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year1, month1, dayOfMonth) -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year1, month1, dayOfMonth);
                    selectedDate = selectedCalendar.getTime();

                    weightPicker.setText(android.text.format.DateFormat.format("MM-dd-yyyy", selectedDate));
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
