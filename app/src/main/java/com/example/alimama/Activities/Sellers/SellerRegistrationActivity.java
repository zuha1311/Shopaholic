package com.example.alimama.Activities.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {
    private Button login,register;
    private EditText nameInput, phoneInput,addressInput, emailInput,passwordInput;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        login = findViewById(R.id.seller_already_account_btn);
        register = findViewById(R.id.seller_register_btn);
        nameInput=findViewById(R.id.seller_name);
        emailInput = findViewById(R.id.seller_email);
        phoneInput=findViewById(R.id.seller_phonee);
        addressInput=findViewById(R.id.seller_address);
        passwordInput = findViewById(R.id.seller_pw);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerSeller();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(SellerRegistrationActivity.this,SellerLoginActivity.class);
               startActivity(intent);
           }
       });
    }

    private void registerSeller() {

        final String name = nameInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String mobile = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String address = addressInput.getText().toString();

        if(!name.equals("") && (!mobile.equals("")) && (!password.equals("")) && (!email.equals("")) &&(!address.equals("")))
        {

            progressDialog.setTitle("Creating Account");
            progressDialog.setMessage("Please wait, while we confirm and register your details");
            progressDialog.setCanceledOnTouchOutside(false);

            progressDialog.show();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            final DatabaseReference rootRef;
                             rootRef = FirebaseDatabase.getInstance().getReference();

                            String sid = mAuth.getCurrentUser().getUid();
                            HashMap<String,Object> sellerMap = new HashMap<>();
                            sellerMap.put("Sid",sid);
                            sellerMap.put("phone",mobile);
                            sellerMap.put("email",email);
                            sellerMap.put("address",address);
                            sellerMap.put("name",name);
                            sellerMap.put("password",password);

                            rootRef.child("Sellers").child(sid).updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(SellerRegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                        Intent intent =new Intent(SellerRegistrationActivity.this,SellerHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });
                        }

                    }
                });
        }
        else
        {
            Toast.makeText(this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
        }
    }
}