package com.inducesmile.lets_walk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class activity_PWreset extends AppCompatActivity{


    private ImageView ivLogo,ivPWreset;
    private TextView tvInfo, tvSignin;
    private AutoCompleteTextView atvEmail;
    private Button btnReset;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwreset);
        initializeGUI();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = atvEmail.getText().toString();

                if (email.isEmpty()) {
                    atvEmail.setError("Please, fill the email field.",null);
                }
                else {
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(activity_PWreset.this, "Email has been sent successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(activity_PWreset.this, activity_login.class));
                            } else {
                                Toast.makeText(activity_PWreset.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });


        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_PWreset.this,activity_login.class));
            }
        });


    }


    private void initializeGUI(){

        ivLogo = findViewById(R.id.ivLogLogo);
        ivPWreset = findViewById(R.id.ivPassReset);
        tvInfo = findViewById(R.id.tvPWinfo);
        tvSignin = findViewById(R.id.tvGoBack);
        atvEmail = findViewById(R.id.atvEmailRes);
        btnReset = findViewById(R.id.btnReset);

        firebaseAuth = FirebaseAuth.getInstance();

    }
}
