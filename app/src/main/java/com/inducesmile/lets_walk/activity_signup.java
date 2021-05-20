package com.inducesmile.lets_walk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class activity_signup extends AppCompatActivity {

    private ImageView logo, joinus;
    private AutoCompleteTextView email, password;
    private Button signup;
    private TextView signin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeGUI();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String inputPw = password.getText().toString().trim();
                final String inputEmail = email.getText().toString().trim();

                if(validateInput(inputPw, inputEmail))
                    registerUser(inputPw, inputEmail);

            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_signup.this, activity_login.class));
            }
        });

    }


    private void initializeGUI(){

        logo = findViewById(R.id.ivRegLogo);
        email =  findViewById(R.id.atvEmailReg);
        password = findViewById(R.id.atvPasswordReg);
        signin = findViewById(R.id.tvSignIn);
        signup = findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void registerUser(final String inputPw,final String inputEmail) {

        progressDialog.setMessage("Verificating...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(inputEmail, inputPw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    sendUserData(userId, inputEmail, inputPw);
                    Toast.makeText(activity_signup.this, "You've been registered successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(activity_signup.this, activity_userDetails.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(activity_signup.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserData(String userId, String email, String password){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        Map newUser= new HashMap();
        newUser.put("Email",email);
        newUser.put("Password",password);
        mDatabase.setValue(newUser);
    }

    private boolean validateInput(String inPw, String inEmail){

        if (!Patterns.EMAIL_ADDRESS.matcher(inEmail).matches()){
            email.setError("Please enter a Valid Email address!");
            return false;
        }
        if(inPw.isEmpty()){
            password.setError("Password is empty.");
            return false;
        }
        if(inEmail.isEmpty()){
            email.setError("Email is empty.");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}