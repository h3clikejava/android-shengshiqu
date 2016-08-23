package com.h3c.shenshiqu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.h3c.shenshiqu.helper.ProvinceDataLoader;

import java.util.Map;

/**
 * 省市区及联选择控件
 * Created by H3c on 16/8/23.
 */

public class ShengShiQuPicker extends LinearLayout {
    private final int DEFAULT_LAYOUT = R.layout.layout_shengshiqu;
    private NumberPicker shengPicker;
    private NumberPicker shiPicker;
    private NumberPicker quPicker;

    private String[] mProvinceData;
    private Map<String, String[]> mCitiesData;
    private Map<String, String[]> mDistrictData;
    private int mCurrentProvinceIndex;
    private int mCurrentCityIndex;
    private int mCurrentDistrictIndex;

    public ShengShiQuPicker(Context context) {
        super(context);
        init();
    }

    public ShengShiQuPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(inflateLayout(), this, true);

        shengPicker = findShengPicker();
        shiPicker = findShiPicker();
        quPicker = findQuPicker();

        // 关掉编辑模式
        shengPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        shiPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        quPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        shengPicker.setOnValueChangedListener(getShengPickerChangeListener());
        shiPicker.setOnValueChangedListener(getShiPickerChangeListener());

        loadData();
    }

    // 支持自定义布局,但是一定要注意控件ID
    public int inflateLayout() {
        return DEFAULT_LAYOUT;
    }

    // 省Picker
    public NumberPicker findShengPicker() {
        return (NumberPicker) findViewById(R.id.shengPicker);
    }

    // 市Picker
    public NumberPicker findShiPicker() {
        return (NumberPicker) findViewById(R.id.shiPicker);
    }

    // 区Picker
    public NumberPicker findQuPicker() {
        return (NumberPicker) findViewById(R.id.quPicker);
    }

    private void loadData() {
        // 初始化数据
        ProvinceDataLoader.getInstance().getData(getContext(),
                new ProvinceDataLoader.ProvinceDataLoaderListener() {
                    @Override
                    public void provinceDataLoaderDone(
                            String[] provinceData,
                            Map<String, String[]> citiesData,
                            Map<String, String[]> districtData) {
                        mProvinceData = provinceData;
                        mCitiesData = citiesData;
                        mDistrictData = districtData;

                        initPickerData(shengPicker, provinceData);
                        refreshCitiesPicker(0);
                    }

                    @Override
                    public void provinceDataLoaderError() {
                    }
                });
    }

    private void initPickerData(NumberPicker picker, String[] data) {
        if(data == null || data.length < 1) return;

        picker.setDisplayedValues(null);
        picker.setMinValue(0);
        picker.setMaxValue(data.length - 1);
        picker.setWrapSelectorWheel(false);
        picker.setDisplayedValues(data);
    }

    private void refreshCitiesPicker(int provinceIndex) {
        if(mProvinceData != null && mProvinceData.length > provinceIndex) {
            mCurrentProvinceIndex = provinceIndex;

            String provinceStr = mProvinceData[provinceIndex];
            String[] citiesData = mCitiesData.get(provinceStr);
            initPickerData(shiPicker, citiesData);
            shiPicker.setValue(0);
            refreshDistrictPicker(0);
        }
    }

    private void refreshDistrictPicker(int cityIndex) {
        if(mProvinceData == null || mCurrentProvinceIndex >= mProvinceData.length) return;
        String provinceStr = mProvinceData[mCurrentProvinceIndex];
        String[] citiesData = mCitiesData.get(provinceStr);

        if(citiesData != null && citiesData.length > cityIndex) {
            mCurrentCityIndex = cityIndex;

            String cityStr = citiesData[cityIndex];
            initPickerData(quPicker, mDistrictData.get(cityStr));
            quPicker.setValue(0);
            mCurrentDistrictIndex = 0;
        }
    }

    public NumberPicker.OnValueChangeListener getShengPickerChangeListener() {
        return new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int from, int to) {
                refreshCitiesPicker(to);
            }
        };
    }

    public NumberPicker.OnValueChangeListener getShiPickerChangeListener() {
        return new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int from, int to) {
                refreshDistrictPicker(to);
            }
        };
    }

}
