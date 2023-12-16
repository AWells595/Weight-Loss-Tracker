package com.example.weightlosstrackingapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.weightlosstrackingapp.R;
import com.example.weightlosstrackingapp.WeightData;
import com.example.weightlosstrackingapp.databases.GoalWeight;
import com.example.weightlosstrackingapp.databases.RecordedWeights;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private GoalWeight goalWeightDB;
    private RecordedWeights recordedWeightsDB;
    private LinearLayout internalLinearLayout;
    private List<WeightData> weights;
    private Button deleteSelectedRow;
    private Button editGoalWeight;
    private Button addGoalWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setListeners();
        initializeDatabases();
    }

    private void initializeDatabases() {
        goalWeightDB = new GoalWeight(getApplicationContext());
        recordedWeightsDB = new RecordedWeights(getApplicationContext());
    }

    private void initializeViews() {
        internalLinearLayout = findViewById(R.id.internalLinearLayout);
        deleteSelectedRow = findViewById(R.id.deleteSelected);
        editGoalWeight = findViewById(R.id.editGoalWeight);
        addGoalWeight = findViewById(R.id.recordNewWeight);
    }

    private void setListeners() {
        deleteSelectedRow.setOnClickListener(view -> deleteSelectedRow());
        editGoalWeight.setOnClickListener(view -> editGoalWeight());
        addGoalWeight.setOnClickListener(this::recordNewWeight);
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadGrid();
        handleGoalWeightCheck();
    }

    private void loadGrid() {
        weights = recordedWeightsDB.retrieveWeights();
        sortByDate(weights, 0, weights.size() - 1);
        createGrid(weights);
    }

    private void handleGoalWeightCheck() {
        if(isGoalWeightReached(weights.get(0).getWeight(), goalWeightDB.retrieveGoalWeight())){
            sendNotification();
        }
    }

    /**
     * Implements a quick sort to sort the list of weights in descending order by date aka newest
     * weight first
     * @param weights list of WeightData to sort
     * @param low starting index of low partition
     * @param high ending index of high partition
     */
    private void sortByDate(List<WeightData> weights, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(weights, low, high);
            sortByDate(weights, low, partitionIndex - 1);
            sortByDate(weights, partitionIndex + 1, high);
        }
    }

    private int partition(List<WeightData> weights, int low, int high) {
        Date pivot = weights.get(high).getDate();
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (weights.get(j).getDate().compareTo(pivot) >= 0) {
                i++;

                Collections.swap(weights, i, j);
            }
        }

        Collections.swap(weights, i + 1, high);

        return i + 1;
    }

    private void createGrid(List<WeightData> weights) {
        int size = weights.size();
        int rows = size / 3; // Number of full rows
        int remainder = size % 3; // Number of weights in the last row

        for (int i = 0; i < rows; i++) {
            createRow(new WeightData[]{weights.get(i * 3), weights.get(i * 3 + 1), weights.get(i * 3 + 2)});
        }

        if (remainder > 0) {
            WeightData[] lastRowWeights = new WeightData[3];
            for (int i = 0; i < remainder; i++) {
                lastRowWeights[i] = weights.get(rows * 3 + i);
            }

            createRow(lastRowWeights);
        }
    }

    /**
     * Creates a row in the ScrollView, each row is 3 buttons with weight data
     * and a check box that is defaulted to unchecked.
     * <p>
     * Rows that don't have 3 full rows of weight data still have 3 buttons each just the extra b
     * buttons are set to invisible
     * @param threeWeights 3 length array containing WeightData
     */
    private void createRow(WeightData[] threeWeights) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox checkBox = createCheckBox();

        for (WeightData weightData : threeWeights) {
            Button button = createButton();

            // create invisible buttons for spacing if not 3 weights in row
            if (weightData == null) {
                button.setVisibility(View.INVISIBLE);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
                String formattedDate = dateFormat.format(weightData.getDate());
                int currentWeight = weightData.getWeight();
                String formattedWeight = "" + currentWeight + " lbs";
                String message = formattedDate + "\n" + formattedWeight;
                button.setText(message);
                button.setTag(weightData.getId());


                button.setOnClickListener(this::updateWeight);
            }

            rowLayout.addView(button);
        }

        rowLayout.addView(checkBox);
        internalLinearLayout.addView(rowLayout);
    }

    private CheckBox createCheckBox() {
        CheckBox checkBox = new CheckBox(this);

        checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        checkBox.setChecked(false);
        checkBox.setGravity(Gravity.CENTER_HORIZONTAL);

        return checkBox;
    }

    private Button createButton() {
        Button button = new Button(this);

        button.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));
        button.setGravity(Gravity.CENTER);

        return button;
    }

    private void deleteSelectedRow() {
        int rowCount = internalLinearLayout.getChildCount();

        for (int i = 0; i < rowCount; i++) {
            View rowView = internalLinearLayout.getChildAt(i);

            if (rowView instanceof LinearLayout) {
                LinearLayout rowLayout = (LinearLayout) rowView;

                // Last child of the row is the CheckBox
                CheckBox checkBox = (CheckBox) rowLayout.getChildAt(rowLayout.getChildCount() - 1);

                if (checkBox.isChecked()) {
                    List<Button> buttonsInRow = getButtonsInRow(rowLayout);
                    for (Button button : buttonsInRow) {
                        if(button.getVisibility() == View.VISIBLE) {
                            long id = (long) button.getTag();
                            recordedWeightsDB.delete(id);
                        }
                    }
                    // Remove the row
                    internalLinearLayout.removeViewAt(i);
                    rowCount--;
                    i--; // need to adjust i so can't go out of bounds

                }
            }
        }
    }

    private List<Button> getButtonsInRow(LinearLayout rowLayout) {
        List<Button> buttonsInRow = new ArrayList<>();

        int buttonCount = rowLayout.getChildCount() - 1; // Exclude the CheckBox
        for (int i = 0; i < buttonCount; i++) {
            View view = rowLayout.getChildAt(i);
            if (view instanceof Button) {
                buttonsInRow.add((Button) view);
            }
        }

        return buttonsInRow;
    }

    private void editGoalWeight(){
        Intent intent = new Intent(this, EnterGoalWeightActivity.class);
        Bundle bundle = new Bundle();

        bundle.putBoolean("update", true);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void updateWeight(View view) {
        Intent intent = new Intent(this, AddOrUpdateWeight.class);
        Bundle bundle = new Bundle();
        long id = (long) view.getTag();
        bundle.putLong("id", id);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void recordNewWeight(View view) {
        Intent intent = new Intent(this, AddOrUpdateWeight.class);
        startActivity(intent);
    }

    private boolean isGoalWeightReached(int weight, int goalWeight) {
        return weight <= goalWeight;
    }

    private void sendNotification() {
        String message = "Goal Weight Achieved!";
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            String phoneNumber = "6031234567";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }
}