package com.example.sony.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity{
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,WakeService.class));
    }
}
