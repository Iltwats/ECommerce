package com.atul.android.ecommerce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.android.ecommerce.Constants.Constants;
import com.atul.android.ecommerce.MyApp;
import com.atul.android.ecommerce.OrderActivity;
import com.atul.android.ecommerce.databinding.OrderItemsBinding;
import com.atul.android.ecommerce.databinding.OrderdetailsBinding;
import com.atul.android.ecommerce.fcm.FCMSender;
import com.atul.android.ecommerce.fcm.MessageFormatter;
import com.atul.android.ecommerce.model.Order;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context context;
    public List<Order> orderList;
    public int lastSelectedItemPosition;
    public static final int PLACED = 1, DELIVERED = 0, DECLINED = -1;
    MyApp app;
    ArrayList<String> orderIDs;
    Order order;


    public OrdersAdapter(Context context, List<Order> orderList, MyApp app, ArrayList<String> ordersIDs) {
        this.context = context;
        this.orderList = new ArrayList<>(orderList);
        this.app = app;
        this.orderIDs=ordersIDs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrderdetailsBinding b = OrderdetailsBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
        );
        return new OrdersViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String orderId= orderIDs.get(position);
        Order order = orderList.get(position);
        OrderdetailsBinding b = ((OrdersViewHolder) holder).bin;
        b.orderUserName.setText("" + order.name);
        b.orderTotalItems.setText("Items: " + order.total_items);
        b.orderTotalPrice.setText("Rs. " + order.total_price);
        setupOrderStatus(b, order.action);
        setupProductsView(b, order);
        b.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.orderStatus.setText("ACCEPTED");
                b.orderStatus.setTextColor(Color.GREEN);
                updateStatus(orderId,DELIVERED);
                sendNotification(orderId,"Has been Accepted and Will be delivered shortly.");
            }
        });
        b.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.orderStatus.setText("DECLINED");
                b.orderStatus.setTextColor(Color.RED);
                updateStatus(orderId,DECLINED);
                sendNotification(orderId,"Has been declined, sorry for the Inconvenience.");

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lastSelectedItemPosition = holder.getAdapterPosition();
                return false;
            }
        });

    }

    private void setupProductsView(OrderdetailsBinding b, Order order) {
        b.allOrderItems.removeAllViews();
        for (int i = 0; i< order.orderItems.size(); i++) {
            OrderItemsBinding bindingNew = OrderItemsBinding.inflate(LayoutInflater.from(context));
            bindingNew.itemName.setText("" + order.orderItems.get(i).name);
            bindingNew.itemQuantity.setText("Total Items/Quantity: " + order.orderItems.get(i).quantity);
            bindingNew.itemPrice.setText("    Rs." + order.orderItems.get(i).price);

            b.allOrderItems.addView(bindingNew.getRoot());
        }
    }

    private void setupOrderStatus(OrderdetailsBinding b, int action) {
        if (action == Order.OrderStatus.PLACED) {
            b.orderStatus.setText("PLACED");
            b.orderStatus.setTextColor(Color.YELLOW);
        } else if (action == Order.OrderStatus.DECLINED) {
            b.orderStatus.setText("DECLINED");
            b.orderStatus.setTextColor(Color.RED);
        } else {
            b.orderStatus.setText("ACCEPTED");
            b.orderStatus.setTextColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private void updateStatus(String id, int status) {
        app.db.collection(Constants.ORDERS).document(id).update("action",status);
    }
    public void sendNotification(String orderId,String message) {
        new FCMSender().send(MessageFormatter.getSampleMessage("users", "Your Order ID. "+ orderId, message), new Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    Log.e("mssg",response.toString());
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("mssg",e.toString());
            }
        });
    }

}
