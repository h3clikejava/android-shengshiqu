package com.h3c.shengshiqu.demo;

import android.app.Application;
import android.graphics.Color;

import com.h3c.shengshiqu.widget.ShengShiQuPicker;

/**
 * Created by H3c on 16/8/25.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ShengShiQuPicker.initSSQPikcerColorInApplication(Color.RED, 12, Color.BLUE);
    }
}
