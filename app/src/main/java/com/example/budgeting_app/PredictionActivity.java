package com.example.budgeting_app;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

public class PredictionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore budgetRef ;
    private ProgressDialog loader;
    private GraphView graph;
    ArrayList<PredictionData> food_data = new ArrayList<PredictionData>();
  //  private Button buttonPredictFood;

    Semaphore semafor = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);
        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);
        budgetRef = FirebaseFirestore.getInstance();

         graph = findViewById(R.id.graph);




       Button buttonPredictFood  = (Button)findViewById(R.id.buttonPredictFood);
        buttonPredictFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(PredictionActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();

               getPredictionData();


            }
        });

        Button buttonPredictFoodShow  = (Button)findViewById(R.id.buttonPredictFoodShow);

        buttonPredictFoodShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(PredictionData d: food_data){
                    System.out.println("Food total: "+ d.date+":  " + d.total+ " -----");

                }
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

        //---------------------------------------


        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Totale").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    DateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                    food_data.clear();
                    //as putea sa fac aici un vector de date sa iau datele pe 100 de zile si dupa sa am doar un for in care parcurg ala si caut

                    double total = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        for (int i = 10 /*100*/; i >= 0; i--) {
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DATE, -i);
                            String data_past_f1 = dateFormat1.format(cal.getTime());

                            if(document.getId().toString().equals(data_past_f1)){
                                double totalAmount = Double.parseDouble(document.get("Food").toString());
                                food_data.add(new PredictionData(data_past_f1, totalAmount));
                         }
                      }


                }
            //----------------------------------------GRAFIC----------------------------------------

                    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                    int x=0;
                    for (PredictionData d: food_data){
                        x++;
                        series.appendData(new DataPoint( x,d.total),true,100);

                    }
                    graph.getLegendRenderer().setVisible(false);
                    graph.addSeries(series);

                    //set color, title, curve, radius

                    series.setColor(Color.RED);
                    series.setTitle("Heart Curve 1");
                    series.setDrawDataPoints(true);
                    //series.setDataPointsRadius(16);
                    series.setThickness(8);

                    //graph title
                    graph.setTitle("Food data prediction");
                    graph.setTitleTextSize(90);
                    graph.setTitleColor(Color.BLUE);

                    //legend

                  /*  graph.getLegendRenderer().setVisible(true);
                    graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);*/

                    //axis titles
                    GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
                    gridLabel.setHorizontalAxisTitle("Nr crt");
                    gridLabel.setHorizontalAxisTitleTextSize(30);
                    gridLabel.setVerticalAxisTitle("Total food");
                    gridLabel.setVerticalAxisTitleColor(30);
                    loader.dismiss();
            } else if (task.getResult().isEmpty()) {
                    System.out.println("EMPTY TASK");
                    loader.dismiss();
            }
            }
        });


    }

    private void predictData(){

        final Button buttonPredictFood  = findViewById(R.id.buttonPredictFood);

        buttonPredictFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


              /*  Rengine re = new Rengine(new String[]{"--no-save"},false,null);


                String bVector = "c(4,5,6)";
                String rv = re.eval(bVector).asString();
                if(rv.isEmpty()){
                    Log.d("RLIB", "Sum of two vectors : c = :  ");

                }*/
                Log.d("RLIB", "Sum of two vectors : c = :  NIMIC");
                double[] x = {
                        12.8, 12.2, 11.9, 10.9, 10.6, 11.3, 11.1, 10.4, 10.0, 9.7, 9.7, 9.7,
                        11.1, 10.5, 10.3, 9.8, 9.8, 10.4, 10.4, 10.0, 9.7, 9.3, 9.6, 9.7,
                        10.8, 10.7, 10.3, 9.7, 9.5, 10.0, 10.0, 9.3, 9.0, 8.8, 8.9, 9.2,
                        10.4, 10.0, 9.6, 9.0, 8.5, 9.2, 9.0, 8.6, 8.3, 7.9, 8.0, 8.2,
                        9.3, 8.9, 8.9, 7.7, 7.6, 8.4, 8.5, 7.8, 7.6, 7.3, 7.2, 7.3,
                        8.5, 8.2, 7.9, 7.4, 7.1, 7.9, 7.7, 7.2, 7.0, 6.7, 6.8, 6.9,
                        7.8, 7.6, 7.4, 6.6, 6.8, 7.2, 7.2, 7.0, 6.6, 6.3, 6.8, 6.7,
                        8.1, 7.9, 7.6, 7.1, 7.2, 8.2, 8.1, 8.1, 8.2, 8.7, 9.0, 9.3,
                        10.5, 10.1, 9.9, 9.4, 9.2, 9.8, 9.9, 9.5, 9.0, 9.0, 9.4, 9.6,
                        11.0, 10.8, 10.4, 9.8, 9.7, 10.6, 10.5, 10.0, 9.8, 9.5, 9.7, 9.6,
                        10.9, 10.3, 10.4, 9.3, 9.3, 9.8, 9.8, 9.3, 8.9, 9.1, 9.1, 9.1,
                        10.2, 9.9, 9.4
                };

                int[] times = {
                        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
                        13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
                        25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36,
                        37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48,
                        49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
                        61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72,
                        73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
                        85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96,
                        97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108,
                        109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120,
                        121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132,
                        133, 134, 135
                };






            }
        });
    }

    protected void onStart() {
        super.onStart();
       /* getPredictionData();
        for(PredictionData d: food_data){
            Log.d("LISTA", " => " + d.date+":  " + d.total+ " -----");
        }*/
     //   predictData();

      /*  Rengine engine = new Rengine(new String[]{"--no-save"},false,null);

        String aVector = "c(1,2,3)";
        String bVector = "c(4,5,6)";
        engine.eval("a<-"+aVector);
        engine.eval("b<-"+bVector);
        engine.eval("c<-a+b");*/

       // Rengine re = new Rengine(new String[] { "--vanilla" }, false, null);
    /*    if (!re.waitForR()) {
            Log.d("RLIB", "Cannot load R " );

            return;
        }

        String bVector = "c(4,5,6)";
        String rv = re.eval(bVector).asString();
        if(rv.isEmpty()){
            Log.d("RLIB", "Sum of two vectors : c = :  ");

        }*/

        //  Log.d("RLIB", "Sum of two vectors : c = :  " + rv);


    }


}