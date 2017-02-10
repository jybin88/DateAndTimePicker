package com.lfh.custom.widget.picker;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by lifuhai on 2017/2/10 0010.
 */
public class UiUtil {
    public UiUtil() {
        /* no-op */
    }

    /**
     * 屏幕宽度
     */
    public static int getScreenWidth(Context pContext) {
        return pContext.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
        return (int) px;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, @DimenRes int dpRes) {
        Resources r = context.getResources();
        return (int) r.getDimension(dpRes);
    }

    /**
     * 根据手机的分辨率从 px 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     */
    public static int sp2px(Context context, float spValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
        return (int) px;
    }

    @SuppressWarnings("ResourceType")
    @NonNull
    public static ViewGroup.MarginLayoutParams createSDPStandardParams(Context context) {
        final float density = context.getResources().getDisplayMetrics().density;
        final int marginLeft = Math.round(density * 8); // 8dp
        final int marginTop = Math.round(density * 24); // 24dp
        final int marginRight = Math.round(density * 8); // 8dp
        final int marginBottom = Math.round(density * 8); // 8dp
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        return params;
    }
}
