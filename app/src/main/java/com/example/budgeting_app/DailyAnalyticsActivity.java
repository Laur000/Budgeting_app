package com.example.budgeting_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.List;

public class DailyAnalyticsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private FirebaseFirestore budgetRef ;
    private Toolbar settingsToolbar;

      private TextView totalBudgetAmountTextView, analyticsTransportAmount, analitycsFoodAmount, analitycsEatingoutAmount;
      private TextView analitycsHealthAmount, analitycsGiftsAmount,analitycsSportsAmount,analitycsCarAmount;
      private TextView analitycsPetsAmount, analitycsClothesAmount, analitycsBillsAmount, analitycsInvestmentsAmount;
      private TextView analitycsSavingsAmount, analitycsEntertainmentAmount, analitycsEducationAmount, analitycsHouseAmount;
      private TextView  monthSpentAmount;


      private RelativeLayout relativeLayoutTransport, relativeLayoutFood, relativeLayoutEatingout, relativeLayoutHealth;
      private RelativeLayout relativeLayoutGifts, relativeLayoutSports, relativeLayoutCar, relativeLayoutPets;
      private RelativeLayout relativeLayoutClothes, relativeLayoutBills, relativeLayoutInvestments, relativeLayoutSavings;
      private RelativeLayout relativeLayoutEntertainment, relativeLayoutEducation, relativeLayoutHouse;


      private AnyChartView anyChartView;
      private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_analytics);


        budgetRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
        //amounts
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analitycsFoodAmount = findViewById(R.id.analitycsFoodAmount);
        analitycsEatingoutAmount = findViewById(R.id.analitycsEatingoutAmount);
        analitycsHealthAmount = findViewById(R.id.analitycsHealthAmount);
        analitycsGiftsAmount = findViewById(R.id.analitycsGiftsAmount);
        analitycsSportsAmount = findViewById(R.id.analitycsSportsAmount);
        analitycsCarAmount = findViewById(R.id.analitycsCarAmount);
        analitycsPetsAmount = findViewById(R.id.analitycsPetsAmount);
        analitycsClothesAmount = findViewById(R.id.analitycsClothesAmount);
        analitycsBillsAmount = findViewById(R.id.analitycsBillsAmount);
        analitycsInvestmentsAmount = findViewById(R.id.analitycsInvestmentsAmount);
        analitycsSavingsAmount = findViewById(R.id.analitycsSavingsAmount);
        analitycsEntertainmentAmount = findViewById(R.id.analitycsEntertainmentAmount);
        analitycsEducationAmount = findViewById(R.id.analitycsEducationAmount);
        analitycsHouseAmount = findViewById(R.id.analitycsHouseAmount);






        relativeLayoutTransport = findViewById(R.id.relativeLayoutTransport);
        relativeLayoutFood = findViewById(R.id.relativeLayoutFood);
        relativeLayoutEatingout = findViewById(R.id.relativeLayoutEatingout);
        relativeLayoutHealth = findViewById(R.id.relativeLayoutHealth);
        relativeLayoutGifts = findViewById(R.id.relativeLayoutGifts);
        relativeLayoutSports = findViewById(R.id.relativeLayoutSports);
        relativeLayoutCar = findViewById(R.id.relativeLayoutCar);
        relativeLayoutPets = findViewById(R.id.relativeLayoutPets);
        relativeLayoutClothes = findViewById(R.id.relativeLayoutClothes);
        relativeLayoutBills = findViewById(R.id.relativeLayoutBills);
        relativeLayoutInvestments = findViewById(R.id.relativeLayoutInvestments);
        relativeLayoutSavings = findViewById(R.id.relativeLayoutSavings);
        relativeLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        relativeLayoutEducation = findViewById(R.id.relativeLayoutEducation);
        relativeLayoutHouse = findViewById(R.id.relativeLayoutHouse);



        //VIEW GONE
        relativeLayoutTransport.setVisibility(View.GONE);
        relativeLayoutFood.setVisibility(View.GONE);
        relativeLayoutEatingout.setVisibility(View.GONE);
        relativeLayoutHealth.setVisibility(View.GONE);
        relativeLayoutGifts.setVisibility(View.GONE);
        relativeLayoutSports.setVisibility(View.GONE);
        relativeLayoutCar.setVisibility(View.GONE);
        relativeLayoutPets.setVisibility(View.GONE);
        relativeLayoutClothes.setVisibility(View.GONE);
        relativeLayoutBills.setVisibility(View.GONE);
        relativeLayoutInvestments.setVisibility(View.GONE);
        relativeLayoutSavings.setVisibility(View.GONE);
        relativeLayoutEntertainment.setVisibility(View.GONE);
        relativeLayoutEducation.setVisibility(View.GONE);
        relativeLayoutHouse.setVisibility(View.GONE);

        //pie chart


        anyChartView = findViewById(R.id.anyChartView);
    }

    @Override
    protected void onStart() {

        super.onStart();
        loader.setMessage("Getting data");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

        DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat1.format(cal.getTime());

        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale").document(date).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Pie pie = AnyChart.pie();
                            List<DataEntry> data = new ArrayList<>();

                            float total = 0;

                            if (document.exists()) {
                                if(!document.get("Food").toString().equals("0")){
                                        relativeLayoutFood.setVisibility(View.VISIBLE);
                                        float aux = Float.valueOf(document.get("Food").toString());
                                        String aux2 = String.valueOf(String.format("%.2f",aux));
                                        analitycsFoodAmount.setText(aux2);
                                        data.add(new ValueDataEntry("Food" , Float.valueOf(aux2)));
                                        total = total + Float.valueOf(aux2);
                                    }
                                if(!document.get("Transport").toString().equals("0")){
                                    relativeLayoutTransport.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Transport").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analyticsTransportAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Transport" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Eating out").toString().equals("0")){
                                    relativeLayoutEatingout.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Eating out").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsEatingoutAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Eating out" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Health").toString().equals("0")){
                                    relativeLayoutHealth.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Health").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsHealthAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Health" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Gifts").toString().equals("0")){
                                    relativeLayoutGifts.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Gifts").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsGiftsAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Gifts" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Sports").toString().equals("0")){
                                    relativeLayoutSports.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Sports").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsSportsAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Sports" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Car").toString().equals("0")){
                                    relativeLayoutCar.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Car").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsCarAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Car" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Pets").toString().equals("0")){
                                    relativeLayoutPets.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Pets").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsPetsAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Pets" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Clothes").toString().equals("0")){
                                    relativeLayoutClothes.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Clothes").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsClothesAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Clothes" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Bills").toString().equals("0")){
                                    relativeLayoutBills.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Bills").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsBillsAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Bills" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Investments").toString().equals("0")){
                                    relativeLayoutInvestments.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Investments").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsInvestmentsAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Investments" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Savings").toString().equals("0")){
                                    relativeLayoutSavings.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Savings").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsSavingsAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Savings" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Entertainment").toString().equals("0")){
                                    relativeLayoutEntertainment.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Entertainment").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsEntertainmentAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Entertainment" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("Education").toString().equals("0")){
                                    relativeLayoutEducation.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("Education").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsEducationAmount.setText(aux2);
                                    data.add(new ValueDataEntry("Education" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }
                                if(!document.get("House").toString().equals("0")){
                                    relativeLayoutHouse.setVisibility(View.VISIBLE);
                                    float aux = Float.valueOf(document.get("House").toString());
                                    String aux2 = String.valueOf(String.format("%.2f",aux));
                                    analitycsHouseAmount.setText(aux2);
                                    data.add(new ValueDataEntry("House" , Float.valueOf(aux2)));
                                    total = total + Float.valueOf(aux2);
                                }

                                pie.data(data);
                                anyChartView.setChart(pie);
                                anyChartView.setBackgroundColor(Color.rgb(230,230,250));
                                String totalText = String.valueOf("Total spend today: " + total);
                                totalBudgetAmountTextView.setText(totalText);


                            } else {
                                //pop-up error "momentan nu aveti cheltuieli"
                                Log.d("EROARE", "No such document");
                            }
                        } else {
                            Log.d("EROARE", "get failed with ", task.getException());
                        }

                        loader.dismiss();


                    }
                });

    }
}