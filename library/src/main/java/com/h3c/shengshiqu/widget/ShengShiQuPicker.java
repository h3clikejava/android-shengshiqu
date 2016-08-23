package com.h3c.shengshiqu.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.h3c.shengshiqu.R;
import com.h3c.shengshiqu.helper.ProvinceDataLoader;

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
        quPicker.setOnValueChangedListener(getQuPickerChangeListener());

        loadData();
    }

    // 支持自定义布局,但是一定要注意控件ID
    public int inflateLayout() {
        return DEFAULT_LAYOUT;
    }

    // 省Picker
    public NumberPicker findShengPicker() {
        return (NumberPicker) findViewById(R.id.ssq_shengPicker);
    }

    // 市Picker
    public NumberPicker findShiPicker() {
        return (NumberPicker) findViewById(R.id.ssq_shiPicker);
    }

    // 区Picker
    public NumberPicker findQuPicker() {
        return (NumberPicker) findViewById(R.id.ssq_quPicker);
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

    private NumberPicker.OnValueChangeListener getShengPickerChangeListener() {
        return new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int from, int to) {
                refreshCitiesPicker(to);
            }
        };
    }

    private NumberPicker.OnValueChangeListener getShiPickerChangeListener() {
        return new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int from, int to) {
                refreshDistrictPicker(to);
            }
        };
    }

    private NumberPicker.OnValueChangeListener getQuPickerChangeListener() {
        return new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int from, int to) {
                mCurrentDistrictIndex = to;
            }
        };
    }

    public String[] getResult() {
        String[] result = new String[3];
        String provinceStr = null;
        if(mProvinceData != null && mCurrentProvinceIndex < mProvinceData.length) {
            provinceStr = mProvinceData[mCurrentProvinceIndex];
            result[0] = provinceStr;
        }

        String cityStr = null;
        if(mCitiesData != null && !TextUtils.isEmpty(provinceStr)) {
            String[] citiesData = mCitiesData.get(provinceStr);
            if(citiesData != null && mCurrentCityIndex < citiesData.length) {
                cityStr = citiesData[mCurrentCityIndex];
                result[1] = cityStr;
            }
        }

        String districtStr = null;
        if(mDistrictData != null && !TextUtils.isEmpty(cityStr)) {
            String[] districtsData = mDistrictData.get(cityStr);
            if(districtsData != null && mCurrentDistrictIndex < districtsData.length) {
                districtStr = districtsData[mCurrentDistrictIndex];
                result[2] = districtStr;
            }
        }

        return result;
    }

    public void setData(final String[] inputData) {
        if(inputData == null || inputData.length != 3) return;

        Integer tmpProvinceIndex = null;
        Integer tmpCityIndex = null;
        Integer tmpDistrictIndex = null;

        if(mProvinceData != null) {
            for (int n = 0; n < mProvinceData.length; n++) {
                if(mProvinceData[n].equals(inputData[0])) {
                    tmpProvinceIndex = n;
                    String[] citiesData = mCitiesData.get(inputData[0]);

                    if(citiesData != null && citiesData.length > 0) {
                        for (int i = 0; i < citiesData.length; i++) {
                            if(citiesData[i].equals(inputData[1])) {
                                tmpCityIndex = i;

                                String[] districtsData = mDistrictData.get(inputData[1]);
                                if(districtsData != null && districtsData.length > 0) {
                                    for (int j = 0; j < districtsData.length; j++) {
                                        if(districtsData[j].equals(inputData[2])) {
                                            tmpDistrictIndex = j;
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        if(tmpProvinceIndex == null || tmpCityIndex == null || tmpDistrictIndex == null) return;

        mCurrentProvinceIndex = tmpProvinceIndex;
        shengPicker.setValue(mCurrentProvinceIndex);
        refreshCitiesPicker(mCurrentProvinceIndex);
        mCurrentCityIndex = tmpCityIndex;
        shiPicker.setValue(mCurrentCityIndex);
        refreshDistrictPicker(mCurrentCityIndex);
        mCurrentDistrictIndex = tmpDistrictIndex;
        quPicker.setValue(mCurrentDistrictIndex);
    }
}
