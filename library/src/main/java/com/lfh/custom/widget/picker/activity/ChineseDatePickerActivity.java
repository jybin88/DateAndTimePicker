package com.lfh.custom.widget.picker.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.lfh.custom.common.util.DimenUtil;
import com.lfh.custom.widget.picker.R;
import com.lfh.custom.widget.picker.UiUtil;
import com.lfh.custom.widget.picker.date.ChineseDatePicker;

import java.util.Calendar;

/**
 * 中式日期选择Activity
 * Created by lifuhai on 2017/2/9 0009.
 */
public class ChineseDatePickerActivity extends BasePickerActivity implements ChineseDatePicker.OnDateChangeListener {
    private static final String TAG = "ChinesePickerActivity";
    protected ChineseDatePicker mDatePicker;
    private RadioButton rbGregorian;
    private RadioButton rbLunar;

    public static void start(Context context) {
        Intent starter = new Intent(context, ChineseDatePickerActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void createPickerView(FrameLayout root) {
        LinearLayout llTopPanel = (LinearLayout) findViewById(R.id.topPanel);
        llTopPanel.setVisibility(View.VISIBLE);
        findViewById(R.id.title_template).setVisibility(View.GONE);
        View headView = View.inflate(this, R.layout.custom_picker_chinese_date_picker_view, null);
        rbGregorian = (RadioButton) headView.findViewById(R.id.rb_gregorian);
        rbLunar = (RadioButton) headView.findViewById(R.id.rb_lunar);
        rbGregorian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton pCompoundButton, boolean pB) {
                if (pB) {
                    mDatePicker.showGregorian(true);
                    rbGregorian.setChecked(true);
                    rbGregorian.setTextColor(ContextCompat.getColor(ChineseDatePickerActivity.this, R.color.custom_picker_color7));
                    rbGregorian.setBackgroundResource(R.drawable.custom_picker_btn_gregorian_focus);
                    rbLunar.setTextColor(ContextCompat.getColor(ChineseDatePickerActivity.this, R.color.custom_picker_color14));
                    rbLunar.setBackgroundResource(R.drawable.custom_picker_btn_lunar_normal);
                    rbLunar.setChecked(false);
                }
            }
        });
        rbLunar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton pCompoundButton, boolean pB) {
                if (pB) {
                    mDatePicker.showGregorian(false);
                    rbGregorian.setChecked(false);
                    rbGregorian.setTextColor(ContextCompat.getColor(ChineseDatePickerActivity.this, R.color.custom_picker_color14));
                    rbGregorian.setBackgroundResource(R.drawable.custom_picker_btn_gregorian_normal);
                    rbLunar.setTextColor(ContextCompat.getColor(ChineseDatePickerActivity.this, R.color.custom_picker_color7));
                    rbLunar.setBackgroundResource(R.drawable.custom_picker_btn_lunar_focus);
                    rbLunar.setChecked(true);
                }
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        lp.topMargin = DimenUtil.dip2px(this, 24f);
        llTopPanel.addView(headView, lp);

        mDatePicker = new ChineseDatePicker(this);
        mDatePicker.addOnDateChangeListener(this);
        mDatePicker.showGregorian(true);

        root.addView(mDatePicker, UiUtil.createSDPStandardParams(this));
    }

    @Override
    public void onChange(Calendar calendar, String pYear, String pMonth, String pDay) {
        Log.d(TAG, "onChange: year=[" + pYear + "], month=[" + pMonth + "], day=[" + pDay + "]");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatePicker.removeWheelChangedListener();
        mDatePicker.removeOnDateChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }
}
