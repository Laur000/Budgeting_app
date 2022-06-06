package com.example.budgeting_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CalendarActivity extends AppCompatActivity {
    //date picker
    private DatePicker datePicker;
    private Button button2Calendar, buttonCloseCalendar;
    private FloatingActionButton fab;
    String dateSaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);


        datePicker = findViewById(R.id.datePicker);
        button2Calendar =findViewById(R.id.button2Calendar);
        buttonCloseCalendar=findViewById(R.id.buttonCloseCalendar);

        button2Calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                month = month +1;
                if(month >0 && month <10 ){

                    dateSaved = "0"+String.valueOf(month) + "-" + String.valueOf(year);
                }else{
                    dateSaved = String.valueOf(month) + "-" + String.valueOf(year);
                }

                Intent intent = new Intent(CalendarActivity.this , MonthAnalyticsActivity.class);
                intent.putExtra("date", dateSaved);
                startActivity(intent);
            }
        });

        buttonCloseCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity.this , MainActivity.class);
                startActivity(intent);
            }
        });

    }
}