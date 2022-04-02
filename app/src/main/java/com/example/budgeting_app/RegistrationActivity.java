package com.example.budgeting_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email, password, passwordConfirm;
    private Button regBtn;

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        regBtn = findViewById(R.id.regBtn);
        passwordConfirm = findViewById(R.id.passwordConfirm);

        //lasam momentan ca butonul de sign in sa te duca pe login, dar trebuie sa intre direct in aplicatie dupa
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                String passwordConfirmString = passwordConfirm.getText().toString();


                if(TextUtils.isEmpty(emailString)){
                    email.setError("Email is required");
                }
                if(TextUtils.isEmpty(passwordString)){
                    password.setError("Password is required");
                }

                if(TextUtils.isEmpty(passwordConfirmString)){
                    passwordConfirm.setError("Confirm your password");
                }

                else{
                    if(passwordString.equals(passwordConfirmString)){
                    progressDialog.setMessage("registration in progress");
                    progressDialog.setCanceledOnTouchOutside(false);  //touch outside and cancel
                    progressDialog.show();

                    mAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //aici pot accesa uid-ul
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);  //from Login to MainActivity
                                startActivity(intent);
                                finish();
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(RegistrationActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                    //adauga in firestore database datele despre cont
                }
                    else{
                        passwordConfirm.setError("You added a different password");
                    }
            }
            }
        });
    }
}