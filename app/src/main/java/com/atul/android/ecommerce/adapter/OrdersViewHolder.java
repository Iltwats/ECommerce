package com.atul.android.ecommerce.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.atul.android.ecommerce.databinding.OrderdetailsBinding;

public class OrdersViewHolder extends RecyclerView.ViewHolder {
    OrderdetailsBinding bin;
    public OrdersViewHolder(OrderdetailsBinding bin) {
        super(bin.getRoot());
        this.bin =bin;
    }
}
