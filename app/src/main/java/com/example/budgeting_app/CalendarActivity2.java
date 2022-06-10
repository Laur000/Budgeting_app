package com.example.budgeting_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarActivity2 extends AppCompatActivity {
    private DatePicker datePicker;
    private Button button2Calendar, buttonCloseCalendar;
    private FloatingActionButton fab;
    String dateSaved;
    private FirebaseAuth mAuth;
    private FirebaseFirestore budgetRef ;
    private ProgressDialog loader;
    ArrayList<PredictionData> investments_data = new ArrayList<PredictionData>();
    ArrayList<PredictionData> savings_data = new ArrayList<PredictionData>();
    ArrayList<PredictionData> entertainment_data = new ArrayList<PredictionData>();
    ArrayList<PredictionData> education_data = new ArrayList<PredictionData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
        budgetRef = FirebaseFirestore.getInstance();
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

                float investmentsData[] = new float[investments_data.size()];
                float savingsData[] = new float[savings_data.size()];
                float entertainmentData[] = new float[entertainment_data.size()];
                float educationData[] = new float[education_data.size()];
                int i1=0;

                if(!investments_data.isEmpty()) {
                    for (PredictionData d : investments_data) {
                        investmentsData[i1] = Float.valueOf(String.valueOf(d.total));
                        i1++;
                    }
                    i1=0;
                }
                if(!savings_data.isEmpty()) {
                    for (PredictionData d : savings_data) {
                        savingsData[i1] = Float.valueOf(String.valueOf(d.total));
                        i1++;
                    }
                    i1=0;
                }
                if(!entertainment_data.isEmpty()) {
                    for (PredictionData d : entertainment_data) {
                        entertainmentData[i1] = Float.valueOf(String.valueOf(d.total));
                        i1++;
                    }
                    i1=0;
                }
                if(!education_data.isEmpty()) {
                    for (PredictionData d : education_data) {
                        educationData[i1] = Float.valueOf(String.valueOf(d.total));
                        i1++;
                    }
                    i1=0;
                }
                Intent intent = new Intent(CalendarActivity2.this , BudgetAnalyticsActivity.class);
                intent.putExtra("date", dateSaved);
                intent.putExtra("investments_data", investmentsData);
                intent.putExtra("savings_data", savingsData);
                intent.putExtra("entertainment_data", entertainmentData);
                intent.putExtra("education_data", educationData);
                startActivity(intent);
            }
        });

        buttonCloseCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalendarActivity2.this , MainActivity.class);
                startActivity(intent);
            }
        });


    }

    protected void onStart(){
            super.onStart();

        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                        investments_data.clear();
                        savings_data.clear();
                        entertainment_data.clear();
                        education_data.clear();

                        double total = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            for (int i = 100; i >= 0; i--) {
                                Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.DATE, -i);
                                String data_past_f1 = dateFormat1.format(cal.getTime());

                                if (document.getId().toString().equals(data_past_f1)) {
                                    double totalAmount1 = Double.parseDouble(document.get("Investments").toString());
                                    double totalAmount2 = Double.parseDouble(document.get("Savings").toString());
                                    double totalAmount3 = Double.parseDouble(document.get("Entertainment").toString());
                                    double totalAmount4 = Double.parseDouble(document.get("Education").toString());


                                    if(totalAmount1!= 0 ) {
                                        investments_data.add(new PredictionData(data_past_f1, totalAmount1));
                                    }
                                    if(totalAmount2!= 0 ) {
                                        savings_data.add(new PredictionData(data_past_f1, totalAmount2));
                                    }
                                    if(totalAmount3!= 0 ) {
                                        entertainment_data.add(new PredictionData(data_past_f1, totalAmount3));
                                    }
                                    if(totalAmount4!= 0 ) {
                                        education_data.add(new PredictionData(data_past_f1, totalAmount4));
                                    }

                                }
                            }


                        }



                    }
                });


    }


}