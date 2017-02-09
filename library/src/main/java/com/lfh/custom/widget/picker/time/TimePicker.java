package com.lfh.custom.widget.picker.time;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lfh.custom.widget.R;
import com.lfh.custom.widget.picker.wheel.WheelView;
import com.lfh.custom.widget.picker.wheel.adapter.NumericWheelAdapter;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelChangedListener;

import java.util.Calendar;
import java.util.HashSet;

/**
 * 时间选择器
 * Created by Administrator on 2017/1/17 0017.
 */
public class TimePicker extends LinearLayout {
    private static final int MIN_HOUR = 0;
    private static final int MAX_HOUR = 23;
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

    /**
     * Picker background color
     */
    private int mBackgroundColor = Color.TRANSPARENT;
    private int mSelectedHourIndex;
    private int mSelectedMinuteIndex;

    private WheelView mHourWheel;
    private WheelView mMinuteWheel;

    private HashSet<OnTimeChangeListener> mOnTimeChangeListeners = new HashSet<>();

    public TimePicker(Context context) {
        this(context, null);
    }

    public TimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.custom_picker_timeStyle);
    }

    public TimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 直接在XML中定义 > style定义                # 在layout.xml内直接写
        // >由defStyleAttr                          # 在对应的ThemeContext里的Theme内定义
        // 和defStyleRes指定的默认值                  # 在自定义view里指定
        // >直接在Theme中指定的值                     # 在对应的ThemeContext里的Theme内定义

        TypedArray a = context.obtainStyledAttributes(attrs, // LayoutInflater 传进来的值
                R.styleable.custom_picker_time, // 自定义的 styleable，事实上是一个数组
                defStyleAttr, // 主题里定义的 style
                R.style.custom_picker_timeDefaultStyle); // 默认的 style

        mItemColor = a.getColor(R.styleable.custom_picker_time_custom_picker_time_item_color, ContextCompat.getColor(context, R.color.custom_picker_default_color));
        mSelectedColor = a.getColor(R.styleable.custom_picker_time_custom_picker_time_item_selected_color, ContextCompat.getColor(context, R.color.custom_picker_selected_color));
        mItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_time_custom_picker_time_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        mSelectedItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_time_custom_picker_time_selected_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        mItemHeight = a.getDimensionPixelSize(R.styleable.custom_picker_time_custom_picker_time_item_height, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_wheel_item_height));
        mVisibleItems = a.getInt(R.styleable.custom_picker_time_custom_picker_time_visible_count, DEF_VISIBLE_ITEMS);
        mCenterDrawableId = a.getResourceId(R.styleable.custom_picker_time_custom_picker_time_center_drawable, R.drawable.custom_picker_wheelview_item_center_bg);
        mItemWidth = a.getDimensionPixelSize(R.styleable.custom_picker_time_custom_picker_time_item_width, (getScreenWidth() - 40) / 3);
        mBackgroundColor = a.getColor(R.styleable.custom_picker_time_custom_picker_time_bg_color, Color.TRANSPARENT);
        a.recycle();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        initWheelData();
        LayoutParams lp = new LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
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
        //set background color
        mHourWheel.setWheelBackgroundColor(mBackgroundColor);
        mMinuteWheel.setWheelBackgroundColor(mBackgroundColor);

        NumericWheelAdapter hourWheelAdapter = new NumericWheelAdapter(getContext(), MIN_HOUR, MAX_HOUR);
        NumericWheelAdapter secondWheelAdapter = new NumericWheelAdapter(getContext(), MIN_SECOND, MAX_SECOND);
        mHourWheel.setViewAdapter(hourWheelAdapter);
        mMinuteWheel.setViewAdapter(secondWheelAdapter);
        // set listeners
        mHourWheel.addChangingListener(mHourWheelChangedListener);
        mMinuteWheel.addChangingListener(mMinuteWheelChangedListener);
        //默认显示当前时间
        mHourWheel.setCurrentItem(mSelectedHourIndex);
        mMinuteWheel.setCurrentItem(mSelectedMinuteIndex);
    }

    private OnWheelChangedListener mHourWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            onTimeChange();
        }
    };

    private OnWheelChangedListener mMinuteWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            onTimeChange();
        }
    };

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
        mSelectedHourIndex = pHour;
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
        int hour = getHour();
        if (hour < 10) {
            timeBuilder.append("0"); // 03:03
        }
        timeBuilder.append(hour);
        timeBuilder.append(":");

        int minute = getMinute();
        if (minute < 10) {
            timeBuilder.append("0");
        }
        timeBuilder.append(minute);

        return timeBuilder.toString();
    }

    private int getMinute() {
        return mMinuteWheel.getCurrentItem();
    }

    private int getHour() {
        return mHourWheel.getCurrentItem();
    }

    /**
     * 移除监听
     */
    public void removeWheelChangedListener() {
        mHourWheel.removeChangingListener(mHourWheelChangedListener);
        mMinuteWheel.removeChangingListener(mMinuteWheelChangedListener);
    }

    private Handler mDebounceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.custom_picker_on_change) {
                for (OnTimeChangeListener nextListener : mOnTimeChangeListeners) {
                    nextListener.onChange((Calendar) msg.obj);
                }
            } else if (msg.what == R.id.custom_picker_on_change_debounce) {
                // 防止当快速滑动时会快速调用回调的问题
                mDebounceHandler.removeMessages(R.id.custom_picker_on_change);
                Message message = mDebounceHandler.obtainMessage(R.id.custom_picker_on_change);
                message.obj = msg.obj;
                mDebounceHandler.sendMessageDelayed(message, 500);
            }
        }
    };

    private void onTimeChange() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.HOUR_OF_DAY, getHour());
        calendar.set(Calendar.MINUTE, getMinute());
        Message message = mDebounceHandler.obtainMessage(R.id.custom_picker_on_change_debounce);
        message.obj = calendar;
        mDebounceHandler.sendMessage(message);
    }

    public void addOnDateChangeListener(OnTimeChangeListener listener) {
        if (listener == null) return;
        mOnTimeChangeListeners.add(listener);
    }

    public void removeOnDateChangeListener(OnTimeChangeListener listener) {
        if (listener == null) return;
        mOnTimeChangeListeners.remove(listener);
    }

    public interface OnTimeChangeListener {
        void onChange(Calendar date);
    }
}
