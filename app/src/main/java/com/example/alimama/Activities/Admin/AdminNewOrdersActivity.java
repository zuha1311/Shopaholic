package com.example.alimama.Activities.Admin;

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

import com.example.alimama.Model.AdminOrders;
import com.example.alimama.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersList;
    private DatabaseReference orderRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_orders);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        ordersList = findViewById(R.id.order_recy);
        ordersList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<AdminOrders> options =
                new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderRef,AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders,AdminOrdersViewHolder> adapter =
                new FirebaseRecyclerAdapter<AdminOrders, AdminOrdersViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AdminOrdersViewHolder holder,final  int position, @NonNull final AdminOrders model) {
                            holder.userName.setText("Name: " + model.getName());
                            holder.userMobile.setText("Mobile: " + model.getMobile());
                            holder.userTotalPrice.setText("Total Price = Rs. " + model.getTotalAmount());
                            holder.userShippingAddress.setText("Shipping Address: " + model.getAddress());
                            holder.userDateTime.setText("Date of Order: " + model.getDate());

                            holder.showOrders.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String uid=getRef(position).getKey();
                                    Intent intent = new Intent(AdminNewOrdersActivity.this, AdminUserProductsActivity.class);
                                    intent.putExtra("uid",uid);
                                    startActivity(intent);
                                }
                            });

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                 "Yes",
                                                 "No"
                                            };
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AdminNewOrdersActivity.this);
                                    builder.setTitle("Have you shipped the items?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            if(i==0)
                                            {
                                                String uid = getRef(position).getKey();
                                                RemoveOrder(uid);
                                            }
                                            else
                                            {
                                                finish();
                                            }

                                        }
                                    });
                                    builder.show();
                                }
                            });
                    }

                    @NonNull
                    @Override
                    public AdminOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout,parent,false);
                        return new AdminOrdersViewHolder(view);

                    }
                };

        ordersList.setAdapter(adapter);
        adapter.startListening();

    }

    private void RemoveOrder(String uid) {
        orderRef.child(uid).removeValue();
    }

    public static class AdminOrdersViewHolder extends RecyclerView.ViewHolder
    {
        public TextView userName, userMobile, userTotalPrice, userDateTime, userShippingAddress;
        public Button showOrders;

        public AdminOrdersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.order_user_name);
            userMobile = itemView.findViewById(R.id.mobile_admin_order);
            userTotalPrice = itemView.findViewById(R.id.order_total_price);
            userDateTime = itemView.findViewById(R.id.order_dateTime);
            userShippingAddress = itemView.findViewById(R.id.order_address );
            showOrders = itemView.findViewById(R.id.admin_show_products_Btn);

        }
    }

}