package com.example.alimama.Activities.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.alimama.Activities.Buyer.HomeActivity;
import com.example.alimama.MainActivity;
import com.example.alimama.R;

public class AdminHomeActivity extends AppCompatActivity {

    private Button admin_logout, admin_check_order,maintainProducts, checkApprovedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        admin_logout = findViewById(R.id.admin_logout_btn);
        admin_check_order = findViewById(R.id.check_orders_btn);
        maintainProducts = findViewById(R.id.maintain_products_btn);
        checkApprovedProducts = findViewById(R.id.check_approved_orders_btn);

        checkApprovedProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });

        maintainProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin", "Admin");
                startActivity(intent);


            }
        });

        admin_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        admin_check_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (AdminHomeActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);
            }
        });

    }
}