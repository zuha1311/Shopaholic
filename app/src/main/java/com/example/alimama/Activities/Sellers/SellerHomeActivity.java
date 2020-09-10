package com.example.alimama.Activities.Sellers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.alimama.Activities.Admin.CheckNewProductActivity;
import com.example.alimama.MainActivity;
import com.example.alimama.Model.Product;
import com.example.alimama.R;
import com.example.alimama.ViewHolder.ProductViewHolder;
import com.example.alimama.ViewHolder.itemViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        recyclerView = findViewById(R.id.seller_home_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_add, R.id.navigation_logout)
        .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.navigation_home:
                Intent intenth = new Intent(SellerHomeActivity.this,SellerHomeActivity.class);
                startActivity(intenth);
                return true;



            case R.id.navigation_add:
                Intent intentCat = new Intent(SellerHomeActivity.this,SellerAddNewProducts.class);
                startActivity(intentCat);
                return true;


            case R.id.navigation_logout:
                final FirebaseAuth  mAuth;
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();

                Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(unverifiedProductsRef.orderByChild("sellerId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()),
                        Product.class).build();

        final FirebaseRecyclerAdapter<Product, itemViewHolder> adapter =
                new FirebaseRecyclerAdapter<Product, itemViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull itemViewHolder holder, int position, @NonNull final Product model) {
                        holder.txtProductName.setText("Quantity = " + model.getName());
                        holder.txtProductPrice.setText("Price = Rs. " + model.getPrice());
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductName.setText(model.getName());
                        holder.txtProductStatus.setText(model.getProductStatus());
                        Picasso.get().load(model.getImage()).into(holder.imageView);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final String productId = model.getPid();

                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };

                                AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                                builder.setTitle("Do you want to delete the product?");
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i==0)
                                        {
                                            DeleteProduct(productId);
                                        }

                                        else if(i==1)
                                        {

                                        }

                                    }
                                });

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public itemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);
                        itemViewHolder holder = new itemViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void DeleteProduct(String productId) {
        unverifiedProductsRef.child(productId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    Toast.makeText(SellerHomeActivity.this, "This item has been deleted successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}