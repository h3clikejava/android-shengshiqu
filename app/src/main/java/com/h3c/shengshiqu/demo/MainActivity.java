package com.h3c.shengshiqu.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.h3c.shengshiqu.ShengShiQuDialog;
import com.h3c.shengshiqu.widget.ShengShiQuPicker;

public class MainActivity extends AppCompatActivity {
    private ShengShiQuPicker picker;
    private String[] inputData = {"湖北省", "武汉市", "武昌区"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        picker = (ShengShiQuPicker) findViewById(R.id.shengshiquPicker);

        Button btn = (Button) findViewById(R.id.dialog);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShengShiQuDialog dialog = new ShengShiQuDialog();
                dialog.setData(inputData);
                dialog.setDataResultListener(new ShengShiQuDialog.ShengShiQuDialogListener() {
                    @Override
                    public void onSSQDialogResult(String province, String city, String district) {
                        inputData[0] = province;
                        inputData[1] = city;
                        inputData[2] = district;
                        Toast.makeText(MainActivity.this,
                                province + " " + city + " " + district, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(getSupportFragmentManager(), "shengshiquDialog");
            }
        });
    }
}
