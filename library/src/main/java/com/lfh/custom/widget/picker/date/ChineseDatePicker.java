package com.lfh.custom.widget.picker.date;

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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lfh.custom.common.util.ScreenUtil;
import com.lfh.custom.widget.picker.R;
import com.lfh.custom.widget.picker.calendar.CalendarUtil;
import com.lfh.custom.widget.picker.calendar.ChineseCalendar;
import com.lfh.custom.widget.picker.wheel.WheelView;
import com.lfh.custom.widget.picker.wheel.adapter.TextWheelAdapter;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelChangedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 中国式日期选择
 * Created by lifuhai on 2017/2/8 0008.
 */
@SuppressWarnings("WrongConstant")
public class ChineseDatePicker extends LinearLayout {
    private static final String TAG = "ChineseDatePicker";
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";//默认时间格式化
    private static final int YEAR_START = 1900; //起始年份
    private static final int YEAR_END = 2100; //结束年份
    private static final int YEAR_SPAN = YEAR_END - YEAR_START + 1;

    private static final int MONTH_START = 1; //起始月份
    private static final int MONTH_END = 12; //结束月份
    private static final int MONTY_SPAN = MONTH_END - MONTH_START + 1;

    private static final int DAY_START = 1; //起始天数
    /**
     * 是否公历  true 公历, false 农历
     */
    private boolean mIsGregorian = false;
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
    /**
     * Picker background color
     */
    private int mBackgroundColor = Color.TRANSPARENT;

    private WheelView mYearWheel;
    private WheelView mMonthWheel;
    private WheelView mDayWheel;

    private int mSelectedYearIndex;
    private int mSelectedMonthIndex;
    private int mSelectedDayIndex;
    private HashSet<OnDateChangeListener> mOnDateChangeListeners = new HashSet<>();

    private String[] mArrGregorianYear = new String[YEAR_SPAN]; //公历年份
    private String[] mArrLunarYear = new String[YEAR_SPAN];//农历年份
    private String[] mArrGregorianMonth = new String[MONTY_SPAN]; //公历月份
    private String[] mArrLunarMonth = new String[MONTY_SPAN]; //农历月份

    private ChineseCalendar mChineseCalendar;

    private TextWheelAdapter mYearWheelAdapter;
    private TextWheelAdapter mMonthWheelAdapter;
    private TextWheelAdapter mDayWheelAdapter;

    public ChineseDatePicker(Context context) {
        this(context, null);
    }

    public ChineseDatePicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.custom_picker_dateStyle);
    }

    public ChineseDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 直接在XML中定义 > style定义                # 在layout.xml内直接写
        // >由defStyleAttr                          # 在对应的ThemeContext里的Theme内定义
        // 和defStyleRes指定的默认值                  # 在自定义view里指定
        // >直接在Theme中指定的值                     # 在对应的ThemeContext里的Theme内定义

        TypedArray a = context.obtainStyledAttributes(attrs, // LayoutInflater 传进来的值
                R.styleable.custom_picker_date, // 自定义的 styleable，事实上是一个数组
                defStyleAttr, // 主题里定义的 style
                R.style.custom_picker_dateDefaultStyle); // 默认的 style

        mItemColor = a.getColor(R.styleable.custom_picker_date_custom_picker_date_item_color, ContextCompat.getColor(context, R.color.custom_picker_default_color));
        mSelectedColor = a.getColor(R.styleable.custom_picker_date_custom_picker_date_item_selected_color, ContextCompat.getColor(context, R.color.custom_picker_selected_color));
        mItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        mSelectedItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_selected_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        mItemHeight = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_item_height, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_wheel_item_height));
        mVisibleItems = a.getInt(R.styleable.custom_picker_date_custom_picker_date_visible_count, DEF_VISIBLE_ITEMS);
        mCenterDrawableId = a.getResourceId(R.styleable.custom_picker_date_custom_picker_date_center_drawable, R.drawable.custom_picker_wheelview_item_center_bg);
        mItemWidth = a.getDimensionPixelSize(R.styleable.custom_picker_date_custom_picker_date_item_width, (ScreenUtil.getScreenWidth(getContext()) - 40) / 3);
        mBackgroundColor = a.getColor(R.styleable.custom_picker_date_custom_picker_date_bg_color, Color.TRANSPARENT);

        a.recycle();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        initWheelData();
        LayoutParams lp = new LayoutParams(mItemWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 10;
        this.addView(mYearWheel, lp);
        this.addView(mMonthWheel, lp);
        this.addView(mDayWheel, lp);
    }

    private void initWheelData() {
        initYearData();
        initMonthData();
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
        //set background color
        mYearWheel.setWheelBackgroundColor(mBackgroundColor);
        mMonthWheel.setWheelBackgroundColor(mBackgroundColor);
        mDayWheel.setWheelBackgroundColor(mBackgroundColor);
        //set listener
        mYearWheel.addChangingListener(mYearWheelChangedListener);
        mMonthWheel.addChangingListener(mMonthWheelChangedListener);
        mDayWheel.addChangingListener(mDayWheelChangedListener);

        //init adapter
        mYearWheelAdapter = new TextWheelAdapter(getContext());
        mMonthWheelAdapter = new TextWheelAdapter(getContext());
        mDayWheelAdapter = new TextWheelAdapter(getContext());
        setAdapter();

        //默认显示当前的日期
        showDate();
    }

    private OnWheelChangedListener mYearWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateCalendarData();
            onDateChange();
        }
    };

    private OnWheelChangedListener mMonthWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            updateCalendarData();
            onDateChange();
        }
    };

    private OnWheelChangedListener mDayWheelChangedListener = new OnWheelChangedListener() {
        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue) {
            onDateChange();
        }
    };

    /**
     * 设置适配器
     */
    private void setAdapter() {
        //set data
        mYearWheelAdapter.setData(stringArrayToList(getYearData()));
        mMonthWheelAdapter.setData(stringArrayToList(getMonthData()));
        mDayWheelAdapter.setData(stringArrayToList(getDayData()));
        //set adapter
        mYearWheel.setViewAdapter(mYearWheelAdapter);
        mMonthWheel.setViewAdapter(mMonthWheelAdapter);
        mDayWheel.setViewAdapter(mDayWheelAdapter);
    }

    /**
     * 初始化当前日期
     */
    private void initCurrentDate() {
        mChineseCalendar = new ChineseCalendar(Calendar.getInstance());
        updateYearMonthDayIndex();
    }

    /**
     * 初始化农历年份
     */
    private void initYearData() {
        for (int i = 0; i < YEAR_SPAN; i++) {
            mArrGregorianYear[i] = String.valueOf(YEAR_START + i);
            mArrLunarYear[i] = String.format(getContext().getString(R.string.custom_picker_year_format), String.valueOf(YEAR_START + i));
//            mArrLunarYear[i] = CalendarUtil.getLunarNameOfYear(YEAR_START + i);
        }
    }

    /**
     * 获取月份数据
     *
     * @return 月份数据
     */
    private String[] getYearData() {
        return mIsGregorian ? mArrGregorianYear : mArrLunarYear;
    }

    /**
     * 初始化默认的月份数据(1-12个月)
     */
    private void initMonthData() {
        mArrGregorianMonth = getResources().getStringArray(R.array.custom_picker_month_array); //公历默认月份

        for (int i = 0; i < MONTY_SPAN; i++) {
            //农历默认月份
            mArrLunarMonth[i] = CalendarUtil.getLunarNameOfMonth(MONTH_START + i);
        }
    }

    /**
     * 根据年的月总数
     *
     * @param year 年份
     * @return 月总数
     */
    private int getMonthCount(int year) {
        if (mIsGregorian) {
            return mArrGregorianMonth.length;
        } else {
            int monthLeap = CalendarUtil.getMonthLeapByYear(year);

            if (0 == monthLeap) { //没有闰月
                return mArrLunarMonth.length;
            } else { //有闰月重新获取数据
                return CalendarUtil.getLunarMonthsNamesWithLeap(monthLeap).length;
            }
        }
    }

    /**
     * 获取月份数据，农历月份可能包含闰月
     *
     * @return 月份数据
     */
    private String[] getMonthData() {
        String[] newMonthData;//新月份数据

        if (mIsGregorian) {
            newMonthData = mArrGregorianMonth;
        } else {
            int monthLeap = CalendarUtil.getMonthLeapByYear(mChineseCalendar.get(ChineseCalendar.CHINESE_YEAR));

            if (0 == monthLeap) { //没有闰月
                newMonthData = mArrLunarMonth;
            } else { //有闰月重新获取数据
                newMonthData = CalendarUtil.getLunarMonthsNamesWithLeap(monthLeap);
            }
        }

        return newMonthData;
    }

    /**
     * 获取每月最大天数
     *
     * @return 最大天数
     */
    private int getDaysByYearMonth() {
        int days;

        if (mIsGregorian) { //公历
            days = CalendarUtil.getSumOfDayInMonthForGregorianByMonth(mChineseCalendar.get(ChineseCalendar.CHINESE_YEAR), mChineseCalendar.get(ChineseCalendar.CHINESE_MONTH));
        } else { //农历
            days = CalendarUtil.getSumOfDayInMonthForLunarByMonthLunar(mChineseCalendar.get(ChineseCalendar.CHINESE_YEAR), mChineseCalendar.get(ChineseCalendar.CHINESE_MONTH));
        }

        return days;
    }

    /**
     * 获取天数数据
     *
     * @return 天数
     */
    private String[] getDayData() {
        mMaxDayByMonth = getDaysByYearMonth();
        int daySpan = mMaxDayByMonth - DAY_START + 1;
        String[] dayData = new String[daySpan];

        for (int i = 0; i < daySpan; i++) {
            if (mIsGregorian) { //公历
                dayData[i] = String.format(getContext().getString(R.string.custom_picker_day_format), String.valueOf(DAY_START + i));
            } else { //农历
                dayData[i] = CalendarUtil.getLunarNameOfDay(DAY_START + i);
            }
        }

        return dayData;
    }

    /**
     * 更新日历数据
     *
     * @param pYear        年
     * @param pMonthSway   月，公历农历均从1开始。农历如果有闰月，按照实际的顺序添加
     * @param pDay         日，从1开始，日期在月份中的显示数值
     * @param pIsGregorian 是否公历 true 公历 false 农历
     */
    private void updateChineseCalendar(int pYear, int pMonthSway, int pDay, boolean pIsGregorian) {
        if (pIsGregorian) { //公历
            mChineseCalendar = new ChineseCalendar(pYear, pMonthSway - 1, pDay); //公历日期构造方法
        } else { //农历
            int month = CalendarUtil.convertMonthSwayToMonthLunarByYear(pMonthSway, pYear);
            mChineseCalendar = new ChineseCalendar(true, pYear, month, pDay);
        }
    }

    /**
     * 切换公历、农历
     *
     * @param pIsGregorian true 公历 false 农历
     */
    public void showGregorian(boolean pIsGregorian) {
        updateChineseCalendar(getYearValue(), mMonthWheel.getCurrentItem() + 1, mDayWheel.getCurrentItem() + 1, mIsGregorian);
        mIsGregorian = pIsGregorian;
        setAdapter();
        updateYearMonthDayIndex();
        //默认显示选中的日期
        showDate();
    }

    /**
     * 更新年、月、日游标、月份天数
     */
    private void updateYearMonthDayIndex() {
        mSelectedYearIndex = mChineseCalendar.get(ChineseCalendar.YEAR) - YEAR_START;

        if (mIsGregorian) { //公历
            mSelectedMonthIndex = mChineseCalendar.get(ChineseCalendar.MONTH);
            mSelectedDayIndex = mChineseCalendar.get(ChineseCalendar.DAY_OF_MONTH) - 1;
        } else { //农历
            int monthLeap = CalendarUtil.getMonthLeapByYear(mChineseCalendar.get(ChineseCalendar.CHINESE_YEAR));

            if (monthLeap == 0) {
                mSelectedMonthIndex = mChineseCalendar.get(ChineseCalendar.CHINESE_MONTH) - 1;
            } else {
                mSelectedMonthIndex = CalendarUtil.convertMonthLunarToMonthSway(mChineseCalendar.get(ChineseCalendar.CHINESE_MONTH), monthLeap) - 1;
            }

            mSelectedDayIndex = mChineseCalendar.get(ChineseCalendar.CHINESE_DATE) - 1;
        }

        mMaxDayByMonth = getDaysByYearMonth();
    }

    /**
     * 显示日期
     */
    private void showDate() {
        mYearWheel.setCurrentItem(mSelectedYearIndex);
        mMonthWheel.setCurrentItem(mSelectedMonthIndex);
        mDayWheel.setCurrentItem(mSelectedDayIndex);
    }

    private int getYearValue() {
        return mYearWheel.getCurrentItem() + YEAR_START;
    }

    /**
     * 获取年份字符串
     *
     * @return 年份字符串
     */
    private String getYearString() {
        int index = mYearWheel.getCurrentItem();

        return mYearWheelAdapter.getItemText(index).toString();
    }

    /**
     * 获取月份字符串
     *
     * @return 月份字符串
     */
    private String getMonthString() {
        int index = mMonthWheel.getCurrentItem();

        return mMonthWheelAdapter.getItemText(index).toString();
    }

    private String getDayString() {
        int index = mDayWheel.getCurrentItem();

        return mDayWheelAdapter.getItemText(index).toString();
    }

    /**
     * 返回公历日期字符串
     *
     * @return 日期字符串(yyyy-MM-dd)
     */
    @SuppressWarnings("JavaDoc")
    public String getGregorianDateString() {
        return getGregorianDateString("");
    }

    /**
     * 返回公历日期字符串
     *
     * @param pDateFormat 日期格式化字符
     * @return 格式化后的日期字符串
     */
    public String getGregorianDateString(String pDateFormat) {
        String formatString = DEFAULT_DATE_FORMAT;

        if (!TextUtils.isEmpty(pDateFormat)) {
            formatString = pDateFormat;
        }

        int year = getYearValue();
        int month = mMonthWheel.getCurrentItem() + 1;
        int day = mDayWheel.getCurrentItem() + 1;

        ChineseCalendar calendar;

        if (mIsGregorian) {
            calendar = new ChineseCalendar(year, month - 1, day);
        } else {
            calendar = new ChineseCalendar(true, year, month, day);
        }

        return new SimpleDateFormat(formatString, Locale.CHINESE).format(calendar.getTime());
    }

    /**
     * 返回农历日期字符串
     *
     * @return 农历日期字符串
     */
    public String getLunarDateString() {
        return String.format(getResources().getString(R.string.custom_picker_year_format), String.valueOf(mChineseCalendar.get(ChineseCalendar.YEAR))) +
                mChineseCalendar.getChinese(ChineseCalendar.CHINESE_MONTH) +
                mChineseCalendar.getChinese(ChineseCalendar.CHINESE_DATE);
    }

    /**
     * 返回标准格林尼治时间下日期时间对应的时间戳
     *
     * @return 标准格林尼治时间下日期时间对应的时间戳
     */
    public long getDateTimeMillis() {
        int year = getYearValue();
        int month = mMonthWheel.getCurrentItem() + 1;
        int day = mDayWheel.getCurrentItem() + 1;

        if (mIsGregorian) {
            ChineseCalendar chineseCalendar = new ChineseCalendar(year, month - 1, day);//直接使用这句后去获取时间戳跟农历状态下获取的时间戳转换后会差一天
            year = chineseCalendar.get(ChineseCalendar.CHINESE_YEAR);
            month = chineseCalendar.get(ChineseCalendar.CHINESE_MONTH);
            day = chineseCalendar.get(ChineseCalendar.CHINESE_DATE);
        }

        ChineseCalendar calendar = new ChineseCalendar(true, year, month, day);

        long unixTime = calendar.getTimeInMillis(); //获取当前时区下日期时间对应的时间戳
        long unixTimeGMT = unixTime - TimeZone.getDefault().getRawOffset(); //获取标准格林尼治时间下日期时间对应的时间戳

        Log.d(TAG, "unixTime(" + unixTime + ")" + "|" + "unixTimeGMT(" + unixTimeGMT + ")");

        return unixTimeGMT;
    }


    /**
     * 设置公历日期
     * <p>
     * 设置农历用setLunarDate(int pYear, int pMonth, int pDay)
     *
     * @param pYear  year
     * @param pMonth month
     * @param pDay   day
     */
    public void setGregorianDate(int pYear, int pMonth, int pDay) {
        updateChineseCalendar(pYear, pMonth, pDay, true);
        updateYearMonthDayIndex();
        //显示选择的日期
        showDate();
    }

    /**
     * 设置农历日期
     * <p>
     * 设置公历用setGregorianDate(int pYear, int pMonth, int pDay)
     *
     * @param pYear  year
     * @param pMonth month
     * @param pDay   day
     */
    public void setLunarDate(int pYear, int pMonth, int pDay) {
        int monthLeap = Math.abs(CalendarUtil.getMonthLeapByYear(pYear)); //获取年份的闰月，没有闰月值为0

        if (monthLeap < pYear) { //如果闰月比传入的月份小，传入月份加1才是正确月份
            pMonth = pMonth + 1;
        }

        updateChineseCalendar(pYear, pMonth, pDay, false);
        updateYearMonthDayIndex();
        //显示选择的日期
        showDate();
    }

    /**
     * 更新日历数据
     */
    private void updateCalendarData() {
        int finalMonthIndex = mMonthWheel.getCurrentItem(); //上次月份选中的位置
        int finalDayIndex = mDayWheel.getCurrentItem(); //上次日期选中的位置
        int year = mYearWheel.getCurrentItem() + YEAR_START; //选中的年
        int monthCount = getMonthCount(year); //重新获取月份总数，农历可能有闰月

        if (finalMonthIndex >= monthCount) {
            //上次选中为最后一个月份，且月份总数大于当前选中的年，当前最后一个月份需要减1
            finalMonthIndex = monthCount - 1;
        }

        int month = finalMonthIndex + 1; //选中的月份
        int day = mDayWheel.getCurrentItem() + 1;
        updateChineseCalendar(year, month, day, mIsGregorian);
        mMaxDayByMonth = getDaysByYearMonth(); //总天数

        if (finalDayIndex >= mMaxDayByMonth) {
            //上次选中的为月的最后一天，且天数大于当前选中的月的天数，当前最后的天数需要减1
            finalDayIndex = mMaxDayByMonth - 1;
        }

        setAdapter();
        mMonthWheel.setCurrentItem(finalMonthIndex);
        mDayWheel.setCurrentItem(finalDayIndex);
    }

    /**
     * String数组转List
     *
     * @param pArray String数组
     * @return List
     */
    private List<String> stringArrayToList(String[] pArray) {
        List<String> strings = new ArrayList<>();
        Collections.addAll(strings, pArray);

        return strings;
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
        mYearWheel.setVisibleItems(pVisibleItems);
        mMonthWheel.setVisibleItems(pVisibleItems);
        mDayWheel.setVisibleItems(pVisibleItems);
    }

    /**
     * Set wheel cyclic flag
     *
     * @param pIsCycle the flag to set
     */
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
     * 移除监听
     */
    public void removeWheelChangedListener() {
        mYearWheel.removeChangingListener(mYearWheelChangedListener);
        mMonthWheel.removeChangingListener(mMonthWheelChangedListener);
        mDayWheel.removeChangingListener(mDayWheelChangedListener);
    }

    private Handler mDebounceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == R.id.custom_picker_on_change) {
                for (ChineseDatePicker.OnDateChangeListener nextListener : mOnDateChangeListeners) {
                    nextListener.onChange((Calendar) msg.obj, getYearString(), getMonthString(), getDayString());
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

    private void onDateChange() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, getYearValue());
        calendar.set(Calendar.MONTH, mMonthWheel.getCurrentItem());
        calendar.set(Calendar.DATE, mDayWheel.getCurrentItem() + 1);
        Message message = mDebounceHandler.obtainMessage(R.id.custom_picker_on_change_debounce);
        message.obj = calendar;
        mDebounceHandler.sendMessage(message);
    }

    public void addOnDateChangeListener(ChineseDatePicker.OnDateChangeListener listener) {
        if (listener == null) return;
        mOnDateChangeListeners.add(listener);
    }

    public void removeOnDateChangeListener(ChineseDatePicker.OnDateChangeListener listener) {
        if (listener == null) return;
        mOnDateChangeListeners.remove(listener);
    }

    public interface OnDateChangeListener {
        void onChange(Calendar date, String pYear, String pMonth, String pDay);
    }
}
