package com.h3c.shengshiqu.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.h3c.shenshiqu.ShengShiQuPicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShengShiQuPicker picker = (ShengShiQuPicker) findViewById(R.id.shengshiquPicker);
    }
}
