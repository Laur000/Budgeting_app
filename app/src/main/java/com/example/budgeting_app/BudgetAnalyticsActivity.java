package com.example.budgeting_app;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BudgetAnalyticsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private FirebaseFirestore budgetRef ;
    private Toolbar settingsToolbar;

    private TextView totalBudgetAmountTextView, analitycsInvestmentsAmount, analitycsMainSpendingsAmount;
    private TextView analitycsSavingsAmount, analitycsEntertainmentAmount, analitycsEducationAmount;

    private RelativeLayout relativeLayoutMainSpendings, relativeLayoutInvestments, relativeLayoutSavings;
    private RelativeLayout relativeLayoutEntertainment, relativeLayoutEducation;

    private ImageView mainSpendings_status, investments_status, savings_status;
    private ImageView entertainment_status, education_status;

    private AnyChartView anyChartViewBudget;
    private ProgressDialog loader;

    //date picker
    private DatePicker datePicker;
    private Button button2Calendar, buttonCloseCalendar;
    String dateSaved;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_analytics);

        budgetRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        //totals
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        analitycsMainSpendingsAmount= findViewById(R.id.analitycsMainSpendingsAmount);
        analitycsInvestmentsAmount = findViewById(R.id.analitycsInvestmentsAmount);
        analitycsSavingsAmount = findViewById(R.id.analitycsSavingsAmount);
        analitycsEntertainmentAmount = findViewById(R.id.analitycsEntertainmentAmount);
        analitycsEducationAmount = findViewById(R.id.analitycsEducationAmount);


        //relative layout
        relativeLayoutMainSpendings= findViewById(R.id.relativeLayoutMainSpendings);
        relativeLayoutInvestments = findViewById(R.id.relativeLayoutInvestments);
        relativeLayoutSavings = findViewById(R.id.relativeLayoutSavings);
        relativeLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        relativeLayoutEducation = findViewById(R.id.relativeLayoutEducation);

        //status
        mainSpendings_status =findViewById(R.id.mainSpendings_status);
        investments_status =findViewById(R.id.investments_status);
        savings_status =findViewById(R.id.savings_status);
        entertainment_status =findViewById(R.id.entertainment_status);
        education_status =findViewById(R.id.education_status);

        relativeLayoutMainSpendings.setVisibility(View.GONE);
        relativeLayoutInvestments.setVisibility(View.GONE);
        relativeLayoutSavings.setVisibility(View.GONE);
        relativeLayoutEntertainment.setVisibility(View.GONE);
        relativeLayoutEducation.setVisibility(View.GONE);

        //pie chart
        anyChartViewBudget = findViewById(R.id.anyChartViewBudget);

        fab =findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BudgetAnalyticsActivity.this , CalendarActivity2.class);
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
                        float totalMainSpendings=0 ;


                        float totalInvestments=0 ;
                        float totalSavings=0 ;
                        float totalEntertainment=0 ;
                        float totalEducation=0 ;

                        Pie pie = AnyChart.pie();
                        List<DataEntry> data = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //total main spendings
                                totalMainSpendings = totalMainSpendings + Float.valueOf(document.get("Transport").toString()) + Float.valueOf(document.get("Food").toString())+
                                  Float.valueOf(document.get("Eating out").toString()) + Float.valueOf(document.get("Health").toString())+
                                         Float.valueOf(document.get("Gifts").toString()) +
                                        Float.valueOf(document.get("Sports").toString()) + Float.valueOf(document.get("Car").toString()) + Float.valueOf(document.get("Clothes").toString()) +
                                        Float.valueOf(document.get("Bills").toString()) + Float.valueOf(document.get("House").toString()) + Float.valueOf(document.get("Pets").toString());


                                //total investments
                                totalInvestments = totalInvestments + Float.valueOf(document.get("Investments").toString());


                                //total Savings
                                totalSavings = totalSavings + Float.valueOf(document.get("Savings").toString());


                                //total Entertainment
                                totalEntertainment = totalEntertainment + Float.valueOf(document.get("Entertainment").toString());


                                //total Education
                               totalEducation = totalEducation + Float.valueOf(document.get("Education").toString());


                            }



                            float finalTotalEntertainment = totalEntertainment;
                            //------------------conexiune budget---------------------
                            float finalTotalMainSpendings = totalMainSpendings;
                            float finalTotalInvestments = totalInvestments;
                            float finalTotalSavings = totalSavings;
                            float finalTotalEducation = totalEducation;

                            data.add(new ValueDataEntry("Entertainment" , Float.valueOf(finalTotalEntertainment)));
                            data.add(new ValueDataEntry("Main spendings" , Float.valueOf(finalTotalMainSpendings)));
                            data.add(new ValueDataEntry("Investments" , Float.valueOf(finalTotalInvestments)));
                            data.add(new ValueDataEntry("Savings" , Float.valueOf(finalTotalSavings)));
                            data.add(new ValueDataEntry("Education" , Float.valueOf(finalTotalEducation)));
                            anyChartViewBudget = findViewById(R.id.anyChartViewBudget);
                            pie.data(data);
                            anyChartViewBudget.setChart(pie);


                            budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets")
                                    .document("Budget").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()){

                                            if(finalTotalMainSpendings != 0){
                                                relativeLayoutMainSpendings.setVisibility(View.VISIBLE);
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalMainSpendings) + " / " + document.get("CheltuieliPrincipale").toString());
                                                analitycsMainSpendingsAmount.setText(aux2);

                                                float percentage = (finalTotalMainSpendings *100) / Float.valueOf(document.get("CheltuieliPrincipale").toString());


                                                if(percentage <= 50 ){
                                                    mainSpendings_status.setImageResource(R.drawable.green);
                                                }
                                                else if(percentage > 50 && percentage < 99  ){
                                                    mainSpendings_status.setImageResource(R.drawable.yellow);
                                                }
                                                else if(percentage >= 100  ){
                                                    mainSpendings_status.setImageResource(R.drawable.red);
                                                }

                                            }
                                            if(finalTotalInvestments != 0){
                                                relativeLayoutInvestments.setVisibility(View.VISIBLE);
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalInvestments) + " / " + document.get("Investitii").toString());
                                                analitycsInvestmentsAmount.setText(aux2);

                                                float percentage = (finalTotalInvestments *100) / Float.valueOf(document.get("Investitii").toString());
                                                if(percentage <= 50 ){
                                                    investments_status.setImageResource(R.drawable.green);
                                                }
                                                else if(percentage > 50 && percentage < 99  ){
                                                    investments_status.setImageResource(R.drawable.yellow);
                                                }
                                                else if(percentage >= 100  ){
                                                    investments_status.setImageResource(R.drawable.red);
                                                }

                                            }
                                            if(finalTotalSavings != 0){
                                                relativeLayoutSavings.setVisibility(View.VISIBLE);
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalSavings) + " / " + document.get("Economii").toString());
                                                analitycsSavingsAmount.setText(aux2);
                                                float percentage = (finalTotalSavings *100) / Float.valueOf(document.get("Economii").toString());
                                                if(percentage <= 50 ){
                                                    savings_status.setImageResource(R.drawable.green);
                                                }
                                                else if(percentage > 50 && percentage < 99  ){
                                                    savings_status.setImageResource(R.drawable.yellow);
                                                }
                                                else if(percentage >= 100  ){
                                                    savings_status.setImageResource(R.drawable.red);
                                                }

                                            }

                                            if(finalTotalEntertainment != 0){
                                                relativeLayoutEntertainment.setVisibility(View.VISIBLE);
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalEntertainment) + " / " + document.get("Distractii").toString());
                                                analitycsEntertainmentAmount.setText(aux2);

                                                float percentage = (finalTotalEntertainment *100) / Float.valueOf(document.get("Distractii").toString());
                                                if(percentage <= 50 ){
                                                    entertainment_status.setImageResource(R.drawable.green);
                                                }
                                                else if(percentage > 50 && percentage < 99  ){
                                                    entertainment_status.setImageResource(R.drawable.yellow);
                                                }
                                                else if(percentage >= 100  ){
                                                    entertainment_status.setImageResource(R.drawable.red);
                                                }
                                            }
                                            if(finalTotalEducation != 0){
                                                relativeLayoutEducation.setVisibility(View.VISIBLE);
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalEducation) + " / " + document.get("Educatie").toString());
                                                analitycsEducationAmount.setText(aux2);

                                                float percentage = (finalTotalEducation *100) / Float.valueOf(document.get("Educatie").toString());
                                                if(percentage <= 50 ){
                                                    education_status.setImageResource(R.drawable.green);
                                                }
                                                else if(percentage > 50 && percentage < 99  ){
                                                    education_status.setImageResource(R.drawable.yellow);
                                                }
                                                else if(percentage >= 100  ){
                                                    education_status.setImageResource(R.drawable.red);
                                                }
                                            }

                                            float totalTot = finalTotalMainSpendings + finalTotalInvestments + finalTotalSavings +finalTotalEntertainment +finalTotalEducation;
                                            String aux = String.valueOf( "Total: "+ String.format("%.2f",totalTot) + " / " + document.get("VenitTotal").toString());

                                            totalBudgetAmountTextView.setText(aux);


                                        } else{
                                            //pop-up error "momentan nu aveti cheltuieli"
                                            Log.d("EROARE", "No such document");
                                        }
                                    } else {
                                        Log.d("EROARE", "get failed with ", task.getException());
                                    }
                                }
                            });



                        }
                        else {
                            Log.d("EROARE", "get failed with ", task.getException());
                        }


                        loader.dismiss();
                    }

                });

    }

}