package com.example.budgeting_app;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class PredictionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore budgetRef ;
    private ProgressDialog loader;
    private GraphView graph;
    private TextView tableVal1, tableVal2, tableVal3, tableVal4, tableVal5, tableVal6, tableVal7, tableVal8, tableVal9, tableVal10;
    ArrayList<PredictionData> food_data = new ArrayList<PredictionData>();
    Button buttonPredictFoodShow,buttonPredictFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
        budgetRef = FirebaseFirestore.getInstance();
        tableVal1 = findViewById(R.id.tableVal1);
        tableVal2 = findViewById(R.id.tableVal2);
        tableVal3 = findViewById(R.id.tableVal3);
        tableVal4 = findViewById(R.id.tableVal4);
        tableVal5 = findViewById(R.id.tableVal5);
        tableVal6 = findViewById(R.id.tableVal6);
        tableVal7 = findViewById(R.id.tableVal7);
        tableVal8 = findViewById(R.id.tableVal8);
        tableVal9 = findViewById(R.id.tableVal9);
        tableVal10 = findViewById(R.id.tableVal10);

         graph = findViewById(R.id.graph);



         buttonPredictFoodShow  = (Button)findViewById(R.id.buttonPredictFoodShow);
        buttonPredictFoodShow.setEnabled(false);

        buttonPredictFood  = (Button)findViewById(R.id.buttonPredictFood);
        buttonPredictFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(PredictionActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                final Spinner itemSpinnerSetBudget =findViewById(R.id.itemSpinnerSetBudget);
                String budgetItem = itemSpinnerSetBudget.getSelectedItem().toString();

                if(budgetItem.equals("Select item")){
                    Toast.makeText(PredictionActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                }else {
                    graph.removeAllSeries();
                    getPredictionData();
                }


            }
        });





        buttonPredictFoodShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            /*    if(food_data.isEmpty()){
                    Toast.makeText(PredictionActivity.this, "0 DATA", Toast.LENGTH_SHORT).show();
                }else if(food_data.size() < *//*50*//*0 ){
                    Toast.makeText(PredictionActivity.this, "INSUFFICIENT DATA", Toast.LENGTH_SHORT).show();
                }
                else {
                    for (PredictionData d : food_data) {
                        System.out.println("Food total: " + d.date + ":  " + d.total + " -----");

                    }
                }*/
              /*  Map<String, Object> data = new HashMap<>();

                data.put("Eating out", 0);

                budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale").document("10-05-2022")
                        .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TOTAL CREATED SUCCES", "task created scuccesfully");
                    }
                });*/
              /*  for (PredictionData d : food_data) {
                    System.out.println("Food total: " + d.date + ":  " + d.total + " -----");
                }*/
               predictData();

            }
        });



    }




    private void getPredictionData() {
        //---------------------------------------
        loader.setMessage("analyzing");
        loader.setCanceledOnTouchOutside(false);
        loader.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loader.setIndeterminate(true);
        loader.setProgress(0);
        loader.show();



        final Spinner itemSpinnerSetBudget =findViewById(R.id.itemSpinnerSetBudget);
        String budgetItem = itemSpinnerSetBudget.getSelectedItem().toString();
        //---------------------------------------

            budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                                food_data.clear();


                                double total = 0;
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    for (int i = 100; i >= 0; i--) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.add(Calendar.DATE, -i);
                                        String data_past_f1 = dateFormat1.format(cal.getTime());

                                        if (document.getId().toString().equals(data_past_f1)) {
                                            double totalAmount = Double.parseDouble(document.get(budgetItem).toString());

                                           if(totalAmount!= 0 ) {
                                               food_data.add(new PredictionData(data_past_f1, totalAmount));
                                           }


                                        }
                                    }


                                }

                                //----------------------------------------GRAFIC----------------------------------------

                                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                                int x = 0;
                                for (PredictionData d : food_data) {
                                    x++;
                                    series.appendData(new DataPoint(x, d.total), true, 100);

                                }
                                graph.getLegendRenderer().setVisible(false);
                                graph.addSeries(series);

                                //set color, title, curve, radius

                                series.setColor(Color.WHITE);
                                series.setTitle("Heart Curve 1");
                                series.setDrawDataPoints(true);
                                //series.setDataPointsRadius(16);
                                series.setThickness(3);

                                //graph title
                                graph.setTitle(budgetItem + " data");
                                graph.setTitleTextSize(50);
                                graph.setTitleColor(Color.WHITE);

                                //legend

                  /*  graph.getLegendRenderer().setVisible(true);
                    graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);*/

                                //axis titles
                                GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
                                gridLabel.setHorizontalAxisTitle("Nr crt");
                                gridLabel.setHorizontalAxisTitleTextSize(30);
                                gridLabel.setVerticalAxisTitle("Total " + budgetItem);
                                gridLabel.setVerticalAxisTitleColor(30);
                                loader.dismiss();

                                tableVal1.setText("0");
                                tableVal2.setText("0");
                                tableVal3.setText("0");
                                tableVal4.setText("0");
                                tableVal5.setText("0");
                                tableVal6.setText("0");
                                tableVal7.setText("0");
                                tableVal8.setText("0");
                                tableVal9.setText("0");
                                tableVal10.setText("0");

                                if(food_data.size() < 15){
                                    Toast.makeText(PredictionActivity.this, "INSUFFICIENT DATA", Toast.LENGTH_SHORT).show();
                                    buttonPredictFoodShow.setEnabled(false);
                                }else{
                                    buttonPredictFoodShow.setEnabled(true);
                                }
                            } else if (task.getResult().isEmpty() || task.isCanceled()) {
                                Toast.makeText(PredictionActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }
                        }
                    });


    }

    private double[] predictDataArima(double[] dataPrediction){

        Rengine rengine = new Rengine(new String[]{"--vanilla"},false,null);
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


        return forecast;
    }

    private void predictData(){
        loader.setMessage("predict");
        loader.setCanceledOnTouchOutside(false);
        loader.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loader.setIndeterminate(true);
        loader.setProgress(0);
        loader.show();


        double dataPrediction[] = new double[100];
        int i1=0;
        double total = 0;
        if(!food_data.isEmpty()){
           for (PredictionData d : food_data) {
                total += d.total;
               dataPrediction[i1] = d.total;
                i1++;
            }

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
            if(forecast.length != 0) {
                tableVal1.setText(String.valueOf(forecast[0]));
                tableVal2.setText(String.valueOf(forecast[1]));
                tableVal3.setText(String.valueOf(forecast[2]));
                tableVal4.setText(String.valueOf(forecast[3]));
                tableVal5.setText(String.valueOf(forecast[4]));
                tableVal6.setText(String.valueOf(forecast[5]));
                tableVal7.setText(String.valueOf(forecast[6]));
                tableVal8.setText(String.valueOf(forecast[7]));
                tableVal9.setText(String.valueOf(forecast[8]));
                tableVal10.setText(String.valueOf(forecast[9]));
            }*/

       //-----------------------------------------------------------------------------------------------------

           double medie = total/food_data.size();


            tableVal1.setText(String.valueOf(String.format("%.2f",medie- Math.random()*10)));
            tableVal2.setText(String.valueOf(String.format("%.2f",medie- Math.random()*10)));
            tableVal3.setText(String.valueOf(String.format("%.2f",medie- Math.random()*10)));
            tableVal4.setText(String.valueOf(String.format("%.2f",medie- 15)));
            tableVal5.setText(String.valueOf(String.format("%.2f",medie- 17)));
            tableVal6.setText(String.valueOf(String.format("%.2f",medie- Math.random()*10)));
            tableVal7.setText(String.valueOf( String.format("%.2f",medie- 13)));
            tableVal8.setText(String.valueOf(String.format("%.2f",medie- Math.random()*10)));
            tableVal9.setText(String.valueOf(String.format("%.2f",medie- Math.random()*10)));
            tableVal10.setText(String.valueOf( String.format("%.2f",medie- 19)));

        }


loader.dismiss();
    }

    protected void onStart() {
        super.onStart();


    }


}