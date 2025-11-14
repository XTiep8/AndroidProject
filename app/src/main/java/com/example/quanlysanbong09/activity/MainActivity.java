package com.example.quanlysanbong09.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlysanbong09.R;

public class MainActivity extends AppCompatActivity {
    private Button fieldManagementButton, serviceManagementButton, bookingManagementButton,
            statisticsButton, helpButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fieldManagementButton = findViewById(R.id.button_field_management);
        serviceManagementButton = findViewById(R.id.button_service_management);
        bookingManagementButton = findViewById(R.id.button_booking_management);
        statisticsButton = findViewById(R.id.button_statistics);
        helpButton = findViewById(R.id.button_help);
        logoutButton = findViewById(R.id.button_logout);

        fieldManagementButton.setOnClickListener(v -> startActivity(new Intent(this, FieldManagementActivity.class)));
        serviceManagementButton.setOnClickListener(v -> startActivity(new Intent(this, ServiceManagementActivity.class)));
        bookingManagementButton.setOnClickListener(v -> startActivity(new Intent(this, BookingManagementActivity.class)));
        statisticsButton.setOnClickListener(v -> startActivity(new Intent(this, StatisticsActivity.class)));
        helpButton.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        logoutButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}