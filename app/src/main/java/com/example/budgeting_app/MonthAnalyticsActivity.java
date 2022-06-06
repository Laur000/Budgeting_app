package com.example.budgeting_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class MonthAnalyticsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private FirebaseFirestore budgetRef ;
    private Toolbar settingsToolbar;

    private TextView totalBudgetAmountTextView, analyticsTransportAmount, analitycsFoodAmount, analitycsEatingoutAmount;
    private TextView analitycsHealthAmount, analitycsGiftsAmount,analitycsSportsAmount,analitycsCarAmount;
    private TextView analitycsPetsAmount, analitycsClothesAmount, analitycsBillsAmount, analitycsInvestmentsAmount;
    private TextView analitycsSavingsAmount, analitycsEntertainmentAmount, analitycsEducationAmount, analitycsHouseAmount;
    private TextView  monthSpentAmount;

    private TextView monthTransport,monthFood, monthEatingOut, monthHealth, monthGifts, monthSports, monthCar, monthPets;
    private TextView monthClothes, monthBills, monthInvestments, monthSavings, monthEntertainment, monthEducation, monthHouse;



    private RelativeLayout relativeLayoutTransport, relativeLayoutFood, relativeLayoutEatingout, relativeLayoutHealth;
    private RelativeLayout relativeLayoutGifts, relativeLayoutSports, relativeLayoutCar, relativeLayoutPets;
    private RelativeLayout relativeLayoutClothes, relativeLayoutBills, relativeLayoutInvestments, relativeLayoutSavings;
    private RelativeLayout relativeLayoutEntertainment, relativeLayoutEducation, relativeLayoutHouse;

    private AnyChartView anyChartView;
    private ProgressDialog loader;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_analytics);

        budgetRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);


        //month
        monthTransport = findViewById(R.id.monthTransport);
        monthFood = findViewById(R.id.monthFood);
        monthEatingOut = findViewById(R.id.monthEatingOut);
        monthHealth = findViewById(R.id.monthHealth);
        monthGifts = findViewById(R.id.monthGifts);
        monthSports = findViewById(R.id.monthSports);
        monthCar = findViewById(R.id.monthCar);
        monthPets = findViewById(R.id.monthPets);
        monthClothes = findViewById(R.id.monthClothes);
        monthBills = findViewById(R.id.monthBills);
        monthInvestments = findViewById(R.id.monthInvestments);
        monthSavings = findViewById(R.id.monthSavings);
        monthEntertainment = findViewById(R.id.monthEntertainment);
        monthEducation = findViewById(R.id.monthEducation);
        monthHouse = findViewById(R.id.monthHouse);


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
        fab =findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MonthAnalyticsActivity.this , CalendarActivity.class);
                startActivity(intent);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        String dateSaved = getIntent().getStringExtra("date");



        loader.setMessage("Getting data");
        loader.setCanceledOnTouchOutside(false);
        loader.show();


        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale")
                .whereEqualTo("Date", dateSaved).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                float totalTransport=0 ;
                float totalFood=0 ;
                float totalEatingOut=0 ;
                float totalHealth=0 ;
                float totalGifts=0 ;
                float totalSports=0 ;
                float totalCar=0 ;
                float totalClothes=0 ;
                float totalBills=0 ;
                float totalHouse=0 ;
                float totalPets =0 ;
                float totalInvestments=0 ;
                float totalSavings=0 ;
                float totalEntertainment=0 ;
                float totalEducation=0 ;



                Pie pie2 = AnyChart.pie();
                List<DataEntry> data2 = new ArrayList<>();

                if (task.isSuccessful() &&  !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //total transport
                        totalTransport = totalTransport + Float.valueOf(document.get("Transport").toString());
                        //total food
                        totalFood= totalFood +  Float.valueOf(document.get("Food").toString());

                        totalEatingOut = totalEatingOut + Float.valueOf(document.get("Eating out").toString());

                        totalHealth= totalHealth+ Float.valueOf(document.get("Health").toString());

                        totalGifts= totalGifts +  Float.valueOf(document.get("Gifts").toString()) ;
                        totalSports= totalSports +  Float.valueOf(document.get("Sports").toString()) ;
                        totalCar = totalCar+ Float.valueOf(document.get("Car").toString()) ;
                        totalClothes = totalClothes + Float.valueOf(document.get("Clothes").toString()) ;

                        totalBills = totalBills +  Float.valueOf(document.get("Bills").toString()) ;
                        totalHouse = totalHouse + Float.valueOf(document.get("House").toString());
                        totalPets = totalPets + Float.valueOf(document.get("Pets").toString());

                        //total investments
                        totalInvestments = totalInvestments + Float.valueOf(document.get("Investments").toString());


                        //total Savings
                        totalSavings = totalSavings + Float.valueOf(document.get("Savings").toString());


                        //total Entertainment
                        totalEntertainment = totalEntertainment + Float.valueOf(document.get("Entertainment").toString());


                        //total Education
                        totalEducation = totalEducation + Float.valueOf(document.get("Education").toString());


                    }
                    if (totalFood != 0) {

                            relativeLayoutFood.setVisibility(View.VISIBLE);
                            String aux2 = String.valueOf(String.format("%.2f",totalFood));
                            analitycsFoodAmount.setText(aux2);
                            data2.add(new ValueDataEntry("Food" , Float.valueOf(aux2)));
                            monthFood.setText(dateSaved);

                        }
                    if (totalTransport != 0) {

                        relativeLayoutTransport.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalTransport));
                        analyticsTransportAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Transport" , Float.valueOf(aux2)));
                        monthTransport.setText(dateSaved);
                    }

                    if (totalEatingOut != 0) {

                        relativeLayoutEatingout.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalEatingOut));
                        analitycsEatingoutAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Eating out" , Float.valueOf(aux2)));
                        monthEatingOut.setText(dateSaved);
                    }
                    if (totalHealth != 0) {

                        relativeLayoutHealth.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalHealth));
                        analitycsHealthAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Health" , Float.valueOf(aux2)));
                        monthHealth.setText(dateSaved);
                    }

                    if (totalGifts != 0) {

                        relativeLayoutGifts.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalGifts));
                        analitycsGiftsAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Gifts" , Float.valueOf(aux2)));
                        monthGifts.setText(dateSaved);
                    }

                    if (totalSports != 0) {

                        relativeLayoutSports.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalSports));
                        analitycsSportsAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Sports" , Float.valueOf(aux2)));
                        monthSports.setText(dateSaved);
                    }
                    if (totalCar != 0) {

                        relativeLayoutCar.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalCar));
                        analitycsCarAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Car" , Float.valueOf(aux2)));
                        monthCar.setText(dateSaved);
                    }

                    if (totalClothes != 0) {

                        relativeLayoutClothes.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalClothes));
                        analitycsClothesAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Clothes" , Float.valueOf(aux2)));
                        monthClothes.setText(dateSaved);
                    }
                    if (totalBills != 0) {

                        relativeLayoutBills.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalBills));
                        analitycsBillsAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Bills" , Float.valueOf(aux2)));
                        monthBills.setText(dateSaved);
                    }
                    if (totalInvestments != 0) {

                        relativeLayoutInvestments.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalInvestments));
                        analitycsInvestmentsAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Investments" , Float.valueOf(aux2)));
                        monthInvestments.setText(dateSaved);
                    }
                    if (totalSavings != 0) {

                        relativeLayoutSavings.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalSavings));
                        analitycsSavingsAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Savings" , Float.valueOf(aux2)));
                        monthSavings.setText(dateSaved);
                    }
                    if (totalEntertainment != 0) {

                        relativeLayoutEntertainment.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalEntertainment));
                        analitycsEntertainmentAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Entertainment" , Float.valueOf(aux2)));
                        monthEntertainment.setText(dateSaved);
                    }
                    if (totalEducation != 0) {

                        relativeLayoutEducation.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalEducation));
                        analitycsEducationAmount.setText(aux2);
                        data2.add(new ValueDataEntry("Education" , Float.valueOf(aux2)));
                        monthEducation.setText(dateSaved);
                    }

                    if (totalHouse != 0) {

                        relativeLayoutHouse.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalHouse));
                        analitycsHouseAmount.setText(aux2);
                        data2.add(new ValueDataEntry("House" , Float.valueOf(aux2)));
                        monthHouse.setText(dateSaved);
                    }
                    if (totalPets != 0) {

                        relativeLayoutPets.setVisibility(View.VISIBLE);
                        String aux2 = String.valueOf(String.format("%.2f",totalPets));
                        analitycsPetsAmount.setText(aux2);
                        data2.add(new ValueDataEntry("House" , Float.valueOf(aux2)));
                        monthPets.setText(dateSaved);
                    }

                    pie2.data(data2);
                    anyChartView.refreshDrawableState();
                    anyChartView.setVisibility(View.VISIBLE);
                    anyChartView.setChart(pie2);

                    //total
                    budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets")
                            .document(dateSaved).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {

                                DocumentSnapshot document = task.getResult();
                                float aux = Float.valueOf(document.get("total").toString());
                                String aux2 = String.valueOf(String.format("%.2f",aux));

                                String totalText = String.valueOf("Total spend: " + aux2);

                                totalBudgetAmountTextView.setText(totalText);

                            }
                        }
                    });


                }
                else {
                    Toast.makeText(MonthAnalyticsActivity.this, "0 DATA", Toast.LENGTH_SHORT).show();
                }


                loader.dismiss();
            }

        });



    }

}