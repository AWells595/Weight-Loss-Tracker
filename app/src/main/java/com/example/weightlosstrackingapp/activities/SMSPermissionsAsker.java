package com.example.weightlosstrackingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.weightlosstrackingapp.R;

public class SMSPermissionsAsker extends Activity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 51995;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions); // Replace with the actual layout file name
        requestSmsPermissions();
    }


    private void requestSmsPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            Toast.makeText(this, "SMS Permission already granted", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "SMS Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "SMS Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}