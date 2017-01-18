package io.github.timepicker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lfh.custom.widget.date.DatePicker;


/**
 * 日期选择器demo
 * Created by Administrator on 2017/1/17 0017.
 */
public class DatePickerActivity extends AppCompatActivity {
    DatePicker mDatePicker;
    private TextView mTvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        mDatePicker = (DatePicker) findViewById(R.id.dp_picker);
        mTvDate = (TextView) findViewById(R.id.tv_date);

        findViewById(R.id.btn_show_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText(mDatePicker.getDateString());
            }
        });

        findViewById(R.id.btn_format_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText(mDatePicker.getDateFormatString("yyyy/MM/dd"));
            }
        });

        findViewById(R.id.btn_set_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvDate.setText("");
                mDatePicker.setShowDate(2016, 10, 25);
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
