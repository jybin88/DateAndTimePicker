package com.lfh.custom.widget.time;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lfh.custom.widget.R;
import com.lfh.custom.widget.wheel.WheelView;
import com.lfh.custom.widget.wheel.adapter.NumericWheelAdapter;

import java.util.Calendar;

/**
 * 时间选择器
 * Created by Administrator on 2017/1/17 0017.
 */
public class TimePicker extends LinearLayout {
    private static final int MIN_HOUR = 1;
    private static final int MAX_HOUR = 24;
    private static final int MIN_SECOND = 0;
    private static final int MAX_SECOND = 59;

    /**
     * Default count of visible items
     */
    private static final int DEF_VISIBLE_ITEMS = 5;
    /**
     * Count of visible items
     */
    private int mVisibleItems = DEF_VISIBLE_ITEMS;
    /**
     * Default item textColor
     */
    private int mItemColor;
    /**
     * Selected item textColor
     */
    private int mSelectedColor;
    /**
     * Default item textSize
     */
    private int mItemSize;
    /**
     * Selected item textSize
     */
    private int mSelectedItemSize;
    /**
     * Center drawable resource id
     */
    private int mCenterDrawableId;
    /**
     * Each item height
     */
    private int mItemHeight;
    /**
     * Each item width
     */
    private int mItemWidth;
    /**
     * Is item cycle
     */
    private boolean mIsCycle = false;

    private int mSelectedHourIndex;
    private int mSelectedMinuteIndex;

    private WheelView mHourWheel;
    private WheelView mMinuteWheel;

    private NumericWheelAdapter mHourWheelAdapter;

    public TimePicker(Context context) {
        super(context);
        init(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int defSize = context.getResources().getDimensionPixelSize(R.dimen.ent_wheel_common_def_selected_text_size);
        int defSelectedSize = context.getResources().getDimensionPixelSize(R.dimen.ent_wheel_common_def_selected_text_size);
        int defColor = ContextCompat.getColor(context, R.color.ent_wheel_default_color);
        int defSelectColor = ContextCompat.getColor(context, R.color.ent_wheel_selected_color);

        if (attrs != null) {
            TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ent_wheel_time_attr);
            mItemColor = t.getColor(R.styleable.ent_wheel_time_attr_ent_wheel_time_item_color, defColor);
            mSelectedColor = t.getColor(R.styleable.ent_wheel_time_attr_ent_wheel_time_item_selector_color, defSelectColor);
            mItemSize = t.getDimensionPixelSize(R.styleable.ent_wheel_time_attr_ent_wheel_time_item_size, defSize);
            mSelectedItemSize = t.getDimensionPixelSize(R.styleable.ent_wheel_time_attr_ent_wheel_time_selected_item_size, defSize);
            mItemHeight = t.getDimensionPixelSize(R.styleable.ent_wheel_time_attr_ent_wheel_time_item_height, context.getResources().getDimensionPixelSize(R.dimen.ent_wheel_def_itemheight));
            mVisibleItems = t.getInt(R.styleable.ent_wheel_time_attr_ent_wheel_time_visibility_num, DEF_VISIBLE_ITEMS);
            mCenterDrawableId = t.getResourceId(R.styleable.ent_wheel_time_attr_ent_wheel_time_center_drawable, R.drawable.ent_wheel_wheelview_item_center_bg);
            t.recycle();
        } else {
            mItemColor = defColor;
            mSelectedColor = defSelectColor;
            mItemSize = defSize;
            mSelectedItemSize = defSelectedSize;
            mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.ent_wheel_def_itemheight);
            mVisibleItems = DEF_VISIBLE_ITEMS;
            mCenterDrawableId = R.drawable.ent_wheel_wheelview_item_center_bg;
        }

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        initWheelData();
        mItemWidth = (getScreenWidth() - 40) / 3;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 10;
        this.addView(mHourWheel, lp);
        this.addView(mMinuteWheel, lp);
    }

    private void initWheelData() {
        initCurrentTime();
        mHourWheel = new WheelView(this.getContext());
        mMinuteWheel = new WheelView(this.getContext());
        //set visible items
        mHourWheel.setVisibleItems(mVisibleItems);
        mMinuteWheel.setVisibleItems(mVisibleItems);
        //set item height
        mHourWheel.setWheelItemHeight(mItemHeight);
        mMinuteWheel.setWheelItemHeight(mItemHeight);
        //set item is cycle
        mHourWheel.setCyclic(mIsCycle);
        mMinuteWheel.setCyclic(mIsCycle);
        //set item size
        mHourWheel.setWheelItemSize(mItemSize);
        mMinuteWheel.setWheelItemSize(mItemSize);
        //set selected item size
        mHourWheel.setWheelSelectedItemSize(mSelectedItemSize);
        mMinuteWheel.setWheelSelectedItemSize(mSelectedItemSize);
        //set item color
        mHourWheel.setWheelItemColor(mItemColor);
        mMinuteWheel.setWheelItemColor(mItemColor);
        //set selected color
        mHourWheel.setWheelSelectedColor(mSelectedColor);
        mMinuteWheel.setWheelSelectedColor(mSelectedColor);
        //set center drawable resource id
        mHourWheel.setCenterDrawableId(mCenterDrawableId);
        mMinuteWheel.setCenterDrawableId(mCenterDrawableId);

        mHourWheelAdapter = new NumericWheelAdapter(this.getContext(), MIN_HOUR, MAX_HOUR);
        NumericWheelAdapter secondWheelAdapter = new NumericWheelAdapter(this.getContext(), MIN_SECOND, MAX_SECOND);
        mHourWheel.setViewAdapter(mHourWheelAdapter);
        mMinuteWheel.setViewAdapter(secondWheelAdapter);

        //默认显示当前时间
        mHourWheel.setCurrentItem(mSelectedHourIndex);
        mMinuteWheel.setCurrentItem(mSelectedMinuteIndex);
    }

    /**
     * 初始化当前时间
     */
    private void initCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        mSelectedHourIndex = calendar.get(Calendar.HOUR) - 1;
        mSelectedMinuteIndex = calendar.get(Calendar.MINUTE);
    }

    private int getScreenWidth() {
        return this.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 设置显示的时间
     *
     * @param pHour   hour
     * @param pMinute minute
     */
    public void setShowTime(int pHour, int pMinute) {
        if (0 == pHour) {//24点、0点判断
            mSelectedHourIndex = 23;
        } else {
            mSelectedHourIndex = pHour - 1;
        }

        mSelectedMinuteIndex = pMinute;
        mHourWheel.setCurrentItem(mSelectedHourIndex);
        mMinuteWheel.setCurrentItem(mSelectedMinuteIndex);
    }

    /**
     * Sets the desired count of visible items.
     * Actual amount of visible items depends on wheel layout parameters.
     * To apply changes and rebuild view call measure().
     *
     * @param pVisibleItems the desired count for visible items
     */
    @SuppressWarnings("unused")
    public void setVisibleItems(int pVisibleItems) {
        mVisibleItems = pVisibleItems;
        mHourWheel.setVisibleItems(pVisibleItems);
        mMinuteWheel.setVisibleItems(pVisibleItems);
    }

    /**
     * Set wheel cyclic flag
     *
     * @param pIsCycle the flag to set
     */
    @SuppressWarnings("unused")
    public void setCycle(boolean pIsCycle) {
        mIsCycle = pIsCycle;
        mHourWheel.setCyclic(pIsCycle);
        mMinuteWheel.setCyclic(pIsCycle);
    }

    /**
     * Set item color
     *
     * @param pItemColor item color
     */
    @SuppressWarnings("unused")
    public void setItemColor(@ColorInt int pItemColor) {
        mItemColor = pItemColor;
        mHourWheel.setWheelItemColor(pItemColor);
        mMinuteWheel.setWheelItemColor(pItemColor);
        invalidateWheel();
    }

    /**
     * Set item selected color
     *
     * @param pSelectedColor item selected color
     */
    @SuppressWarnings("unused")
    public void setSelectedColor(@ColorInt int pSelectedColor) {
        mSelectedColor = pSelectedColor;
        mHourWheel.setWheelSelectedColor(pSelectedColor);
        mMinuteWheel.setWheelSelectedColor(pSelectedColor);
        invalidateWheel();
    }

    /**
     * Set item size
     *
     * @param pItemSize item size
     */
    @SuppressWarnings("unused")
    public void setItemSize(int pItemSize) {
        mItemSize = pItemSize;
        mHourWheel.setWheelItemSize(pItemSize);
        mMinuteWheel.setWheelItemSize(pItemSize);
        invalidateWheel();
    }

    /**
     * Set item selected size
     *
     * @param pSelectedItemSize item selected size
     */
    @SuppressWarnings("unused")
    public void setSelectedItemSize(int pSelectedItemSize) {
        mSelectedItemSize = pSelectedItemSize;
        mHourWheel.setWheelSelectedItemSize(pSelectedItemSize);
        mMinuteWheel.setWheelSelectedItemSize(pSelectedItemSize);
        invalidateWheel();
    }

    /**
     * Set item height
     *
     * @param pItemHeight item height
     */
    @SuppressWarnings("unused")
    public void setItemHeight(int pItemHeight) {
        mItemHeight = pItemHeight;
        mHourWheel.setWheelItemHeight(pItemHeight);
        mMinuteWheel.setWheelItemHeight(pItemHeight);
        invalidateWheel();
    }

    /**
     * Set item width
     *
     * @param pItemWidth item width
     */
    @SuppressWarnings("unused")
    public void setItemWidth(int pItemWidth) {
        mItemWidth = pItemWidth;
        mHourWheel.getLayoutParams().width = pItemWidth;
        mMinuteWheel.getLayoutParams().width = pItemWidth;
        requestLayout();
    }

    /**
     * Set center drawable resource id
     *
     * @param pCenterDrawableId center drawable resource id
     */
    @SuppressWarnings("unused")
    public void setCenterDrawableId(@DrawableRes int pCenterDrawableId) {
        mCenterDrawableId = pCenterDrawableId;
        mHourWheel.setCenterDrawableId(mCenterDrawableId);
        mMinuteWheel.setCenterDrawableId(mCenterDrawableId);
        invalidateWheel();
    }

    /**
     * 刷新界面
     */
    private void invalidateWheel() {
        mHourWheel.invalidateWheel(false);
        mMinuteWheel.invalidateWheel(false);
    }

    /**
     * 设置渐变颜色，至少两个值,不需要渐变传相同的值
     *
     * @param pShadowsColors 渐变颜色数组
     */
    public void setShadowsColors(int[] pShadowsColors) {
        mHourWheel.setShadowsColors(pShadowsColors);
        mMinuteWheel.setShadowsColors(pShadowsColors);
    }

    public void setPickerBackgroundColor(@ColorInt int pColor) {
        mHourWheel.setWheelBackgroundColor(pColor);
        mMinuteWheel.setWheelBackgroundColor(pColor);
    }

    public void setPickerBackgroundResource(@DrawableRes int pResId) {
        mHourWheel.setWheelBackgroundResource(pResId);
        mMinuteWheel.setWheelBackgroundResource(pResId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setPickerBackground(Drawable pDrawable) {
        mHourWheel.setWheelBackground(pDrawable);
        mMinuteWheel.setWheelBackground(pDrawable);
    }

    /**
     * 获取时间字符串
     *
     * @return 时间字符串
     */
    public String getTimeString() {
        StringBuilder timeBuilder = new StringBuilder();
        int hour = mHourWheel.getCurrentItem() + 1;

        if (24 == hour) {
            timeBuilder.append("0");
        } else {
            timeBuilder.append(hour);
        }

        timeBuilder.append(":");
        timeBuilder.append(mMinuteWheel.getCurrentItem());

        return timeBuilder.toString();
    }
}
