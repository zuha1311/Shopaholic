package com.example.alimama.Activities;

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

import com.example.alimama.Activities.Admin.AdminHomeActivity;
import com.example.alimama.Activities.Buyer.HomeActivity;
import com.example.alimama.Activities.Buyer.ForgotPasswordActivity;
import com.example.alimama.Model.Users;
import com.example.alimama.Prevalent.Prevalent;
import com.example.alimama.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText inputNumber, inputPassword;
    Button login;
    ProgressDialog loadingBar;
    String parentDBName = "Users";
    TextView adminLink, notadminLink,forgotPassword;
    private CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button)findViewById(R.id.login_btn);
        inputNumber = (EditText)findViewById(R.id.login_phone_input);
        inputPassword = (EditText)findViewById(R.id.login_password_input);
        adminLink = (TextView) findViewById(R.id.admin_panel_link);
        notadminLink = (TextView) findViewById(R.id.not_admin_panel_link);
        loadingBar = new ProgressDialog(this);
        forgotPassword = findViewById(R.id.forgot_pw);
        chkBoxRememberMe = findViewById(R.id.remember_me_chk_box);
        Paper.init(this);
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notadminLink.setVisibility(View.VISIBLE);
                parentDBName = "Admins";
                chkBoxRememberMe.setVisibility(View.INVISIBLE);
                forgotPassword.setVisibility(View.INVISIBLE);
            }
        });

        notadminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notadminLink.setVisibility(View.INVISIBLE);
                parentDBName = "Users";

            }
        });

    }

    private void loginUser() {
        String phone = inputNumber.getText().toString();
        String password = inputPassword.getText().toString();
        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this,"Enter your phone", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Enter your password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials..");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            AllowAccessToAccount(phone,password);
        }
    }

    private void AllowAccessToAccount(final String phone, final String password) {

        if(chkBoxRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.UserPhoneKey, phone );
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(parentDBName).child(phone).exists())
                {
                        Users usersData = snapshot.child(parentDBName).child(phone).getValue(Users.class);
                        if(usersData.getPhone().equals(phone))
                        {
                            if(usersData.getPassword().equals(password))
                            {
                                if(parentDBName.equals("Admins"))
                                {
                                    Toast.makeText(LoginActivity.this,"Logged in Successfully", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                    startActivity(intent);
                                }
                                else if(parentDBName.equals("Users"))
                                {
                                    Toast.makeText(LoginActivity.this,"Logged in Successfully", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    Prevalent.currentOnlineUser = usersData;
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this,"Password is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Account with this " + phone + " does not exist. Please create an account" , Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}