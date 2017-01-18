package io.github.timepicker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.nd.ent.widget.time.TimePicker;


/**
 * 时间选择器Demo
 * Created by Administrator on 2017/1/17 0017.
 */
public class TimePickerActivity extends AppCompatActivity {
    private TextView mTvTime;
    private TimePicker mTimePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);
        mTimePicker = (TimePicker) findViewById(R.id.tp_picker);
        mTvTime = (TextView) findViewById(R.id.tv_time);

        findViewById(R.id.btn_show_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvTime.setText(mTimePicker.getTimeString());
            }
        });

        findViewById(R.id.btn_set_time).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTvTime.setText("");
                mTimePicker.setShowTime(20, 50);
            }
        });

        findViewById(R.id.btn_set_background).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTimePicker.setPickerBackgroundColor(Color.WHITE);
            }
        });

        findViewById(R.id.btn_visiable_items).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setVisibleItems(3);
            }
        });

        findViewById(R.id.btn_change_recycle).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View pView) {
                mTimePicker.setCycle(true);
            }
        });

        findViewById(R.id.btn_set_item_size).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setItemSize(16);
            }
        });

        findViewById(R.id.btn_set_selected_item_size).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setSelectedItemSize(25);
            }
        });

        findViewById(R.id.btn_set_item_color).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setItemColor(Color.BLUE);
            }
        });

        findViewById(R.id.btn_set_selected_item_color).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setSelectedColor(Color.RED);
            }
        });

        findViewById(R.id.btn_set_width).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setItemWidth(90);
            }
        });

        findViewById(R.id.btn_set_center_drawable).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View pView) {
                mTimePicker.setCenterDrawableId(R.mipmap.item_center_bg);
            }
        });
    }
}
