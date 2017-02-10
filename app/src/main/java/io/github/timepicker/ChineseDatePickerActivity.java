package io.github.timepicker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lfh.custom.widget.picker.date.ChineseDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 中式日期选择器demo
 * Created by Administrator on 2017/1/17 0017.
 */
public class ChineseDatePickerActivity extends AppCompatActivity {
    private ChineseDatePicker mDatePicker;
    private Button mBtnToggle;
    private TextView mTvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinese_date_picker);
        mDatePicker = (ChineseDatePicker) findViewById(R.id.dp_picker);
        mTvDate = (TextView) findViewById(R.id.tv_date);
        mBtnToggle = (Button) findViewById(R.id.btn_toggle);
        mBtnToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                String tag = mBtnToggle.getTag().toString();

                if ("n".equals(tag)) {
                    mBtnToggle.setText("切换农历");
                    mBtnToggle.setTag("g");
                    mDatePicker.showGregorian(true);
                } else if ("g".equals(tag)) {
                    mBtnToggle.setText("切换公历");
                    mBtnToggle.setTag("n");
                    mDatePicker.showGregorian(false);
                }
            }
        });

        findViewById(R.id.btn_show_time_millis).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                long timeMillis = mDatePicker.getDateTimeMillis();
                SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
                mTvDate.setText(sdr.format(new Date(timeMillis)));
            }
        });

        findViewById(R.id.btn_show_lunar_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText(mDatePicker.getLunarDateString());
            }
        });
        findViewById(R.id.btn_show_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText(mDatePicker.getGregorianDateString());
            }
        });

        findViewById(R.id.btn_format_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText(mDatePicker.getGregorianDateString("yyyy/MM/dd"));
            }
        });

        findViewById(R.id.btn_set_gregorian_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText("");
                mDatePicker.setGregorianDate(2017, 7, 10);
            }
        });

        findViewById(R.id.btn_set_lunar_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText("");
                mDatePicker.setLunarDate(2017, 7, 10);
            }
        });

        findViewById(R.id.btn_set_background).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mDatePicker.setPickerBackgroundColor(Color.WHITE);
            }
        });

        findViewById(R.id.btn_visiable_items).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setVisibleItems(3);
            }
        });

        findViewById(R.id.btn_change_recycle).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mDatePicker.setCycle(true);
            }
        });

        findViewById(R.id.btn_set_item_size).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setItemSize(16);
            }
        });

        findViewById(R.id.btn_set_selected_item_size).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setSelectedItemSize(25);
            }
        });

        findViewById(R.id.btn_set_item_color).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setItemColor(Color.BLUE);
            }
        });

        findViewById(R.id.btn_set_selected_item_color).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setSelectedColor(Color.RED);
            }
        });

        findViewById(R.id.btn_set_width).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setItemWidth(90);
            }
        });

        findViewById(R.id.btn_set_center_drawable).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mDatePicker.setCenterDrawableId(R.mipmap.item_center_bg);
            }
        });
    }
}
