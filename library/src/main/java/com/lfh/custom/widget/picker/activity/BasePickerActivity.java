package com.lfh.custom.widget.picker.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.lfh.custom.widget.picker.R;


/**
 * Created by lifuhai on 2017/2/7 0007.
 */
abstract class BasePickerActivity extends AppCompatActivity implements View.OnClickListener {

    protected Button mNeutralButton;
    protected Button mNegativeButton;
    protected Button mPositiveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_alert_dialog_material);
        findViewById(R.id.topPanel).setVisibility(View.GONE);
        findViewById(R.id.contentPanel).setVisibility(View.GONE);
        mNeutralButton = (Button) findViewById(android.R.id.button3);
        mNeutralButton.setVisibility(View.INVISIBLE);
        mNegativeButton = (Button) findViewById(android.R.id.button2);
        mPositiveButton = (Button) findViewById(android.R.id.button1);
        mNegativeButton.setText(android.R.string.cancel);
        mPositiveButton.setText(android.R.string.ok);
        mNegativeButton.setOnClickListener(this);
        mPositiveButton.setOnClickListener(this);
        createPickerView((FrameLayout) findViewById(R.id.custom));
    }

    protected abstract void createPickerView(FrameLayout root);

    @Override
    public void onClick(View v) {
        finish();
    }
}
