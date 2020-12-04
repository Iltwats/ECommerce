package com.atul.android.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.atul.android.ecommerce.databinding.ActivityNotificationBinding;

public class NotificationActivity extends AppCompatActivity {

    private ActivityNotificationBinding notifyb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifyb = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(notifyb.getRoot());
    }
}