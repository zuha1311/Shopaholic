package com.example.alimama.Activities.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alimama.Activities.Sellers.SellerCategoryActivity;
import com.example.alimama.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductsActivity extends AppCompatActivity {
    private Button applyChanges, deleteProduct;
    private EditText name,price,description;
    private ImageView imageView;
    private String productId = "";
    DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_products);
        productId = getIntent().getStringExtra("pid");
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productId);

        applyChanges = findViewById(R.id.maintain_products_btn);
        deleteProduct = findViewById(R.id.admin_deleteProduct_btn);
        name = findViewById(R.id.admin_maintainProduct_product_name);
        price = findViewById(R.id.admin_maintainProduct_product_price);
        description = findViewById(R.id.admin_maintainProduct_product_description);
        imageView = findViewById(R.id.admin_maintainProducts_product_image);
        
        displaySpecificProductInfo();

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_Product();
            }
        });

        applyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apply_changes();
            }
        });
    }

    private void delete_Product() {

        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerCategoryActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(AdminMaintainProductsActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private void apply_changes() {

        String n_name = name.getText().toString();
        String n_price = price.getText().toString();
        String n_description = description.getText().toString();
        
        if(n_name.equals(""))
        {
            Toast.makeText(this, "Write down product name", Toast.LENGTH_SHORT).show();
        }
        else if(n_price.equals(""))
        {
            Toast.makeText(this, "Write down product price", Toast.LENGTH_SHORT).show();
        }
        else if(n_description.equals(""))
        {
            Toast.makeText(this, "Write down product description", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String,Object> productMap = new HashMap<>();
            productMap.put("pid",productId);
            productMap.put("description",n_description);
            productMap.put("price",n_price);
            productMap.put("name",n_name);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(AdminMaintainProductsActivity.this, "Changes applied successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminMaintainProductsActivity.this, SellerCategoryActivity.class);
                            startActivity(intent);
                            finish();
                        }
                }
            });
        }

    }

    private void displaySpecificProductInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    String p_name = snapshot.child("name").getValue().toString();
                    String p_price = snapshot.child("price").getValue().toString();
                    String p_description = snapshot.child("description").getValue().toString();
                    String p_image  = snapshot.child("image").getValue().toString();

                    name.setText(p_name);
                    price.setText(p_price);
                    description.setText(p_description);
                    Picasso.get().load(p_image).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}