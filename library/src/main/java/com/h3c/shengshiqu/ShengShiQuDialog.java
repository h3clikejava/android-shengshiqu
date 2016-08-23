package com.h3c.shengshiqu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.h3c.shengshiqu.widget.ShengShiQuPicker;

/**
 * Created by H3c on 16/8/23.
 */

public class ShengShiQuDialog extends DialogFragment implements View.OnClickListener {
    public ShengShiQuPicker picker;
    private String[] mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(inflateLayout(), null);
        initView(view);

        // 去掉系统默认的一个主题样式Title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    public int inflateLayout() {
        return R.layout.dialog_shengshiqu;
    }

    public void initView(View view) {
        view.findViewById(R.id.dialog_SSQ_doneBtn).setOnClickListener(this);
        view.findViewById(R.id.dialog_SSQ_cancelBtn).setOnClickListener(this);
        picker = (ShengShiQuPicker) view.findViewById(R.id.dialog_SSQPicker);
        picker.setData(mData);
    }

    @Override
    public void onResume() {
        if(isMatchParentWidth()) {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }

        super.onResume();
    }

    public boolean isMatchParentWidth() {
        return true;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.dialog_SSQ_doneBtn) {
            String[] result = picker.getResult();
            if(mListener != null) {
                mListener.onSSQDialogResult(result[0], result[1], result[2]);
            }
        }

        dismiss();
    }

    public ShengShiQuDialogListener mListener;
    public void setDataResultListener(ShengShiQuDialogListener l) {
        this.mListener = l;
    }

    public void setData(String[] data) {
        mData = data;
    }

    public interface ShengShiQuDialogListener {
        void onSSQDialogResult(String province, String city, String district);
    }
}
