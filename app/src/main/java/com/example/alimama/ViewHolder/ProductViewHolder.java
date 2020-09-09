package com.example.alimama.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alimama.R;
import com.example.alimama.itemClickListener;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName,txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public itemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.product_image);
        txtProductName = itemView.findViewById(R.id.product_name);
        txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductPrice = itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListener(itemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        listener.onClick(view, getAdapterPosition(),false);

    }
}
