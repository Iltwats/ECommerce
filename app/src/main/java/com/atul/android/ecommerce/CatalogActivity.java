package com.atul.android.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.atul.android.ecommerce.databinding.ActivityCatalogBinding;

public class CatalogActivity extends AppCompatActivity {

    private ActivityCatalogBinding bind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        userActivity();
    }

    private void userActivity() {
        bind.editProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CatalogActivity.this,"Clicked Edit Products",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CatalogActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        bind.orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CatalogActivity.this,"Clicked Orders",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CatalogActivity.this,OrderActivity.class);
                startActivity(intent);
            }
        });
        bind.notifyUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CatalogActivity.this,"Clicked Notify Users",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CatalogActivity.this,NotificationActivity.class);
                startActivity(intent);
            }
        });
    }


}