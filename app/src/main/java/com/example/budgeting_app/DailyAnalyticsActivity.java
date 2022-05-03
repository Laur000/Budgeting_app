package com.example.budgeting_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private String onlineUserId = "";

    private Toolbar settingsToolbar;
    private TextView totalBudgetAmountTextView, analyticsTransportAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);

        settingsToolbar = findViewById(R.id.settingsToolbar);
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
      /*  totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        */

    }
}