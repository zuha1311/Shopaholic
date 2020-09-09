package com.example.alimama.Activities.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alimama.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SellerLoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button login;
    private ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.seller_email_login);
        password = findViewById(R.id.seller_pw_login);
        login = findViewById(R.id.seller_LOGIN_btn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String e = email.getText().toString();
        String pw = password.getText().toString();

        if(!e.equals("") && !pw.equals(""))
        {
            progressDialog.setTitle("Logging In");
            progressDialog.setMessage("Please wait while we log you in");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(e,pw).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Intent intent = new Intent(SellerLoginActivity.this,SellerHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }

                }
            });



        }
        else
        {
            Toast.makeText(this, "Please enter your details", Toast.LENGTH_LONG).show();
        }

    }
}