package com.lfh.custom.widget.picker.activity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by lifuhai on 2017/2/7 0007.
 */
public class WheelParam implements Parcelable {
    private String mTitle;
    private int mSelectedPosition;
    private List<String> mData;

    protected WheelParam(Parcel in) {
        mTitle = in.readString();
        mSelectedPosition = in.readInt();
        mData = in.createStringArrayList();
    }

    public static final Creator<WheelParam> CREATOR = new Creator<WheelParam>() {
        @Override
        public WheelParam createFromParcel(Parcel in) {
            return new WheelParam(in);
        }

        @Override
        public WheelParam[] newArray(int size) {
            return new WheelParam[size];
        }
    };

    public List<String> getData() {
        return mData;
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    public String getTitle() {
        return mTitle;
    }

    public static Builder builder() {
        return new Builder();
    }

    private WheelParam(Builder pBuilder) {
        this.mTitle = pBuilder.mTitle;
        this.mSelectedPosition = pBuilder.mSelectedPosition;
        this.mData = pBuilder.mData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pParcel, int pI) {
        pParcel.writeString(mTitle);
        pParcel.writeInt(mSelectedPosition);
        pParcel.writeStringList(mData);
    }

    public static class Builder {
        private String mTitle;
        private int mSelectedPosition = 0;
        private List<String> mData;

        public Builder setTitle(String pTitle) {
            mTitle = pTitle;
            return this;
        }

        public Builder setSelectedPosition(int pSelectedPosition) {
            mSelectedPosition = pSelectedPosition;
            return this;
        }

        public Builder setData(List<String> pData) {
            mData = pData;
            return this;
        }

        public WheelParam build() {
            return new WheelParam(this);
        }
    }
}
