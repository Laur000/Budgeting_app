package com.example.budgeting_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class setBudgetActivity extends AppCompatActivity {

    private FloatingActionButton setBudget;
    private TextView setVenitTotal, cheltuieliPrincipale60, economii10, distractii10,investitii10,educatie10;

    private ProgressDialog loader;
    private FirebaseFirestore budgetRef ;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_budget);


        loader = new ProgressDialog(this);
        setBudget = findViewById(R.id.butonSet);
        budgetRef = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //Text views
        setVenitTotal = findViewById(R.id.setVenitTotal);
        cheltuieliPrincipale60 = findViewById(R.id.cheltuieliPrincipale60);
        economii10 = findViewById(R.id.economii10);
        distractii10 = findViewById(R.id.distractii10);
        investitii10 = findViewById(R.id.investitii10);
        educatie10 = findViewById(R.id.educatie10);

        setBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBudgetAmount();
            }
        });

    }

    private void createBudget(){
        Map<String, Object> data = new HashMap<>();
        data.put("VenitTotal", 0);
        data.put("CheltuieliPrincipale", 0);
        data.put("Economii", 0);
        data.put("Distractii", 0);
        data.put("Investitii", 0);
        data.put("Educatie", 0);


        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("BUDGET CREATED SUCCES", "BUDGET created scuccesfully");
            }
        });
    }
    private void updateBudget(Float venitTotal){

        //update the main table from here? after you modified it

        Float cheltuieliTotale = (venitTotal*60)/100;
        Float economii = (venitTotal*10)/100;
        Float distractii = (venitTotal*10)/100;
        Float investitii = (venitTotal*10)/100;
        Float educatie = (venitTotal*10)/100;




        //venitTotal
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .update("VenitTotal",venitTotal);

       //Cheltuieli principale
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .update("CheltuieliPrincipale",cheltuieliTotale);

        //economii
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .update("Economii",economii);

        //economii
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .update("Distractii",distractii);
        //economii
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .update("Investitii",investitii);
        //economii
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document("Budget")
                .update("Educatie",educatie);
        //--------------------------------------------update the page----------------------------
        setVenitTotal.setText("Total income: "+ venitTotal.toString());
        setVenitTotal.setTextColor(Color.WHITE);

        cheltuieliPrincipale60.setText("Main spendings: "+cheltuieliTotale.toString());
        cheltuieliPrincipale60.setTextColor(Color.parseColor("#4147D5"));

        economii10.setText("Savings: " + economii.toString());
        economii10.setTextColor(Color.parseColor("#4147D5"));

        distractii10.setText("Entertainment: " + distractii.toString());
        distractii10.setTextColor(Color.parseColor("#4147D5"));

        investitii10.setText("Investments: " + investitii.toString());
        investitii10.setTextColor(Color.parseColor("#4147D5"));

        educatie10.setText("Education: "+ educatie.toString());
        educatie10.setTextColor(Color.parseColor("#4147D5"));





        loader.dismiss();



    }


    public void setBudgetAmount(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater =  LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.set_budget_amount_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

         final EditText venitLunar = myView.findViewById(R.id.venitLunar);
        final Button cancelSetBudget = myView.findViewById(R.id.cancelSetBudget);
        final Button saveSetBuget = myView.findViewById(R.id.saveSetBuget);



        //Save button logic
        saveSetBuget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String budgetAmount = venitLunar.getText().toString();


                //checks
                if(TextUtils.isEmpty(budgetAmount)){
                    venitLunar.setError("Amount is required!");
                    return;
                }
                else{
                    //if everything is ok, then add the data
                    loader.setMessage("update total income");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    Float venitLunarFloat = Float.parseFloat(budgetAmount);

                    Float cheltuieliTotale = (venitLunarFloat*60)/100;
                    Float economii = (venitLunarFloat*10)/100;
                    Float distractii = (venitLunarFloat*10)/100;
                    Float investitii = (venitLunarFloat*10)/100;
                    Float educatie = (venitLunarFloat*10)/100;


                    updateBudget(venitLunarFloat);
                    //creare buget functie + update



                }
                dialog.dismiss();



            }
        });

        cancelSetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public void onStart() {
        super.onStart();
        loader.setMessage("setting budget");
        loader.setCanceledOnTouchOutside(false);
        loader.show();


        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets")
                .document("Budget").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(!task.getResult().exists()){
                    createBudget();
                }
                if(task.getResult().exists()){

                    setVenitTotal.setText("Total income: "+ task.getResult().get("VenitTotal").toString());
                    setVenitTotal.setTextColor(Color.WHITE);

                    cheltuieliPrincipale60.setText("Main spendings: "+task.getResult().get("CheltuieliPrincipale").toString());
                    cheltuieliPrincipale60.setTextColor(Color.parseColor("#4147D5"));

                    economii10.setText("Savings: " + task.getResult().get("Economii").toString());
                    economii10.setTextColor(Color.parseColor("#4147D5"));

                    distractii10.setText("Entertainment: " + task.getResult().get("Distractii").toString());
                    distractii10.setTextColor(Color.parseColor("#4147D5"));

                    investitii10.setText("Investments: " + task.getResult().get("Investitii").toString());
                    investitii10.setTextColor(Color.parseColor("#4147D5"));

                    educatie10.setText("Education: "+ task.getResult().get("Educatie").toString());
                    educatie10.setTextColor(Color.parseColor("#4147D5"));

                    loader.dismiss();
                }
            }

        });

    }

}