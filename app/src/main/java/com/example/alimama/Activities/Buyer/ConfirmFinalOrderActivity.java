package com.example.alimama.Activities.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alimama.Prevalent.Prevalent;
import com.example.alimama.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText name, mobile, address, city;
    private Button confirmBtn;
    private String totalAmt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        name = findViewById(R.id.shipment_name);
        mobile = findViewById(R.id.shipment_phone_number);
        address = findViewById(R.id.shipment_address);
        city = findViewById(R.id.shipment_city);
        confirmBtn = findViewById(R.id.confirm_final_order_btn);
        totalAmt = getIntent().getStringExtra("Total Price");

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
    }

    private void Check() {
        if(TextUtils.isEmpty(name.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(mobile.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your Mobile", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(address.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your Address", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(city.getText().toString()))
        {
            Toast.makeText(this, "Please Enter your City", Toast.LENGTH_SHORT).show();
        }
        else
        {
            ConfirmOrder();
        }

    }

    private void ConfirmOrder() {
        final String saveCurrentDate, saveCurrentTime;

        Calendar callforDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callforDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callforDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String,Object> orderMap = new HashMap<>();
        orderMap.put("totalAmount",totalAmt);
        orderMap.put("name", name);
        orderMap.put("mobile", mobile);
        orderMap.put("address",address);
        orderMap.put("city",city);
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("state", "NOT_SHIPPED");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    FirebaseDatabase.getInstance().getReference()
                            .child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ConfirmFinalOrderActivity.this, "Congratulations! You're order has been confirmed", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        }
                    });
                }

            }
        });

    }
}