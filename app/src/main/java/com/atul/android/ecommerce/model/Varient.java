package com.atul.android.ecommerce.model;

public class Varient {

    public String name;
    public int price;

    public Varient(String name, int price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " - Rs." + price;
    }

}
