package com.h3c.shengshiqu.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * Created by H3c on 16/8/23.
 */

public class SSQCustomPicker extends NumberPicker {

    public SSQCustomPicker(Context context) {
        super(context);
        init();
    }

    public SSQCustomPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);
        updateView(child);
    }

    public void updateView(View view) {
        if (view instanceof EditText) {
            //这里修改字体的属性
            if(ShengShiQuPicker.PICKER_TEXT_COLOR != null) {
                ((EditText) view).setTextColor(ShengShiQuPicker.PICKER_TEXT_COLOR);
            }
            if(ShengShiQuPicker.PICKER_TEXT_SIZE != null) {
                ((EditText) view).setTextSize(ShengShiQuPicker.PICKER_TEXT_SIZE);
            }
        }
    }

    @Override
    public void setDisplayedValues(String[] displayedValues) {
        super.setDisplayedValues(displayedValues);

        int customDividerColor = customDividerColor();
        if(customDividerColor != 0) {
            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        //设置分割线的颜色值
                        pf.set(this, new ColorDrawable(customDividerColor));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    // 自定义分割线的颜色
    public int customDividerColor() {
        if(ShengShiQuPicker.PICKER_DIVIDER_COLOR != null) {
            return ShengShiQuPicker.PICKER_DIVIDER_COLOR;
        }
        return 0;
    }
}
