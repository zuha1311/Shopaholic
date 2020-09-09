package com.example.alimama.Activities.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.alimama.Model.Product;
import com.example.alimama.R;
import com.example.alimama.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SearchActivity extends AppCompatActivity {
    private Button search;
    private EditText search_name;
    private RecyclerView recyclerView;
    private String searchText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        search_name = findViewById(R.id.search_product_name);
        search = findViewById(R.id.search_btn);
        recyclerView = findViewById(R.id.recy1);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = search_name.getText().toString();
                onStart();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Product> options =
                new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(reference.orderByChild("pname").startAt(searchText).endAt(searchText),Product.class)   //if error check what is the name in databse
                .build();

        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                holder.txtProductName.setText(model.getName());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Rs. " + model.getPrice());
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid",model.getPid());
                        startActivity(intent);
                    }
                });

            }


            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_items_layout,parent,false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;

            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}