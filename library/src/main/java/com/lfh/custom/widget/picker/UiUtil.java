package com.lfh.custom.widget.picker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by lifuhai on 2017/2/10 0010.
 */
public class UiUtil {
    public UiUtil() {
        /* no-op */
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
