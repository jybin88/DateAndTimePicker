package com.lfh.custom.widget.picker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lfh.custom.common.util.DimenUtil;
import com.lfh.custom.widget.picker.R;
import com.lfh.custom.widget.picker.UiUtil;
import com.lfh.custom.widget.picker.wheel.WheelView;
import com.lfh.custom.widget.picker.wheel.adapter.TextWheelAdapter;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelChangedListener;


/**
 * Created by lifuhai on 2017/2/7 0007.
 */
public class WheelActivity extends AppCompatActivity implements View.OnClickListener, OnWheelChangedListener {
    public static final String PARAM = "param";
    protected WheelView mWheelView;
    protected Button mNeutralButton;
    protected Button mNegativeButton;
    protected Button mPositiveButton;

    public static void start(Context context, WheelParam pParam) {
        Intent starter = new Intent(context, WheelActivity.class);
        starter.putExtra(PARAM, pParam);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abc_alert_dialog_material);
        findViewById(R.id.contentPanel).setVisibility(View.GONE);
        mNeutralButton = (Button) findViewById(android.R.id.button3);
        mNegativeButton = (Button) findViewById(android.R.id.button2);
        mPositiveButton = (Button) findViewById(android.R.id.button1);
        mNegativeButton.setText(android.R.string.cancel);
        mPositiveButton.setText(android.R.string.ok);
        mNegativeButton.setOnClickListener(this);
        mPositiveButton.setOnClickListener(this);

        mWheelView = new WheelView(this);
        mWheelView.addChangingListener(this);
        mWheelView.setWheelItemHeight(DimenUtil.dip2px(this, 46f));
        mWheelView.setWheelItemColor(ContextCompat.getColor(this, R.color.custom_picker_color4));
        mWheelView.setWheelSelectedColor(ContextCompat.getColor(this, R.color.custom_picker_color1));
        mWheelView.setWheelItemSize(getResources().getDimensionPixelSize(R.dimen.custom_picker_fontsize14));
        mWheelView.setWheelSelectedItemSize(getResources().getDimensionPixelSize(R.dimen.custom_picker_fontsize3));
        TextWheelAdapter wheelAdapter = new TextWheelAdapter(this);

        Intent intent = getIntent();

        if (intent.hasExtra(PARAM)) {
            WheelParam param = intent.getParcelableExtra(PARAM);

            if (null != param) {
                String title = param.getTitle();

                if (TextUtils.isEmpty(title)) {
                    findViewById(R.id.topPanel).setVisibility(View.GONE);
                } else {
                    findViewById(android.R.id.icon).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.alertTitle)).setText(title);
                }

                wheelAdapter.setData(param.getData());
                mWheelView.setViewAdapter(wheelAdapter);
                mWheelView.setCurrentItem(param.getSelectedPosition());
            }
        }

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ((FrameLayout) findViewById(R.id.custom)).addView(mWheelView, layoutParams);
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        wheel.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWheelView.removeChangingListener(this);
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
