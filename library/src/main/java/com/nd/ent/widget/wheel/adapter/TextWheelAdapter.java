package com.nd.ent.widget.wheel.adapter;

import android.content.Context;

import java.util.List;

public class TextWheelAdapter extends AbstractWheelTextAdapter {
    private List<String> mData;

    public TextWheelAdapter(Context context) {
        super(context);
    }

    public void setData(List<String> data) {
        mData = data;
    }

    @Override
    public CharSequence getItemText(int index) {
        if (mData == null) {
            return "";
        }
        if (index < mData.size()) {
            return mData.get(index);
        }
        return "";
    }

    @Override
    public int getItemsCount() {
        return mData == null ? 0 : mData.size();
    }
}
