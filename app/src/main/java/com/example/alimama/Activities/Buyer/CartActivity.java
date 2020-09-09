package com.example.alimama.Activities.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alimama.Model.Cart;
import com.example.alimama.Prevalent.Prevalent;
import com.example.alimama.R;
import com.example.alimama.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView totalAmt, txtmsg1;
    private int overAllTotalPrice = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = findViewById(R.id.next_process_btn);
        totalAmt = findViewById(R.id.totalPrice);
        txtmsg1 = findViewById(R.id.msg1);

        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price" , String.valueOf(overAllTotalPrice));
                startActivity(intent);
                finish();
            }
        });
        totalAmt.setText("Total Price = Rs. " + String.valueOf(overAllTotalPrice));
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckOrderStatus();
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Cart> options =
                new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                .child(Prevalent.currentOnlineUser.getPhone())
                        .child("Products"),Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter
                = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.txtProductQuantity.setText("Quantity = " + model.getQuantity());
                holder.txtProductPrice.setText("Price = Rs. " + model.getPrice());
                holder.txtProductName.setText(model.getName());

                int oneTypeProductPrice = ((Integer.valueOf(model.getPrice()))) * (Integer.valueOf(model.getQuantity()));
                overAllTotalPrice = overAllTotalPrice + oneTypeProductPrice;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]
                                {
                                        "Edit",
                                        "Remove"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Options");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i==0)
                                {
                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if(i==1)
                                {
                                   cartListRef.child("User View")
                                           .child(Prevalent.currentOnlineUser.getPhone())
                                           .child("Products")
                                           .child(model.getPid())
                                           .removeValue()
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful())
                                                   {
                                                       Toast.makeText(CartActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();
                                                       Intent intent = new Intent(CartActivity.this, HomeActivity.class);

                                                       startActivity(intent);
                                                   }

                                               }
                                           });
                                }


                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }
    private void CheckOrderStatus()
    {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());
                orderRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String shippingState = snapshot.child("state").getValue().toString();
                            String userName = snapshot.child("name").getValue().toString();

                            if(shippingState.equals("SHIPPED"))
                            {
                                totalAmt.setText("Dear " + userName + "\nOrder is Shipped Successfully");
                                recyclerView.setVisibility(View.GONE);
                                txtmsg1.setVisibility(View.VISIBLE);
                                txtmsg1.setText("Your order has been shipped and we will be delivered to you soon");
                                NextProcessBtn.setVisibility(View.GONE);

                            }
                            else if(shippingState.equals("NOT_SHIPPED"))
                            {
                                totalAmt.setText("Dear " + userName + "\nOrder is not yet shipped");
                                recyclerView.setVisibility(View.GONE);
                                txtmsg1.setVisibility(View.VISIBLE);
                                NextProcessBtn.setVisibility(View.GONE);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}