package io.github.timepicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lfh.custom.widget.picker.activity.WheelActivity;
import com.lfh.custom.widget.picker.activity.WheelParam;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                startActivity(new Intent(MainActivity.this, DatePickerActivity.class));
            }
        });

        findViewById(R.id.btn_chinese_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                startActivity(new Intent(MainActivity.this, ChineseDatePickerActivity.class));
            }
        });

        findViewById(R.id.btn_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                startActivity(new Intent(MainActivity.this, TimePickerActivity.class));
            }
        });

        findViewById(R.id.btn_wheel_dialog_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                List<String> data = new ArrayList<>();
                data.add("运动距离/米");
                data.add("运动距离/秒");
                data.add("运动距离/千米");
                data.add("运动距离/组");
                data.add("运动距离/小时");
                WheelParam param = WheelParam.builder()
                        .setData(data)
                        .setTitle("Test")
                        .setSelectedPosition(0)
                        .build();
                WheelActivity.start(MainActivity.this, param);
            }
        });

        findViewById(R.id.btn_chinese_date_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View pView) {
                com.lfh.custom.widget.picker.activity.ChineseDatePickerActivity.start(MainActivity.this);
            }
        });
    }
}
