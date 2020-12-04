package com.atul.android.ecommerce.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    public Timestamp orderTime;
    public String name;
    public List<CartItem> orderItems;
    public int action;
    public int total_price, total_items;

    public Order() {
    }

    public Order(String name, Timestamp orderTime, List<CartItem> orderItems, int action, int total_price, int total_items) {
        this.orderTime = orderTime;
        this.name = name;
        this.orderItems = orderItems;
        this.action = action;
        this.total_price = total_price;
        this.total_items = total_items;
    }

    public static class OrderStatus {

        public static final int PLACED = 1 // Initially (U)
                , DELIVERED = 0, DECLINED = -1;     //(A)

    }

}
