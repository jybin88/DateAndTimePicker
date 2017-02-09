package com.lfh.custom.widget.picker.date;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lfh.custom.widget.R;
import com.lfh.custom.widget.picker.wheel.WheelView;
import com.lfh.custom.widget.picker.wheel.adapter.NumericWheelAdapter;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelChangedListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 日期选择器
 * Created by Administrator on 2017/1/16 0016.
 */
public class DatePicker extends LinearLayout {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";//默认时间格式化
    private static final int DEFAULT_START_YEAR = 1900;//默认开始的时间
    private static final int DEFAULT_END_YEAR = 2100;//默认结束时间
    private static final int MIN_MONTH = 1;
    private static final int MAX_MONTH = 12;
    private static final int MIN_DAY = 1;

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
     * The max day of the month
     */
    private int mMaxDayByMonth;

    private WheelView mYearWheel;
    private WheelView mMonthWheel;
    private WheelView mDayWheel;

    private NumericWheelAdapter mDayWheelAdapter;

    private int mSelectedYearIndex;
    private int mSelectedMonthIndex;
    private int mSelectedDayIndex;

    public DatePicker(Context context) {
        this(context, null);
    }

    public DatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.custom_picker_date_dateStyle);
    }

    public DatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 直接在XML中定义 > style定义                # 在layout.xml内直接写
        // >由defStyleAttr                          # 在对应的ThemeContext里的Theme内定义
        // 和defStyleRes指定的默认值                  # 在自定义view里指定
        // >直接在Theme中指定的值                     # 在对应的ThemeContext里的Theme内定义

        TypedArray a = context.obtainStyledAttributes(attrs, // LayoutInflater 传进来的值
                R.styleable.custom_picker_date, // 自定义的 styleable，事实上是一个数组
                defStyleAttr, // 主题里定义的 style
                R.style.custom_picker_date_dateDefaultStyle); // 默认的 style

        mItemColor = a.getColor(R.styleable.custom_picker_date_custom_picker_date_item_color, ContextCompat.getColor(context, R.color.custom_picker_default_color));
        mSelectedColor = a.getColor(R.styleable.custom_picker_date_custom_picker_date_item_selector_color, ContextCompat.getColor(context, R.color.custom_picker_selected_color));
        mItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        mSelectedItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_selected_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        mItemHeight = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_item_height, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_wheel_item_height));
        mVisibleItems = a.getInt(R.styleable.custom_picker_date_custom_picker_date_visible_count, DEF_VISIBLE_ITEMS);
        mCenterDrawableId = a.getResourceId(R.styleable.custom_picker_date_custom_picker_date_center_drawable, R.drawable.custom_picker_wheelview_item_center_bg);

        a.recycle();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        initWheelData();
        mItemWidth = (getScreenWidth() - 40) / 3;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 10;
        this.addView(mYearWheel, lp);
        this.addView(mMonthWheel, lp);
        this.addView(mDayWheel, lp);
    }

    /**
     * 初始化年、月、日数据
     */
    private void initWheelData() {
        initCurrentDate();
        mYearWheel = new WheelView(this.getContext());
        mMonthWheel = new WheelView(this.getContext());
        mDayWheel = new WheelView(this.getContext());
        //set visible items
        mYearWheel.setVisibleItems(mVisibleItems);
        mMonthWheel.setVisibleItems(mVisibleItems);
        mDayWheel.setVisibleItems(mVisibleItems);
        //set item height
        mYearWheel.setWheelItemHeight(mItemHeight);
        mMonthWheel.setWheelItemHeight(mItemHeight);
        mDayWheel.setWheelItemHeight(mItemHeight);
        //set item is cycle
        mYearWheel.setCyclic(mIsCycle);
        mMonthWheel.setCyclic(mIsCycle);
        mDayWheel.setCyclic(mIsCycle);
        //set item size
        mYearWheel.setWheelItemSize(mItemSize);
        mMonthWheel.setWheelItemSize(mItemSize);
        mDayWheel.setWheelItemSize(mItemSize);
        //set selected item size
        mYearWheel.setWheelSelectedItemSize(mSelectedItemSize);
        mMonthWheel.setWheelSelectedItemSize(mSelectedItemSize);
        mDayWheel.setWheelSelectedItemSize(mSelectedItemSize);
        //set item color
        mYearWheel.setWheelItemColor(mItemColor);
        mMonthWheel.setWheelItemColor(mItemColor);
        mDayWheel.setWheelItemColor(mItemColor);
        //set selected color
        mYearWheel.setWheelSelectedColor(mSelectedColor);
        mMonthWheel.setWheelSelectedColor(mSelectedColor);
        mDayWheel.setWheelSelectedColor(mSelectedColor);
        //set center drawable resource id
        mYearWheel.setCenterDrawableId(mCenterDrawableId);
        mMonthWheel.setCenterDrawableId(mCenterDrawableId);
        mDayWheel.setCenterDrawableId(mCenterDrawableId);

        mYearWheel.addChangingListener(mYearChangedListener);
        mMonthWheel.addChangingListener(mMonthChangedListener);

        NumericWheelAdapter yearWheelAdapter = new NumericWheelAdapter(this.getContext(), DEFAULT_START_YEAR, DEFAULT_END_YEAR);
        NumericWheelAdapter monthWheelAdapter = new NumericWheelAdapter(this.getContext(), MIN_MONTH, MAX_MONTH);
        mDayWheelAdapter = new NumericWheelAdapter(this.getContext(), MIN_DAY, mMaxDayByMonth);
        mYearWheel.setViewAdapter(yearWheelAdapter);
        mMonthWheel.setViewAdapter(monthWheelAdapter);
        mDayWheel.setViewAdapter(mDayWheelAdapter);

        //默认显示当前的日期
        mYearWheel.setCurrentItem(mSelectedYearIndex);
        mMonthWheel.setCurrentItem(mSelectedMonthIndex);
        mDayWheel.setCurrentItem(mSelectedDayIndex);
    }

    OnWheelChangedListener mYearChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateMonthDays();//2月闰年29天，平年28天
        }
    };

    OnWheelChangedListener mMonthChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateMonthDays();
        }
    };

    /**
     * 更新月份天数
     */
    private void updateMonthDays() {
        int finalDayIndex = mDayWheel.getCurrentItem();//天数最后选中的索引位置
        mMaxDayByMonth = getDaysByYearMonth(getYearValue(), mMonthWheel.getCurrentItem());

        if (finalDayIndex >= mMaxDayByMonth) {
            finalDayIndex = mMaxDayByMonth - 1;
        }

        mDayWheelAdapter.changeData(MIN_DAY, mMaxDayByMonth);
        mDayWheel.setCurrentItem(finalDayIndex);//默认位置为最后选中的位置
    }

    /**
     * 初始化当前日期
     */
    private void initCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        mSelectedYearIndex = year - DEFAULT_START_YEAR;
        mSelectedMonthIndex = month;
        mSelectedDayIndex = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        mMaxDayByMonth = getDaysByYearMonth(year, month);
    }

    /**
     * 根据年、月获取月份天数
     *
     * @param pYear  年份
     * @param pMonth 月份
     * @return 月份总天数
     */
    private int getDaysByYearMonth(int pYear, int pMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, pYear);
        calendar.set(Calendar.MONTH, pMonth);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);

        return calendar.get(Calendar.DATE);
    }

    /**
     * Set show date
     *
     * @param pYear  year
     * @param pMonth month
     * @param pDay   day
     */
    public void setShowDate(int pYear, int pMonth, int pDay) {
        mSelectedYearIndex = pYear - DEFAULT_START_YEAR;
        mSelectedMonthIndex = pMonth - 1;
        mSelectedDayIndex = pDay - 1;
        mYearWheel.setCurrentItem(mSelectedYearIndex);
        mMonthWheel.setCurrentItem(mSelectedMonthIndex);
        mDayWheel.setCurrentItem(mSelectedDayIndex);
    }

    /**
     * 返回日期字符串
     *
     * @return 日期字符串(yyyy-MM-dd)
     */
    @SuppressWarnings("JavaDoc")
    public String getDateString() {
        return getDateFormatString("");
    }

    /**
     * 返回日期字符串
     *
     * @param pDateFormat 日期格式化字符
     * @return 格式化后的日期字符串
     */
    public String getDateFormatString(String pDateFormat) {
        String formatString = DEFAULT_DATE_FORMAT;

        if (!TextUtils.isEmpty(pDateFormat)) {
            formatString = pDateFormat;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, getYearValue());
        calendar.set(Calendar.MONTH, mMonthWheel.getCurrentItem());
        calendar.set(Calendar.DATE, mDayWheel.getCurrentItem() + 1);

        return new SimpleDateFormat(formatString, Locale.CHINA).format(calendar.getTime());
    }

    private int getYearValue() {
        return mYearWheel.getCurrentItem() + DEFAULT_START_YEAR;
    }

    private int getScreenWidth() {
        return this.getContext().getResources().getDisplayMetrics().widthPixels;
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
        mYearWheel.setVisibleItems(pVisibleItems);
        mMonthWheel.setVisibleItems(pVisibleItems);
        mDayWheel.setVisibleItems(pVisibleItems);
    }

    /**
     * Set wheel cyclic flag
     *
     * @param pIsCycle the flag to set
     */
    @SuppressWarnings("unused")
    public void setCycle(boolean pIsCycle) {
        mIsCycle = pIsCycle;
        mYearWheel.setCyclic(pIsCycle);
        mMonthWheel.setCyclic(pIsCycle);
        mDayWheel.setCyclic(pIsCycle);
    }

    /**
     * Set item color
     *
     * @param pItemColor item color
     */
    @SuppressWarnings("unused")
    public void setItemColor(@ColorInt int pItemColor) {
        mItemColor = pItemColor;
        mYearWheel.setWheelItemColor(pItemColor);
        mMonthWheel.setWheelItemColor(pItemColor);
        mDayWheel.setWheelItemColor(pItemColor);
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
        mYearWheel.setWheelSelectedColor(pSelectedColor);
        mMonthWheel.setWheelSelectedColor(pSelectedColor);
        mDayWheel.setWheelSelectedColor(pSelectedColor);
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
        mYearWheel.setWheelItemSize(pItemSize);
        mMonthWheel.setWheelItemSize(pItemSize);
        mDayWheel.setWheelItemSize(pItemSize);
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
        mYearWheel.setWheelSelectedItemSize(pSelectedItemSize);
        mMonthWheel.setWheelSelectedItemSize(pSelectedItemSize);
        mDayWheel.setWheelSelectedItemSize(pSelectedItemSize);
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
        mYearWheel.setWheelItemHeight(pItemHeight);
        mMonthWheel.setWheelItemHeight(pItemHeight);
        mDayWheel.setWheelItemHeight(pItemHeight);
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
        mYearWheel.getLayoutParams().width = pItemWidth;
        mMonthWheel.getLayoutParams().width = pItemWidth;
        mDayWheel.getLayoutParams().width = pItemWidth;
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
        mYearWheel.setCenterDrawableId(mCenterDrawableId);
        mMonthWheel.setCenterDrawableId(mCenterDrawableId);
        mDayWheel.setCenterDrawableId(mCenterDrawableId);
        invalidateWheel();
    }

    /**
     * 设置渐变颜色，至少两个值,不需要渐变传相同的值
     *
     * @param pShadowsColors 渐变颜色数组
     */
    @SuppressWarnings("unused")
    public void setShadowsColors(int[] pShadowsColors) {
        mYearWheel.setShadowsColors(pShadowsColors);
        mMonthWheel.setShadowsColors(pShadowsColors);
        mDayWheel.setShadowsColors(pShadowsColors);
    }

    public void setPickerBackgroundColor(@ColorInt int pColor) {
        mYearWheel.setWheelBackgroundColor(pColor);
        mMonthWheel.setWheelBackgroundColor(pColor);
        mDayWheel.setWheelBackgroundColor(pColor);
    }

    public void setPickerBackgroundResource(@DrawableRes int pResId) {
        mYearWheel.setWheelBackgroundResource(pResId);
        mMonthWheel.setWheelBackgroundResource(pResId);
        mDayWheel.setWheelBackgroundResource(pResId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setPickerBackground(Drawable pDrawable) {
        mYearWheel.setWheelBackground(pDrawable);
        mMonthWheel.setWheelBackground(pDrawable);
        mDayWheel.setWheelBackground(pDrawable);
    }

    /**
     * 刷新界面
     */
    private void invalidateWheel() {
        mYearWheel.invalidateWheel(false);
        mMonthWheel.invalidateWheel(false);
        mDayWheel.invalidateWheel(false);
    }


    /**
     * 移除监听器
     * 在Activity onDestroy()调用，避免内存泄露
     */
    public void removeWheelChangedListener() {
        mYearWheel.removeChangingListener(mYearChangedListener);
        mMonthWheel.removeChangingListener(mMonthChangedListener);
    }
}
