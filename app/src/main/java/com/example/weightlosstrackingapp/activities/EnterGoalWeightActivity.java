package com.example.weightlosstrackingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.weightlosstrackingapp.R;
import com.example.weightlosstrackingapp.databases.GoalWeight;

public class EnterGoalWeightActivity extends Activity {
    private Button goalWeightButton;
    private TextView goalWeightInput;
    private GoalWeight goalWeightDatabase;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_goal_weight);

        initializeViews();

        // external data
        goalWeightDatabase = new GoalWeight(getApplicationContext());
        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        boolean update = bundle.getBoolean("update");
        if(update) {
            int weight = goalWeightDatabase.retrieveGoalWeight();
            goalWeightInput.setText(String.valueOf(weight));
            goalWeightButton.setOnClickListener(view -> updateWeight());
            title.setText(R.string.update_goal_weight);
        }
        else{
            goalWeightButton.setOnClickListener(this::addWeight);
            title.setText(R.string.enter_goal_weight);
        }
    }

    private void initializeViews() {
        goalWeightButton = findViewById(R.id.goalWeightInputButton);
        goalWeightInput = findViewById(R.id.goalWeightInput);
        title = findViewById(R.id.enterGoalTitle);
    }

    private void addWeight(View view) {
        String goal = goalWeightInput.getText().toString();
        if(goal.isEmpty()) {
            return;
        }

        int goalWeight = Integer.parseInt(goal);
        goalWeightDatabase.setGoalWeight(goalWeight);
        Intent intent = new Intent(this, SMSPermissionsAsker.class);
        startActivity(intent);
    }

    private void updateWeight() {
        String goal = goalWeightInput.getText().toString();
        if(goal.isEmpty()) {
            return;
        }

        int goalWeight = Integer.parseInt(goal);
        goalWeightDatabase.updateGoalWeight(goalWeight);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
