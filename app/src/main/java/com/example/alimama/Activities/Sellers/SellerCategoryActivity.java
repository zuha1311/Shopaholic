package com.example.alimama.Activities.Sellers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.alimama.R;

public class SellerCategoryActivity extends AppCompatActivity {
    private ImageView tshirts,sports,dresses,sweaters;
    private ImageView glasses,hats,wallets,shoes;
    private ImageView headphones,laptops,watches,mobiles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_category);
        tshirts = findViewById(R.id.tshirts);
        sports = findViewById(R.id.sports);
        dresses = findViewById(R.id.femaleDresses);
        sweaters = findViewById(R.id.sweaters);
        glasses = findViewById(R.id.glasses);
        hats=findViewById(R.id.hatsAndCaps);
        wallets = findViewById(R.id.purses_bags);
        shoes = findViewById(R.id.shoes);
        headphones=findViewById(R.id.headphones);
        laptops=findViewById(R.id.laptops);
        watches=findViewById(R.id.watches);
        mobiles=findViewById(R.id.mobile);


        tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","tShirts");
                startActivity(intent);
            }
        });
        sports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","sports");
                startActivity(intent);
            }
        });
        dresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","dresses");
                startActivity(intent);
            }
        });
        sweaters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","sweaters");
                startActivity(intent);
            }
        });
        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","glasses");
                startActivity(intent);
            }
        });
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","hats");
                startActivity(intent);
            }
        });
        wallets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","wallets");
                startActivity(intent);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","shoes");
                startActivity(intent);
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","laptops");
                startActivity(intent);
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","watches");
                startActivity(intent);
            }
        });
        mobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerCategoryActivity.this, SellerAddNewProducts.class);
                intent.putExtra("category","mobiles");
                startActivity(intent);
            }
        });
    }
}