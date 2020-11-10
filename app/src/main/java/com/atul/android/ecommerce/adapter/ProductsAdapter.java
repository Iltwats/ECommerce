package com.atul.android.ecommerce.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;


import com.atul.android.ecommerce.MainActivity;
import com.atul.android.ecommerce.R;
import com.atul.android.ecommerce.databinding.VarientItemLayoutBinding;
import com.atul.android.ecommerce.databinding.WeightItemLayoutBinding;
import com.atul.android.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    public List<Product> productList, allProductsList;
    public int lastSelectedItemPosition;

    public ProductsAdapter(Context context, List<Product> productList) {
        this.context = context;
        allProductsList = productList;
        this.productList = new ArrayList<>(productList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Product.WEIGHT_BASED) {
            WeightItemLayoutBinding b = WeightItemLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            );
            return new WeightBasedProductViewHolder(b);
        } else {
            VarientItemLayoutBinding b = VarientItemLayoutBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
            );
            return new VarientsBasedProductViewHolder(b);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product.type == Product.WEIGHT_BASED) {
            WeightItemLayoutBinding b = ((WeightBasedProductViewHolder) holder).b;
            b.nameWeight.setText(product.name);
            b.priceWeight.setText("Rs. " + product.pricePerKg);
            b.minQtyWeight.setText("MinQty - " + product.minQty);

            showContextMenu(b.getRoot());
        } else {
            VarientItemLayoutBinding b = ((VarientsBasedProductViewHolder) holder).b;
            b.nameVarient.setText(product.name);
            b.varientsVarients.setText(product.varientsString());

            showContextMenu(b.getRoot());
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });
    }

    private void showContextMenu(ConstraintLayout root) {
        root.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                if (!(context instanceof MainActivity)) {
                    return;
                }
                ((MainActivity) context).getMenuInflater().inflate(R.menu.menu_context, menu);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return productList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void filterList(String query) {
        query = query.toLowerCase();
        productList = new ArrayList<>();
        for (Product p : allProductsList) {
            if (p.name.toLowerCase().contains(query)) {
                productList.add(p);
            }
        }
        notifyDataSetChanged();
    }

    public static class WeightBasedProductViewHolder extends RecyclerView.ViewHolder {

        WeightItemLayoutBinding b;

        public WeightBasedProductViewHolder(@NonNull WeightItemLayoutBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

    public static class VarientsBasedProductViewHolder extends RecyclerView.ViewHolder {

        VarientItemLayoutBinding b;

        public VarientsBasedProductViewHolder(@NonNull VarientItemLayoutBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }

}
