# TimePicker、DatePicker 

时间和日期选择器

---

开发者：lifh

本库提供时间选择、日期选择功能，主要功能：
---------------------

 - 获取选中的日期
 - 获取选中的时间

可自定义选项
----------
 - 更改背景色
 - 设置是否可循环
 - 更改可见数量
 - 更改默认字体颜色
 - 更改默认字体大小
 - 更改选中字体颜色
 - 更改选中字体大小
 - 更改宽度
 - 更新选中的背景图片

获取选中的日期、时间
----------

> 1.日期选择器
```java
getDateString(); //返回格式为yyyy-MM-dd
getDateFormatString(String pDateFormat) //返回自定义格式(pDateFormat)的日期
```
> 2.时间选择器
```java
getTimeString(); //返回格式为HH:mm
```
显示任意日期、时间
-----------

> 日期选择器
```java
setShowDate(int pYear, int pMonth, int pDay)
```
> 时间选择器
```java
setShowTime(int pHour, int pMinute)
```
使用方法：
-----

> 1.直接在布局文件中定义

日期选择器
```xml
<com.nd.ent.widget.DatePicker
    android:id="@+id/dp_picker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```    
时间选择器
```xml
<com.nd.ent.widget.TimePicker
    android:id="@+id/tp_picker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```
> 2.直接在代码中创建相应的实例使用

日期选择器 :
```java
DatePicker datePicker = new DatePicker(this);
```

时间选择器:
 ```java
TimePicker timePicker = new TimePicker(this);
```

属性设置：
-----

> 1.代码动态设置（日期选择器，时间选择器都使用下面的方法）

 - 设置背景色：
 ```java
setPickerBackgroundColor(@ColorInt int pColor)
setPickerBackgroundResource(@DrawableRes int pResId)`
setPickerBackground(Drawable pDrawable)
```

 - 设置可见的选项数量：
```java
setVisibleItems(int pVisibleItems)
```

 - 是否循环（只能动态进行设置，默认不循环）
```java
setCycle(boolean pIsCycle)
```

 - 默认字体大小
```java
setItemSize(int pItemSize)
```
  
 - 选中的字体大小
```java
setSelectedItemSize(int pSelectedItemSize)
```

 - 默认字体颜色
```java
setItemColor(@ColorInt int pItemColor)
```
 
 - 选中的字体颜色

```java
setSelectedColor(@ColorInt int pSelectedColor)
```

 - 宽度

```java
setItemWidth(int pItemWidth)
```

 - 选中的背景图片

```java
setCenterDrawableId(@DrawableRes int pCenterDrawableId)
```
 

> 2.XML中静态设置

在布局根节点添加 
```xml
xmlns:picker="http://schemas.android.com/apk/res-auto"
```

通过下面属性进行设置

 - 日期选择器
```java
picker:ent_picker_date_bg_color="#ffffff" //背景色

picker:ent_picker_date_center_drawable="@drawable/item_center_bg" // 选中的背景图片

picker:ent_picker_date_item_color="#ff0000" //默认字体颜色

picker:ent_picker_date_item_selected_color="#0000ff" //选中字体颜色

picker:ent_picker_date_item_size="@dimen/default_size" //默认字体大小(必须在dimen文件中定义相应的值)

picker:ent_picker_date_item_width="90dp" //宽度

picker:ent_picker_date_selected_item_size="@dimen/selected_size" //选中字体大小(必须在dimen文件中定义相应的值)

picker:ent_picker_date_visible_count="3" //可见的选项数量
```
 - 时间选择器
```java
picker:ent_picker_time_bg_color="#ffffff"  //背景色

picker:ent_picker_time_center_drawable="@drawable/item_center_bg" // 选中的背景图片

picker:ent_picker_time_item_color="#ff0000" //默认字体颜色

picker:ent_picker_time_item_selected_color="#0000ff" //选中字体颜色

picker:ent_picker_time_item_size="@dimen/default_size" //默认字体大小(必须在dimen文件中定义相应的值)

picker:ent_picker_time_item_width="90dp" //宽度

picker:ent_picker_time_selected_item_size="@dimen/selected_size" //选中字体大小(必须在dimen文件中定义相应的值)

picker:ent_picker_time_visible_count="3" //可见的选项数量
```
> 3.通过样式进行设置

 - 日期选择器
```xml
<com.nd.ent.widget.DatePicker 
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@style/date_picker_style"/>
```

或在全局样式中使用

```xml
<style name="AppTheme1" parent="Theme.AppCompat.Light.DarkActionBar">
    <!-- 指定全局样式. -->
    <item name="ent_picker_dateStyle">@style/date_picker_style</item>
</style>
```

如果同时使用了上面两种方式，优先使用style="@style/date_picker_style"

date_picker_style样式参考
```xml
<style name="date_picker_style" parent="ent_picker_dateDefaultStyle">
    <item name="ent_picker_date_item_color">#ff0000</item>
    <item name="ent_picker_date_item_selected_color">#00ff00</item>
    <item name="ent_picker_date_item_size">@dimen/date_picker_text_size</item>
    <item name="ent_picker_date_selected_item_size">@dimen/date_picker_selected_text_size</item>
    <item name="ent_picker_date_item_height">50sp</item>
    <item name="ent_picker_date_visible_count">3</item>
    <item name="ent_picker_date_center_drawable">@drawable/item_center_bg</item>
</style>
```
 - 时间选择器
```xml
<com.nd.ent.widget.TimePicker 
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        style="@style/time_picker_style" />
```

或在全局样式中使用

```xml
<style name="AppTheme1" parent="Theme.AppCompat.Light.DarkActionBar">
    <!-- 指定全局样式. -->
    <item name="ent_picker_timeStyle">@style/time_picker_style</item>
</style>
```

如果同时使用了上面两种方式，优先使用style="@style/time_picker_style"

time_picker_style样式参考
```xml
<style name="time_picker_style" parent="ent_picker_timeDefaultStyle">
    <item name="ent_picker_time_item_color">#ff0000</item>
    <item name="ent_picker_time_item_selected_color">#00ff00</item>
    <item name="ent_picker_time_item_size">@dimen/time_picker_text_size</item>
    <item name="ent_picker_time_selected_item_size">@dimen/time_picker_selected_text_size</item>
    <item name="ent_picker_time_item_height">50sp</item>
    <item name="ent_picker_time_visible_count">3</item>
    <item name="ent_picker_time_center_drawable">@drawable/item_center_bg</item>
</style>
```

本库依赖
----------

 - com.android.support:support-annotations
 - com.android.support:support-v4
 - com.android.support:appcompat-v7
