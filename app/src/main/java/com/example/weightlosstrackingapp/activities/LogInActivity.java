package com.example.weightlosstrackingapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.example.weightlosstrackingapp.R;
import com.example.weightlosstrackingapp.databases.LogInDatabase;

public class LogInActivity extends Activity {
    private TextView userNameInput;
    private TextView passwordInput;
    private TextView errorMessage;

    LogInDatabase logInDatabase;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_in_screen);

        initializeViews();
        logInDatabase = new LogInDatabase(getApplicationContext());
    }

    private void initializeViews() {
        userNameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        errorMessage = findViewById(R.id.errorMessage);
    }

    public void onClickLogIn(View view) {
        String username = userNameInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(TextUtils.isEmpty(userNameInput.getText()) || TextUtils.isEmpty(passwordInput.getText())) {
            errorMessage.setText(R.string.log_in_empty_fields);
        }
        else{
            errorMessage.setText("");
            long doesUserNameExist = logInDatabase.getLogInByUsername(username);
            if(doesUserNameExist == -1) {
                errorMessage.setText(R.string.log_in_username_not_Found);
                return;
            }
            boolean logInSuccessful = logInDatabase.isLogInSuccessful(username,password);
            if(!logInSuccessful) {
                errorMessage.setText(R.string.log_in_password_incorrect);
            }
            else {
                needPermission();
            }

        }
    }

    public void needPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            Intent intent = new Intent(this, SMSPermissionsAsker.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onClickCreateNewAccount(View view) {
        String username = userNameInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(TextUtils.isEmpty(userNameInput.getText()) || TextUtils.isEmpty(passwordInput.getText())) {
            errorMessage.setText(R.string.log_in_empty_fields);
        }
        else{
            errorMessage.setText("");
            logInDatabase.addLogIn(username, password);
            boolean loggedIn = logInDatabase.isLogInSuccessful(username, password);
            if(loggedIn) {
                Intent goalWeight = new Intent(this, EnterGoalWeightActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("update", false);
                goalWeight.putExtras(bundle);
                startActivity(goalWeight);
            }
            else{
                errorMessage.setText(R.string.log_in_password_incorrect);
            }
        }
    }
}
