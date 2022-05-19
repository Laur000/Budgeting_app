package com.example.budgeting_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private CardView analyticsCardView;
    private CardView budgetCardView;
    private CardView predictionCardView;
    private CardView setBudgetCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budgetCardView = findViewById(R.id.budgetCardView);
        analyticsCardView = findViewById(R.id.analyticsCardView);
        predictionCardView = findViewById(R.id.todayCardView);
        setBudgetCardView = findViewById(R.id.setBudgetCardView);

        //redirect for budgetView
        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , BudgetActivity.class);
                startActivity(intent);
            }
        });

        //redirect for analytics
        analyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , ChooseAnalyticActivity.class);
                startActivity(intent);
            }
        });

        //prediction card view
        predictionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , PredictionActivity.class);
                startActivity(intent);
            }
        });

        //redirect for setBudgetCardView
        setBudgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this , setBudgetActivity.class);
                startActivity(intent);
            }
        });



    }
}