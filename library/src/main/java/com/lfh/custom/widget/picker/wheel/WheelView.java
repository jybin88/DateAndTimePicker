package com.lfh.custom.widget.picker.wheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.lfh.custom.widget.picker.R;
import com.lfh.custom.widget.picker.wheel.WheelScroller.ScrollingListener;
import com.lfh.custom.widget.picker.wheel.adapter.WheelViewAdapter;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelChangedListener;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelClickedListener;
import com.lfh.custom.widget.picker.wheel.listener.OnWheelScrollListener;

import java.util.LinkedList;
import java.util.List;


/**
 * Numeric wheel view.
 */
public class WheelView extends View {

    //白色的渐变效果，色值一样，透明度设置几种程度，从上到下的覆盖，数组第一个覆盖的颜色最深，所以显示的颜色越浅
    /**
     * Top and bottom shadows colors
     */
//    private static final int[] SHADOWS_COLORS = new int[]{0xddffffff,
//            0x99ffffff, 0x55ffffff, 0x00ffffff};

    //用黑色的测试，效果明显些
//    private static final int[] SHADOWS_COLORS = new int[]{0xcc000000,
//            0x88000000, 0x44000000};
    //透明渐变
    private static final int[] SHADOWS_COLORS = new int[]{0x00000000, 0x00000000};
    /**
     * Top and bottom items offset (to hide that)
     */
    private static final int ITEM_OFFSET_PERCENT = 10;

    /**
     * Left and right padding value
     */
    private static final int PADDING = 10;

    /**
     * Default count of visible items
     */
    private static final int DEF_VISIBLE_ITEMS = 5;

    // Wheel Values
    private int currentItem = 0;

    // Count of visible items
    private int visibleItems = DEF_VISIBLE_ITEMS;

    // Item height
    private int itemHeight = 0;

    // Center Line
    private Drawable centerDrawable;
    // Shadows drawables
    private GradientDrawable topShadow;
    private GradientDrawable bottomShadow;
    //2017.1.18 增加渐变颜色数组
    private int[] mShadowsColors = SHADOWS_COLORS;
    // Scrolling
    private WheelScroller scroller;
    private boolean isScrollingPerformed;
    private int scrollingOffset;

    // Cyclic
    boolean isCyclic = false;

    // Items layout
    private LinearLayout itemsLayout;

    // The number of first item in layout
    private int firstItem;

    // View adapter
    private WheelViewAdapter viewAdapter;

    // Recycle
    private WheelRecycle recycle = new WheelRecycle(this);

    // Listeners
    private List<OnWheelChangedListener> changingListeners = new LinkedList<>();
    private List<OnWheelScrollListener> scrollingListeners = new LinkedList<>();
    private List<OnWheelClickedListener> clickingListeners = new LinkedList<>();
    private int wheelItemColor;
    private int wheelSelectedColor;
    private int wheelItemSize;
    private int wheelSelectedItemSize;
    private int centerDrawableId;
    private int wheelItemHeight;
    //和内容之间的间距
    private int contentPadding;

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.custom_picker_wheelStyle);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // 直接在XML中定义 > style定义                # 在layout.xml内直接写
        // >由defStyleAttr                          # 在对应的ThemeContext里的Theme内定义
        // 和defStyleRes指定的默认值                  # 在自定义view里指定
        // >直接在Theme中指定的值                     # 在对应的ThemeContext里的Theme内定义

        TypedArray a = context.obtainStyledAttributes(attrs, // LayoutInflater 传进来的值
                R.styleable.custom_picker_wheel, // 自定义的 styleable，事实上是一个数组
                defStyle, // 主题里定义的 style
                R.style.custom_picker_wheelDefaultStyle); // 默认的 style

        wheelItemColor = a.getColor(R.styleable.custom_picker_wheel_custom_picker_wheel_item_color, ContextCompat.getColor(context, R.color.custom_picker_default_color));
        wheelSelectedColor = a.getColor(R.styleable.custom_picker_wheel_custom_picker_wheelSelectedColor, ContextCompat.getColor(context, R.color.custom_picker_selected_color));
        wheelItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_wheel_custom_picker_wheel_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        wheelSelectedItemSize = a.getDimensionPixelSize(R.styleable.custom_picker_wheel_custom_picker_wheel_selected_item_size, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_selected_text_size));
        wheelItemHeight = a.getDimensionPixelSize(R.styleable.custom_picker_wheel_custom_picker_wheel_item_height, context.getResources().getDimensionPixelSize(R.dimen.custom_picker_wheel_item_height));
        visibleItems = a.getInt(R.styleable.custom_picker_wheel_custom_picker_wheel_visible_count, DEF_VISIBLE_ITEMS);
        centerDrawableId = a.getResourceId(R.styleable.custom_picker_wheel_custom_picker_wheel_center_drawable, R.drawable.custom_picker_wheelview_item_center_bg);
        contentPadding = a.getDimensionPixelSize(R.styleable.custom_picker_wheel_custom_picker_wheel_content_padding, 0);
        centerDrawable = ContextCompat.getDrawable(context, centerDrawableId);

        a.recycle();

        initData();
    }

    /**
     * Initializes class data
     */
    private void initData() {
        scroller = new WheelScroller(getContext(), scrollingListener);
    }

    // Scrolling listener
    ScrollingListener scrollingListener = new ScrollingListener() {
        public void onStarted() {
            isScrollingPerformed = true;
            notifyScrollingListenersAboutStart();
        }

        public void onScroll(int distance) {
            doScroll(distance);

            int height = getHeight();
            if (scrollingOffset > height) {
                scrollingOffset = height;
                scroller.stopScrolling();
            } else if (scrollingOffset < -height) {
                scrollingOffset = -height;
                scroller.stopScrolling();
            }
        }

        public void onFinished() {
            if (isScrollingPerformed) {
                notifyScrollingListenersAboutEnd();
                isScrollingPerformed = false;
            }

            scrollingOffset = 0;
            invalidate();
        }

        public void onJustify() {
            if (Math.abs(scrollingOffset) > WheelScroller.MIN_DELTA_FOR_SCROLLING) {
                scroller.scroll(scrollingOffset, 0);
            }
        }
    };

    @SuppressWarnings("unused")
    public int getWheelItemColor() {
        return wheelItemColor;
    }

    @SuppressWarnings("unused")
    public void setWheelItemColor(int wheelItemColor) {
        this.wheelItemColor = wheelItemColor;
        if (viewAdapter != null) {
            viewAdapter.setTextColor(wheelItemColor);
        }
    }

    @SuppressWarnings("unused")
    public int getWheelItemHeight() {
        return wheelItemHeight;
    }

    @SuppressWarnings("unused")
    public void setWheelItemHeight(int wheelItemHeight) {
        this.wheelItemHeight = wheelItemHeight;
        if (viewAdapter != null) {
            viewAdapter.setItemHeight(wheelItemHeight);
        }
    }

    @SuppressWarnings("unused")
    public int getWheelItemSize() {
        return wheelItemSize;
    }

    @SuppressWarnings("unused")
    public void setWheelItemSize(int wheelItemSize) {
        this.wheelItemSize = wheelItemSize;
        if (viewAdapter != null) {
            viewAdapter.setTextSize(wheelItemSize);
        }

    }

    @SuppressWarnings("unused")
    public int getWheelSelectedItemSize() {
        return wheelSelectedItemSize;
    }

    @SuppressWarnings("unused")
    public void setWheelSelectedItemSize(int wheelSelectedItemSize) {
        this.wheelSelectedItemSize = wheelSelectedItemSize;

        if (viewAdapter != null) {
            viewAdapter.setSelectedTextSize(wheelSelectedItemSize);
        }
    }

    @SuppressWarnings("unused")
    public int getCenterDrawableId() {
        return centerDrawableId;
    }

    @SuppressWarnings("unused")
    public void setCenterDrawableId(int pCenterDrawableId) {
        centerDrawableId = pCenterDrawableId;
        centerDrawable = ContextCompat.getDrawable(getContext(), pCenterDrawableId);
    }

    @SuppressWarnings("unused")
    public int getWheelSelectedColor() {
        return wheelSelectedColor;
    }

    @SuppressWarnings("unused")
    public void setWheelSelectedColor(int wheelSelectedColor) {
        this.wheelSelectedColor = wheelSelectedColor;
        if (viewAdapter != null) {
            viewAdapter.setSelectedTextColor(wheelSelectedColor);
        }
    }

    /**
     * 设置渐变颜色，至少两个值,不需要渐变传相同的值
     *
     * @param pShadowsColors 渐变颜色数组
     */
    public void setShadowsColors(int[] pShadowsColors) {
        if (0 < pShadowsColors.length) {
            mShadowsColors = pShadowsColors;
            requestLayout();
        }
    }

    public void setWheelBackgroundColor(@ColorInt int pColor) {
        setBackgroundColor(pColor);
    }

    public void setWheelBackgroundResource(@DrawableRes int pResId) {
        setBackgroundResource(pResId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setWheelBackground(Drawable pDrawable) {
        setBackground(pDrawable);
    }

    /**
     * Set the the specified scrolling interpolator
     *
     * @param interpolator the interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        scroller.setInterpolator(interpolator);
    }

    /**
     * Gets count of visible items
     *
     * @return the count of visible items
     */
    @SuppressWarnings("unused")
    public int getVisibleItems() {
        return visibleItems;
    }

    /**
     * Sets the desired count of visible items.
     * Actual amount of visible items depends on wheel layout parameters.
     * To apply changes and rebuild view call measure().
     *
     * @param count the desired count for visible items
     */
    public void setVisibleItems(int count) {
        visibleItems = count;
        requestLayout();
    }

    /**
     * Gets view adapter
     *
     * @return the view adapter
     */
    public WheelViewAdapter getViewAdapter() {
        return viewAdapter;
    }

    // Adapter listener
    private DataSetObserver dataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidateWheel(false);
        }

        @Override
        public void onInvalidated() {
            invalidateWheel(true);
        }
    };

    /**
     * Sets view adapter. Usually new adapters contain different views, so
     * it needs to rebuild view by calling measure().
     *
     * @param viewAdapter the view adapter
     */
    public void setViewAdapter(WheelViewAdapter viewAdapter) {
        if (this.viewAdapter != null) {
            this.viewAdapter.unregisterDataSetObserver(dataObserver);
        }

        this.viewAdapter = viewAdapter;

        if (this.viewAdapter != null) {
            this.viewAdapter.registerDataSetObserver(dataObserver);
            this.viewAdapter.setTextSize(wheelItemSize);
            this.viewAdapter.setSelectedTextSize(wheelSelectedItemSize);
            this.viewAdapter.setTextColor(wheelItemColor);
            this.viewAdapter.setItemHeight(wheelItemHeight);
            this.viewAdapter.setCurrentIndex(currentItem);
        }

        invalidateWheel(true);
    }

    /**
     * Adds wheel changing listener
     *
     * @param listener the listener
     */
    @SuppressWarnings("unused")
    public void addChangingListener(OnWheelChangedListener listener) {
        changingListeners.add(listener);
    }

    /**
     * Removes wheel changing listener
     *
     * @param listener the listener
     */
    @SuppressWarnings("unused")
    public void removeChangingListener(OnWheelChangedListener listener) {
        changingListeners.remove(listener);
    }

    @SuppressWarnings("unused")
    public void removeAllListener() {
        changingListeners.clear();
    }

    /**
     * Notifies changing listeners
     *
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyChangingListeners(int oldValue, int newValue) {
        for (OnWheelChangedListener listener : changingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds wheel scrolling listener
     *
     * @param listener the listener
     */
    @SuppressWarnings("unused")
    public void addScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.add(listener);
    }

    /**
     * Removes wheel scrolling listener
     *
     * @param listener the listener
     */
    @SuppressWarnings("unused")
    public void removeScrollingListener(OnWheelScrollListener listener) {
        scrollingListeners.remove(listener);
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : scrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Adds wheel clicking listener
     *
     * @param listener the listener
     */
    @SuppressWarnings("unused")
    public void addClickingListener(OnWheelClickedListener listener) {
        clickingListeners.add(listener);
    }

    /**
     * Removes wheel clicking listener
     *
     * @param listener the listener
     */
    @SuppressWarnings("unused")
    public void removeClickingListener(OnWheelClickedListener listener) {
        clickingListeners.remove(listener);
    }

    /**
     * Notifies listeners about clicking
     *
     * @param item item
     */
    protected void notifyClickListenersAboutClick(int item) {
        for (OnWheelClickedListener listener : clickingListeners) {
            listener.onItemClicked(this, item);
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return currentItem;
    }

    /**
     * Sets the current item. Does nothing when index is wrong.
     *
     * @param index    the item index
     * @param animated the animation flag
     */
    public void setCurrentItem(int index, boolean animated) {
        if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
            return; // throw?
        }
        viewAdapter.setCurrentIndex(index);
        int itemCount = viewAdapter.getItemsCount();
        if (index < 0 || index >= itemCount) {
            if (isCyclic) {
                while (index < 0) {
                    index += itemCount;
                }
                index %= itemCount;
            } else {
                return; // throw?
            }
        }
        if (index != currentItem) {
            if (animated) {
                int itemsToScroll = index - currentItem;
                if (isCyclic) {
                    int scroll = itemCount + Math.min(index, currentItem) - Math.max(index, currentItem);
                    if (scroll < Math.abs(itemsToScroll)) {
                        itemsToScroll = itemsToScroll < 0 ? scroll : -scroll;
                    }
                }
                scroll(itemsToScroll, 0);
            } else {
                scrollingOffset = 0;

                int old = currentItem;
                currentItem = index;

                notifyChangingListeners(old, currentItem);

                invalidate();
            }
        }
    }

    /**
     * Sets the current item w/o animation. Does nothing when index is wrong.
     *
     * @param index the item index
     */
    public void setCurrentItem(int index) {
        setCurrentItem(index, false);
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
     *
     * @return true if wheel is cyclic
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * Set wheel cyclic flag
     *
     * @param isCyclic the flag to set
     */
    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
        invalidateWheel(false);
    }

    /**
     * Invalidates wheel
     *
     * @param clearCaches if true then cached views will be clear
     */
    public void invalidateWheel(boolean clearCaches) {
        if (clearCaches) {
            recycle.clearAll();
            if (itemsLayout != null) {
                itemsLayout.removeAllViews();
            }
            scrollingOffset = 0;
        } else if (itemsLayout != null) {
            // cache all items
            recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
        }

        invalidate();
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (topShadow == null) {
            topShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, mShadowsColors);
        }

        if (bottomShadow == null) {
            bottomShadow = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, mShadowsColors);
        }
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(LinearLayout layout) {
        if (layout != null && layout.getChildAt(0) != null) {
            itemHeight = layout.getChildAt(0).getMeasuredHeight();
        }
        int desired = itemHeight * visibleItems - itemHeight * ITEM_OFFSET_PERCENT / 50;

        return Math.max(desired, getSuggestedMinimumHeight());
    }

    /**
     * Returns height of wheel item
     *
     * @return the item height
     */
    private int getItemHeight() {
        if (itemHeight != 0) {
            return itemHeight;
        }
        if (itemsLayout != null && itemsLayout.getChildAt(0) != null) {
            itemHeight = itemsLayout.getChildAt(0).getHeight();
            return itemHeight;
        }

        return getHeight() / visibleItems;
    }

    /**
     * Calculates control width and creates text layouts
     *
     * @param widthSize the input layout width
     * @param mode      the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(int widthSize, int mode) {
        initResourcesIfNecessary();

        itemsLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        itemsLayout.measure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        int width = itemsLayout.getMeasuredWidth();

        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width += 2 * PADDING;

            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());

            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
            }
        }

        itemsLayout.measure(MeasureSpec.makeMeasureSpec(width - 2 * PADDING, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        return width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        buildViewForMeasuring();

        int width = calculateLayoutWidth(widthSize, widthMode);

        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(itemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout(r - l, b - t);
    }

    /**
     * Sets layouts width and height
     *
     * @param width  the layout width
     * @param height the layout height
     */
    private void layout(int width, int height) {
        int itemsWidth = width - 2 * PADDING;

        itemsLayout.layout(0, 0, itemsWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (viewAdapter != null && viewAdapter.getItemsCount() > 0) {
            updateView();

            drawItems(canvas);
            drawCenterRect(canvas);
        }

        drawShadows(canvas);
    }

    /**
     * Draws shadows on top and bottom of control
     *
     * @param canvas the canvas for drawing
     */
    private void drawShadows(Canvas canvas) {
//        int height = (int) (1.3 * getItemHeight());
        //2016.6.20修改，阴影全覆盖
        int height = getHeight() / 2;
        topShadow.setBounds(0, 0, getWidth(), height);
        topShadow.draw(canvas);

        bottomShadow.setBounds(0, getHeight() - height, getWidth(), getHeight());
        bottomShadow.draw(canvas);
    }

    /**
     * Draws items
     *
     * @param canvas the canvas for drawing
     */
    private void drawItems(Canvas canvas) {
        canvas.save();

        int top = (currentItem - firstItem) * getItemHeight() + (getItemHeight() - getHeight()) / 2;
        canvas.translate(PADDING, -top + scrollingOffset);

        itemsLayout.draw(canvas);

        canvas.restore();
    }

    /**
     * Draws rect for current value
     *
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(Canvas canvas) {
        int center = getHeight() / 2 + 2;
        int offset = getItemHeight() / 2;
        if (centerDrawable == null) {
            centerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.custom_picker_wheelview_item_center_bg);
        }
        centerDrawable.setBounds(contentPadding, center - offset, getWidth() - contentPadding, center + offset);
        centerDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || getViewAdapter() == null) {
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isScrollingPerformed) {
                    int distance = (int) event.getY() - getHeight() / 2;
                    if (distance > 0) {
                        distance += getItemHeight() / 2;
                    } else {
                        distance -= getItemHeight() / 2;
                    }
                    int items = distance / getItemHeight();
                    notifyClickListenersAboutClick(currentItem + items);
//		            if (items != 0 && isValidItemIndex(currentItem + items)) {
//		                notifyClickListenersAboutClick(currentItem + items);
//		            }
                }
                break;
            default:
                break;
        }

        return scroller.onTouchEvent(event);
    }

    /**
     * Scrolls the wheel
     *
     * @param delta the scrolling value
     */
    private void doScroll(int delta) {
        scrollingOffset += delta;

        int itemHeight = getItemHeight();
        int count = scrollingOffset / itemHeight;

        int pos = currentItem - count;
        int itemCount = viewAdapter.getItemsCount();

        int fixPos = scrollingOffset % itemHeight;
        if (Math.abs(fixPos) <= itemHeight / 2) {
            fixPos = 0;
        }
        if (isCyclic && itemCount > 0) {
            if (fixPos > 0) {
                pos--;
                count++;
            } else if (fixPos < 0) {
                pos++;
                count--;
            }
            // fix position by rotating
            while (pos < 0) {
                pos += itemCount;
            }
            pos %= itemCount;
        } else {
            //
            if (pos < 0) {
                count = currentItem;
                pos = 0;
            } else if (pos >= itemCount) {
                count = currentItem - itemCount + 1;
                pos = itemCount - 1;
            } else if (pos > 0 && fixPos > 0) {
                pos--;
                count++;
            } else if (pos < itemCount - 1 && fixPos < 0) {
                pos++;
                count--;
            }
        }

        int offset = scrollingOffset;
        if (pos != currentItem) {
            setCurrentItem(pos, false);
        } else {
            invalidate();
        }

        // update offset
        scrollingOffset = offset - count * itemHeight;
        if (scrollingOffset > getHeight()) {
            scrollingOffset = scrollingOffset % getHeight() + getHeight();
        }
    }

    /**
     * Scroll the wheel
     *
     * @param itemsToScroll items to scroll
     * @param time          scrolling duration
     */
    public void scroll(int itemsToScroll, int time) {
        int distance = itemsToScroll * getItemHeight() - scrollingOffset;
        scroller.scroll(distance, time);
    }

    /**
     * Calculates range for wheel items
     *
     * @return the items range
     */
    private ItemsRange getItemsRange() {
        if (getItemHeight() == 0) {
            return null;
        }

        int first = currentItem;
        int count = 1;

        while (count * getItemHeight() < getHeight()) {
            first--;
            count += 2; // top + bottom items
        }

        if (scrollingOffset != 0) {
            if (scrollingOffset > 0) {
                first--;
            }
            count++;

            // process empty items above the first or below the second
            int emptyItems = scrollingOffset / getItemHeight();
            first -= emptyItems;
            count += Math.asin(emptyItems);
        }
        return new ItemsRange(first, count);
    }

    /**
     * Rebuilds wheel items if necessary. Caches all unused items.
     *
     * @return true if items are rebuilt
     */
    private boolean rebuildItems() {
        boolean updated;
        ItemsRange range = getItemsRange();
        if (itemsLayout != null) {
            int first = recycle.recycleItems(itemsLayout, firstItem, range);
            updated = firstItem != first;
            firstItem = first;
        } else {
            createItemsLayout();
            updated = true;
        }
        if (range != null) {
            if (!updated) {
                updated = firstItem != range.getFirst() || itemsLayout.getChildCount() != range.getCount();
            }

            if (firstItem > range.getFirst() && firstItem <= range.getLast()) {
                for (int i = firstItem - 1; i >= range.getFirst(); i--) {
                    if (!addViewItem(i, true)) {
                        break;
                    }
                    firstItem = i;
                }
            } else {
                firstItem = range.getFirst();
            }

            int first = firstItem;
            for (int i = itemsLayout.getChildCount(); i < range.getCount(); i++) {
                if (!addViewItem(firstItem + i, false) && itemsLayout.getChildCount() == 0) {
                    first++;
                }
            }
            firstItem = first;
        }

        return updated;
    }

    /**
     * Updates view. Rebuilds items and label if necessary, recalculate items sizes.
     */
    private void updateView() {
        if (rebuildItems()) {
            calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            layout(getWidth(), getHeight());
        }
    }

    /**
     * Creates item layouts if necessary
     */
    private void createItemsLayout() {
        if (itemsLayout == null) {
            itemsLayout = new LinearLayout(getContext());
//            itemsLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            //测试打开
//            itemsLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            itemsLayout.setPadding(contentPadding, 0, contentPadding, 0);
            itemsLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    /**
     * Builds view for measuring
     */
    private void buildViewForMeasuring() {
        // clear all items
        if (itemsLayout != null) {
            recycle.recycleItems(itemsLayout, firstItem, new ItemsRange());
        } else {
            createItemsLayout();
        }

        // add views
        int addItems = visibleItems / 2;
        for (int i = currentItem + addItems; i >= currentItem - addItems; i--) {
            if (addViewItem(i, true)) {
                firstItem = i;
            }
        }
    }

    /**
     * Adds view for item to items layout
     *
     * @param index the item index
     * @param first the flag indicates if view should be first
     * @return true if corresponding item exists and is added
     */
    private boolean addViewItem(int index, boolean first) {
        View view = getItemView(index);
        if (view != null) {
            if (first) {
                itemsLayout.addView(view, 0);
            } else {
                itemsLayout.addView(view);
            }

            return true;
        }

        return false;
    }

    /**
     * Checks whether intem index is valid
     *
     * @param index the item index
     * @return true if item index is not out of bounds or the wheel is cyclic
     */
    private boolean isValidItemIndex(int index) {
        return viewAdapter != null && viewAdapter.getItemsCount() > 0 &&
                (isCyclic || index >= 0 && index < viewAdapter.getItemsCount());
    }

    /**
     * Returns view for specified item
     *
     * @param index the item index
     * @return item view or empty view if index is out of bounds
     */
    private View getItemView(int index) {
        if (viewAdapter == null || viewAdapter.getItemsCount() == 0) {
            return null;
        }
        int count = viewAdapter.getItemsCount();
        if (!isValidItemIndex(index)) {
            return viewAdapter.getEmptyItem(recycle.getEmptyItem(), itemsLayout);
        } else {
            while (index < 0) {
                index = count + index;
            }
        }

        index %= count;
        return viewAdapter.getItem(index, recycle.getItem(), itemsLayout);
    }

    /**
     * Stops scrolling
     */
    @SuppressWarnings("unused")
    public void stopScrolling() {
        scroller.stopScrolling();
    }
}
