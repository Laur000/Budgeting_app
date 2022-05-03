package com.example.budgeting_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private TextView totalBudgetAmountTextView;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private FirebaseFirestore budgetRef ;

    private String post_key = "";
    private String item = "";
    private float amount = 0 ;
    private String details = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        mAuth = FirebaseAuth.getInstance();

        budgetRef = FirebaseFirestore.getInstance();
        loader = new ProgressDialog(this);



        fab =findViewById(R.id.fab);
        totalBudgetAmountTextView = findViewById(R.id.totalBudgetAmountTextView);
        recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();

            }
        });
    }

    private void createTotal(String dateID){
        Map<String, Object> data = new HashMap<>();
        data.put("total", 0);

        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TOTAL CREATED SUCCES", "task created scuccesfully");
            }
        });



    }

    private void updateTotal(String dateID) {


       /* budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                .update("total", amount).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TOTAL UPDATE SUCCES", "DocumentSnapshot successfully written!");
            }
        });*/


        final float[] total = {0};
        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                .collection("Items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                        if(error!=null){
                            Log.e("Firebase Error" , error.getMessage());
                            return;
                        }

                         if (documentSnapshot != null && !documentSnapshot.getDocuments().isEmpty()) {

                            List<DocumentSnapshot> documents = documentSnapshot.getDocuments();

                            for (DocumentSnapshot item : documents) {

                                total[0] = total[0] +   Float.valueOf(item.get("amount").toString()) ;
                            }


                            budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                                    .update("total",  total[0]).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Log.d("TOTAL UPDATE SUCCES", "DocumentSnapshot successfully written!");
                                }
                            });
                        }

                    }
                });

    }


    private void addItem(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater =  LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText details = myView.findViewById(R.id.details);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);


        //Save button logic
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();

                String budgetDetails = details.getText().toString();

                //checks
                if(TextUtils.isEmpty(budgetDetails)){
                    budgetDetails = "no details";
                }
                if(TextUtils.isEmpty(budgetAmount)){
                    amount.setError("Amount is required!");
                    return;
                }
                if(budgetItem.equals("Select item")){
                    Toast.makeText(BudgetActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                }
                else{
                    //if everything is ok, then add the data
                    loader.setMessage("adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();


                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    DateFormat dateFormatID = new SimpleDateFormat("MM-yyyy");
                    Calendar cal = Calendar.getInstance();
                    String date = dateFormat.format(cal.getTime());
                    String dateID = dateFormatID.format(cal.getTime());


                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Months months =  Months.monthsBetween(epoch, now);

                    //add item
                    Data data = new Data(date, budgetDetails, Float.parseFloat(budgetAmount), months.getMonths(), budgetItem);

                   // budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).set(city); adaug email, il iau din field de login/register


                   budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                           .collection("Items").document()
                           .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(BudgetActivity.this, "Budget item added successfully", Toast.LENGTH_SHORT).show();
                               updateTotal(dateID);
                           }else{

                               Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                           }
                            loader.dismiss();
                       }
                   });

          /*         CollectionReference docRef = budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid())
                             .collection("Budgets").document(dateID)
                            .collection("Categories").document(budgetItem).collection("Items");*/
                }
                dialog.dismiss();



            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();

        //get current date
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormatID = new SimpleDateFormat("MM-yyyy");
        String dateID  = dateFormatID.format(cal.getTime());

           budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                .collection("Items")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Log.e("Firebase Error" , error.getMessage());
                    return;
                }


                if(documentSnapshot.getDocuments().isEmpty()){
                        createTotal(dateID);
                }
                 else if (documentSnapshot != null && !documentSnapshot.getDocuments().isEmpty()) {


                    List<DocumentSnapshot> documents = documentSnapshot.getDocuments();
                    ArrayList<Data> dataArrayList = new ArrayList<Data>();
                    ArrayList<String> mUserKey = new ArrayList<String>();

                    for (DocumentSnapshot item : documents) {
                        dataArrayList.add(item.toObject(Data.class));
                        mUserKey.add(item.getId());
                        /*   Log.d("DataShow AMOUNT", item.get("amount").toString());
                        Log.d("DataShow DATE",  item.get("category").toString());*/


                    }

                    MyAdaptor myAdaptor = new MyAdaptor(BudgetActivity.this,dataArrayList, mUserKey);
                    myAdaptor.notifyDataSetChanged();
                    recyclerView.setAdapter(myAdaptor);
                }

            }
        });

        budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TOTAL SPEND FAILED", "Listen failed.", error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            float totalShow = Float.valueOf(value.get("total").toString());


                            String sTotal = String.valueOf("Total spend: " + String.format("%.2f",totalShow));
                            totalBudgetAmountTextView.setText(sTotal);
                        } else {
                            Log.d("TOTAL SPEND: null", "TOTAL SPEND: null");
                        }
                    }
                });





        //!!!!!!!!1--------NEXT STEP: Total, dupa ce e creat automat, sa fie afisat sus 1:12:43 tutorial

        //cod tutorial initial
      /*
       FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>().setQuery(budgetRef, Data.class).build();
        FirebaseRecyclerAdapter <Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>() {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount("Allocated amount: " + model.getAmount());
                holder.setDate("On: " + model.getDate());
                holder.setItemName("BudgetDetails: " + model.getDetails());

            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent , false);
                return new MyViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();*/
    }


    public class MyAdaptor extends  RecyclerView.Adapter<MyAdaptor.MyViewHolder>{

            Context context;
            ArrayList<Data> dataArrayList;
            ArrayList<String>  mUserKey;

        public MyAdaptor(Context context, ArrayList<Data> dataArrayList ,ArrayList<String>  mUserKey) {
            this.context = context;
            this.dataArrayList = dataArrayList;
            this.mUserKey = mUserKey;
        }


        @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout, parent , false);
            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Data data = dataArrayList.get(position);

            holder.setItemName("Item: " + data.getCategory());
            holder.setItemAmount("Amount: " + data.getAmount());
            holder.setNote("Details: " + data.getDetails());
            holder.setDate("Date: " + data.getDate());

            switch (data.getCategory()){
                case "Transport":
                    holder.imageView.setImageResource(R.drawable.ic_transport);
                    break;
                case "Food":
                    holder.imageView.setImageResource(R.drawable.ic_food);
                    break;
                case "House":
                    holder.imageView.setImageResource(R.drawable.ic_house);
                    break;
                case "Entertainment":
                    holder.imageView.setImageResource(R.drawable.ic_entertainment);
                    break;
                case "Education":
                    holder.imageView.setImageResource(R.drawable.ic_education);
                    break;
                case "Eating out":
                    holder.imageView.setImageResource(R.drawable.ic_other); //TO DO ICON
                    break;
                case "Health":
                    holder.imageView.setImageResource(R.drawable.ic_health);
                    break;
                case "Gifts":
                    holder.imageView.setImageResource(R.drawable.ic_other); //TO DO ICON
                    break;
                case "Sports":
                    holder.imageView.setImageResource(R.drawable.ic_other); //TO DO ICON
                    break;
                case "Car":
                    holder.imageView.setImageResource(R.drawable.ic_other); //TO DO ICON
                    break;
                case "Pets":
                    holder.imageView.setImageResource(R.drawable.ic_other); //TO DO ICON
                    break;
                case "Clothes":
                    holder.imageView.setImageResource(R.drawable.ic_shirt);
                    break;

                case "Bills":
                    holder.imageView.setImageResource(R.drawable.ic_other); //TO DO ICON
                    break;
            }
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    post_key = mUserKey.get(position);
                    item = data.getCategory();
                    amount = data.getAmount();
                    details = data.getDetails();
                    updateData();

                }
            });


        }

            @Override
            public int getItemCount() {
            return dataArrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{

            View mView;
            public ImageView imageView;
           // public TextView notes;

            public MyViewHolder(View itemView){
                super(itemView);
                mView = itemView;
                imageView = itemView.findViewById(R.id.imageView);
                //notes = itemView.findViewById(R.id.note);



            }

            public void setItemName(String itemName){
                TextView item = mView.findViewById(R.id.item);
                item.setText(itemName);
            }
            public void setItemAmount(String itemAmount){
                TextView amount = mView.findViewById(R.id.amount);
                amount.setText(itemAmount);
            }
            public void setDate(String itemDate){
                TextView date = mView.findViewById(R.id.date);
                date.setText(itemDate);
            }
            public void setNote(String itemNote){
                TextView note = mView.findViewById(R.id.note);
                note.setText(itemNote);
            }

        }

    }

    private void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View mView = inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final  TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);


        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mNotes.setText(String.valueOf(details));
        mNotes.setSelection(String.valueOf(details).length());

        Button delBut = mView.findViewById(R.id.btnDelete);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);


        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat dateFormatID = new SimpleDateFormat("MM-yyyy");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());
        String dateID = dateFormatID.format(cal.getTime());


        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months =  Months.monthsBetween(epoch, now);


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Float.parseFloat(mAmount.getText().toString());
                details =  mNotes.getText().toString();



                //add item
                Data data = new Data(date, details, amount, months.getMonths(), item);

                budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                        .collection("Items").document(post_key)
                        .set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Budget item updated successfully", Toast.LENGTH_SHORT).show();
                            updateTotal(dateID);
                        }else{

                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });
                dialog.dismiss();
            }
        });


        delBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetRef.collection("Users").document(mAuth.getCurrentUser().getUid()).collection("Budgets").document(dateID)
                        .collection("Items").document(post_key)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Budget item deleted successfully", Toast.LENGTH_SHORT).show();
                            updateTotal(dateID);
                        }else{
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
}




}