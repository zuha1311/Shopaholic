package com.example.alimama.Activities.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alimama.Activities.LoginActivity;
import com.example.alimama.Prevalent.Prevalent;
import com.example.alimama.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity {

    private String check = "";
    private TextView title,titleQuestions;
    private EditText phone,ques1,ques2;
    private Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        check = getIntent().getStringExtra("check");
        title = findViewById(R.id.pageTitle);
        titleQuestions = findViewById(R.id.title_questions);
        phone = findViewById(R.id.fp_pn);
        ques1 = findViewById(R.id.question_1);
        ques2 = findViewById(R.id.question_2);
        submit = findViewById(R.id.fp_pw_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        phone.setVisibility(View.GONE);


        if(check.equals("settings"))
        {
            title.setText("Set Questions");

            titleQuestions.setText("Please set answers for the following security questions");
            submit.setText("Set");
            DisplayPreviousAnswers();

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setAnswers();

                }
            });
        }
        else if(check.equals("login"))
        {
            phone.setVisibility(View.VISIBLE);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    verifyUser();

                }
            });
        }
    }

    private void verifyUser() {

        final String mobile = phone.getText().toString();
        final String answer1 = ques1.getText().toString().toLowerCase();
        final String answer2 = ques2.getText().toString().toLowerCase();

        if(!mobile.equals("") && !answer1.equals("") && !answer2.equals(""))
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mobile);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        String mPhone = snapshot.child("phone").getValue().toString(); //if error then check if it is mobile or phone

                        if(snapshot.hasChild("Security Answers"))
                        {
                            String ans1 = snapshot.child("Security Answers").child("answer1").getValue().toString();
                            String ans2 = snapshot.child("Security Answers").child("answer2").getValue().toString();

                            if(!ans1.equals(answer1))
                            {
                                Toast.makeText(ForgotPasswordActivity.this, "Your first answer is wrong", Toast.LENGTH_SHORT).show();
                            }
                            else if(!ans2.equals(answer2))
                            {
                                Toast.makeText(ForgotPasswordActivity.this, "Your second answer is wrong", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                builder.setTitle("New Password");
                                final EditText newPassword = new EditText(ForgotPasswordActivity.this);
                                newPassword.setHint("Write Password Here");
                                builder.setView(newPassword);

                                builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(!newPassword.getText().toString().equals(""))
                                        {
                                            ref.child("password").setValue(newPassword.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful())
                                                            {
                                                                Toast.makeText(ForgotPasswordActivity.this, "Password Chnaged Successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });
                                builder.show();

                            }
                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this, "You have not set the security questions", Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        else
        {
            Toast.makeText(this, "Please complete the form", Toast.LENGTH_SHORT).show();
        }


    }

    private void setAnswers()
    {
        String answer1 = ques1.getText().toString().toLowerCase();
        String answer2 = ques2.getText().toString().toLowerCase();

        if(answer1.equals("") && answer2.equals(""))
        {
            Toast.makeText(ForgotPasswordActivity.this, "Please answer the questions", Toast.LENGTH_SHORT).show();
        }
        else
        {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(Prevalent.currentOnlineUser.getPhone());

            HashMap<String, Object> quesMap = new HashMap<>();
            quesMap.put("answer1", answer1);
            quesMap.put("answer2", answer2);

            ref.child("Security Answers").updateChildren(quesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ForgotPasswordActivity.this, "Answers updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPasswordActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });

        }
    }
    private void DisplayPreviousAnswers()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Prevalent.currentOnlineUser.getPhone());
        ref.child("Security Answers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String ans1 = snapshot.child("answer1").getValue().toString();
                    String ans2 = snapshot.child("answer2").getValue().toString();

                    ques1.setText(ans1);
                    ques2.setText(ans2);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}