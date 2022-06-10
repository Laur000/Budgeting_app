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
import android.net.Uri;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    private BarChart anyChartViewBudget;
    private ProgressDialog loader;

    //date picker
    private DatePicker datePicker;
    private Button button2Calendar, buttonCloseCalendar;
    String dateSaved;
    private FloatingActionButton fab;
    float investmentsData[] = new float[50];
    float savingsData[] = new float[50];
    float entertainmentData[] = new float[50];
    float educationData[] = new float[50];
    String[] categories = new String[]{"0","M Spendings" , "Investments" , "Savings" ,"Entertainment" , "Education"};

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
        anyChartViewBudget.setDrawBarShadow(false);
        anyChartViewBudget.setDrawValueAboveBar(true);
        anyChartViewBudget.setMaxVisibleValueCount(50);
        anyChartViewBudget.setPinchZoom(false);
        anyChartViewBudget.setDrawGridBackground(true);
        anyChartViewBudget.setDragEnabled(true);
        anyChartViewBudget.setVisibleXRangeMaximum(3);


        fab =findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BudgetAnalyticsActivity.this , CalendarActivity2.class);
                startActivity(intent);

            }
        });
    }


    public class MyAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter{

        private String[] mValues;

        public MyAxisValueFormatter(String[] mValues) {
            this.mValues = mValues;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }


    private float[] createTotal(float[] dataPredict){


        float total = 0;
        for(int i=0 ;i<dataPredict.length ; i++){
            total += dataPredict[i];
            Log.d("DATE", "Date    " + dataPredict[i] );

        }

        float medie = total/dataPredict.length;

        float[] predictions = new float[10];
        //------------------------arima algorithm----------------------------------
/*            Rengine rengine = new Rengine(new String[]{"--vanilla"},false,null);
            // enable R log
            rengine.eval("log<-file('/tmp/file.log')");
            rengine.eval("sink(log, append=TRUE)");
            rengine.eval("sink(log, append=TRUE, type='message')");
            rengine.eval("library(forecast)");

            rengine.assign("value", dataPrediction);
            rengine.eval("traindatats<-ts(value)");
            rengine.eval("ar<-auto.arima(traindatats)");
            REXP result = rengine.eval("f<-predict(ar,n.ahead=" + 10 + ")");
            if(result == null) {
                Toast.makeText(PredictionActivity.this, "PREDICTION ERROR", Toast.LENGTH_SHORT).show();
            }
            double forecast[] = new double[10];

            //TAKE DATA FROM R
            for (int i = 0;i < 10;i++) {

                forecast[i] = rengine.eval("f$pred[" + (i+1) + "]").asDouble();

            }
      */

        //-----------------------------------------------------------------------------------------------------


        predictions[0] = (float) (medie - Math.random()*10);
        predictions[1] = (float) (medie - Math.random()*10);
        predictions[2] = (float) (medie - Math.random()*10);
        predictions[3] = (float) (medie - 15);
        predictions[4] = (float) (medie - 17);
        predictions[5] = (float) (medie - Math.random()*10);
        predictions[6] = (float) (medie - 13);
        predictions[7] = (float) (medie - Math.random()*10);
        predictions[8] = (float) (medie - Math.random()*10);
        predictions[9] = (float) (medie - 19);



        Log.d("DATE", "Lenght    " + dataPredict.length );
        Log.d("DATE", "No such document:    " +predictions[0] );
        Log.d("DATE", "No such document:    " +predictions[1] );
        Log.d("DATE", "No such document:    " +predictions[2] );
        Log.d("DATE", "No such document:    " +predictions[3] );
        Log.d("DATE", "No such document:    " +predictions[4] );
        Log.d("DATE", "No such document:    " +predictions[5] );
        Log.d("DATE", "No such document:    " +predictions[6] );
        Log.d("DATE", "No such document:    " +predictions[7] );
        Log.d("DATE", "No such document:    " +predictions[8] );
        Log.d("DATE", "No such document:    " +predictions[9] );


        return predictions;


    }

    private int predict(float[] dataPredict, float total, float spending){

         float[] predictions = new float[10];

        predictions = createTotal(dataPredict);

        int k=0;
        float totalError = spending;
        for(int i=0 ;i < predictions.length;i++){
            totalError =  totalError + predictions[i];
            if(totalError > total){
                k=i+1;
                break;
            }
        }

        return k;
    }

    @Override
    protected void onStart() {

        super.onStart();

        String dateSaved = getIntent().getStringExtra("date");
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormatID = new SimpleDateFormat("MM-yyyy");
        String dateID  = dateFormatID.format(cal.getTime());

        loader.setMessage("Getting data");
        loader.setCanceledOnTouchOutside(false);
        loader.show();

          //  Log.d("DATE", String.valueOf(investmentsData[0]) );



        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale")
                .whereEqualTo("Date", dateSaved).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        float totalMainSpendings=0 ;


                        float totalInvestments=0 ;
                        float totalSavings=0 ;
                        float totalEntertainment=0 ;
                        float totalEducation=0 ;




                   /*     Pie pie = AnyChart.pie();
                        List<DataEntry> data = new ArrayList<>();*/

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


                            budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets")
                                    .document("Budget").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()){

                                            ArrayList<BarEntry> data1 = new ArrayList<>();
                                            ArrayList<BarEntry> data2 = new ArrayList<>();

                                            String MainSpendings = document.get("CheltuieliPrincipale").toString();
                                            String Investments = document.get("Investitii").toString();
                                            String Savings = document.get("Economii").toString();
                                            String Entertainment = document.get("Distractii").toString();
                                            String Education = document.get("Educatie").toString();

                                            if(finalTotalMainSpendings != 0){
                                                relativeLayoutMainSpendings.setVisibility(View.VISIBLE);
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalMainSpendings) + " / " + MainSpendings);
                                                analitycsMainSpendingsAmount.setText(aux2);

                                                float percentage = (finalTotalMainSpendings *100) / Float.valueOf(MainSpendings);
                                                data1.add(new BarEntry(1 , Float.parseFloat(MainSpendings)));
                                                data2.add(new BarEntry(1 , finalTotalMainSpendings));

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
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalInvestments) + " / " + Investments);
                                                analitycsInvestmentsAmount.setText(aux2);

                                                float percentage = (finalTotalInvestments *100) / Float.valueOf(Investments);

                                                data1.add(new BarEntry(2 , Float.parseFloat(Investments)));
                                                data2.add(new BarEntry(2 , finalTotalInvestments));

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
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalSavings) + " / " + Savings);
                                                analitycsSavingsAmount.setText(aux2);
                                                float percentage = (finalTotalSavings *100) / Float.valueOf(Savings);

                                                data1.add(new BarEntry(3 , Float.parseFloat(Savings)));
                                                data2.add(new BarEntry(3 , finalTotalSavings));

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
                                                String aux2 = String.valueOf(String.format("%.2f",finalTotalEntertainment) + " / " + Entertainment);
                                                analitycsEntertainmentAmount.setText(aux2);

                                                float percentage = (finalTotalEntertainment *100) / Float.valueOf(Entertainment);

                                                data1.add(new BarEntry(4 , Float.parseFloat(Entertainment)));
                                                data2.add(new BarEntry(4 , finalTotalEntertainment));

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

                                                float percentage = (finalTotalEducation *100) / Float.valueOf(Education);

                                                data1.add(new BarEntry(5 , Float.parseFloat(Education)));
                                                data2.add(new BarEntry(5 , finalTotalEducation));

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
  //------------------------------BARCHART ANALYTICS-------------------------------------------------------------------------
                                            data1.add(new BarEntry(6 , 0));

                                            BarDataSet barDataSet1 = new BarDataSet(data1, "totals");
                                            barDataSet1.setColor(Color.RED);

                                            BarDataSet barDataSet2 = new BarDataSet(data2, "categories");
                                            barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

                                            BarData data = new BarData(barDataSet1,barDataSet2);

                                            float barSpace = 0.02f;
                                            float groupSpace = 0.1f;
                                            anyChartViewBudget.setData(data);

                                            data.setBarWidth(0.43f);
                                            anyChartViewBudget.groupBars(1, groupSpace,barSpace);



                                            XAxis xAxis = anyChartViewBudget.getXAxis();
                                            xAxis.setValueFormatter(new IndexAxisValueFormatter(categories));
                                            xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                                            xAxis.setTextColor(Color.WHITE);
                                            xAxis.setGranularity(1);
                                            xAxis.setCenterAxisLabels(true);
                                            xAxis.setAxisMinimum(1);
//------------------------------------------------------------------------------------------------------------

                                            float totalTot = finalTotalMainSpendings + finalTotalInvestments + finalTotalSavings +finalTotalEntertainment +finalTotalEducation;
                                            String aux = String.valueOf( "Total: "+ String.format("%.2f",totalTot) + " / " + document.get("VenitTotal").toString());

                                            totalBudgetAmountTextView.setText(aux);

//-----------------------------------------PREDICTIONS------------------------------------------------------------

                                            String message1= "";
                                            String message2= "";
                                            String message3 = "";
                                            String message4 = "";
                                            if(dateID.equals(dateSaved)) {
                                                investmentsData = getIntent().getFloatArrayExtra("investments_data");
                                                savingsData = getIntent().getFloatArrayExtra("savings_data");
                                                entertainmentData = getIntent().getFloatArrayExtra("entertainment_data");
                                                educationData = getIntent().getFloatArrayExtra("education_data");

                                                if(investmentsData.length > 1 && finalTotalInvestments!=0){
                                                    //return the day when the
                                                    int k = predict(investmentsData,Float.valueOf(Investments),finalTotalInvestments );
                                                    if(k!=0){
                                                        message1 = "Investments: " + k + "days";
                                                    }else{
                                                        message1="Investments in track";
                                                    }

                                                } else{
                                                    message1="Investments not predicted";
                                                }
                                                if(savingsData.length > 20 && finalTotalSavings!=0){
                                                    //return the day when the
                                                    int k = predict(savingsData,Float.valueOf(Savings),finalTotalSavings );
                                                    if(k!=0){
                                                        message2 = "Savings: " + k + "days";
                                                    }else{
                                                        message2="Savings in track";
                                                    }
                                                }else{
                                                    message2="Savings not predicted";
                                                }
                                                if(entertainmentData.length > 20 && finalTotalEntertainment!=0){
                                                    //return the day when the
                                                    int k = predict(entertainmentData,Float.valueOf(Entertainment),finalTotalEntertainment );
                                                    if(k!=0){
                                                        message3 = "Entertainment: " + k + "days";
                                                    }else{
                                                        message3="Entertainment in track";
                                                    }
                                                }else{
                                                    message3="Entertainment not predicted";
                                                }
                                                if(educationData.length > 20 && finalTotalEducation!=0){
                                                    //return the day when the
                                                    int k = predict(educationData,Float.valueOf(Education),finalTotalEducation );
                                                    if(k!=0){
                                                        message4 = "Education: " + k + "days";
                                                    }else{
                                                        message4="Education in track";
                                                    }
                                                }else{
                                                    message4="Education not predicted";
                                                }


                                                //------------------------------------------------------------------------------------afisare----------------------------------------------------------------------------------------------------------

                                                String afisareConcluzie ="Budget exceeded in:\n "+ "Main spendings in track" + "\n" +message1 + "\n" +message2+ "\n"+ message3 +"\n"+message4;
                                                Log.d("DATE", afisareConcluzie);

                                                AlertDialog.Builder myDialog = new AlertDialog.Builder(BudgetAnalyticsActivity.this);
                                                LayoutInflater inflater =  LayoutInflater.from(BudgetAnalyticsActivity.this);
                                                View myView = inflater.inflate(R.layout.prediction_errors, null);
                                                myDialog.setView(myView);

                                                final AlertDialog dialog = myDialog.create();
                                                dialog.setCancelable(true);


                                                final TextView error1 = myView.findViewById(R.id.error2);
                                                error1.setText(afisareConcluzie);
                                                error1.setTextColor(Color.WHITE);

                                                dialog.show();
                                            }

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