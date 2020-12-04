package com.atul.android.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.atul.android.ecommerce.Constants.Constants;
import com.atul.android.ecommerce.adapter.OrdersAdapter;
import com.atul.android.ecommerce.databinding.ActivityOrderBinding;
import com.atul.android.ecommerce.fcm.FCMSender;
import com.atul.android.ecommerce.fcm.MessageFormatter;
import com.atul.android.ecommerce.model.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {

    private ActivityOrderBinding orderBinding;
    private MyApp myApp;
    OrdersAdapter ordersAdapter;
    List<Order> orderList = new ArrayList<>();
    ArrayList<String> orderIDs=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderBinding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(orderBinding.getRoot());

        myApp = (MyApp) getApplicationContext();
        fetchAllOrdersFromDB();
    }

    private void fetchAllOrdersFromDB() {
        myApp.db.collection(Constants.ORDERS)
                .orderBy(Constants.ORDER_TIME, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Order order = snapshot.toObject(Order.class);
                            orderList.add(order);
                            orderIDs.add(snapshot.getId());
                        }
                        setupAdapter();
                    }
                });
    }

    private void setupAdapter() {
        ordersAdapter = new OrdersAdapter(OrderActivity.this, orderList,myApp,orderIDs);
        orderBinding.allOrders.setAdapter(ordersAdapter);
        orderBinding.allOrders.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
        orderBinding.allOrders.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        );

    }

}