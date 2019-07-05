## About Toolbar
**Toolbar**是一个ToolBar的扩展工具类，省去了对不同版本适配的复杂方案，它可以帮助你轻松实现`NavigationBar`和`StatusBar`的样式管理，最最重要的是它的使用方式及其接近原生，大家快来试用吧！
## 起因
按照官方的方式去管理`NavigationBar`和`StatusBar`显示对各个系统版本的兼容性是很麻烦的。尤其在一些应用中可能不同的页面对应了不同显示状况，比如A页面`NavigationBar`需要显示成蓝色`StatusBar`需要显示成深蓝，而到了B页面`NavigationBar`和`StatusBar`却需要显示成白色。这时候你需要在不同的页面通过Code调过来调过去麻烦得很。那么我们为什么不做一个款仅需要在xml文件中设置几个属性就能完成各种样式适配的`Toolbar`呢？OK，那我们来定下目标吧！
	1. 适配各API版本但不需要那么麻烦的去编写个API对应的styles文件。
	2. 使用方式简单，接近原生。
	3. 仅需要布局文件，不需要在个页面维护代码逻辑。
针对这写要求，下面我们来实现一款自定义**Toolbar**.
## 原理
**Toolbar**的原理很简单，既然个系统版本需要兼容Statusbar等才能做到效果一致，那我们就不要Statusbar好了！
首先，将`StatusBar`设置成透明，并且让页面布局可以延伸到`StatusBar`下。这可以通过全局`style`实现：
```
<style name="AppTheme" parent="Theme.MaterialComponents.NoActionBar">
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:statusBarColor">@android:color/transparent</item>
</style>
```
现在我们的布局已经延伸到(0，0)的起点了，接下来怎么办呢？我们肯定不能在每个页面布局里写一个适配`StatusBar`的布局吧，那我们可以考虑把它放到自定义的view控件中。接下来我们可以自定义一个`Toolbar`的控件继承自`androidx.appcompat.widget.Toolbar`。那么接下来的问题就转化成了：
	1. 保持`Toolbar`的原有特性和使用方法，因为我们view是集成来的，所以这点肯定是满足的；
	2. 该自定义`Toolbar`如何适配`StatusBar`部分；
	3. 系统`Toolbar`中各个内置控件的布局是通过私有方法计算之后显示出来的，我们如何调整到跟原来的显示一模一样。
下面我们来将这些问题一一解决掉（第一条略）。
`Toolbar`适配系统栏部分，我们可以考虑重写`onDraw()`方法，绘制一个可自定义颜色的矩形区域，并且保证该区域的高度等于系统栏高度就可以了。请看代码实现：
```(构造函数中使用)
//在构造函数中使用
private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = 
            	context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
            color = typedArray.getColor(
            	R.styleable.Toolbar_statusBarColor,
            	getResources().getColor(R.color.colorPrimaryDark));
        }
        int id = context.getResources()
        	.getIdentifier("status_bar_height","dimen","android");
        statusBarHeight = context.getResources()
        	.getDimensionPixelOffset(id);
        paint = new Paint();
        paint.setColor(color);
}
```
```(重写onDraw)
@Override
protected void onDraw(Canvas canvas) {
    Rect rect = 
      new Rect(0, 0, getMeasuredWidth(), (int) statusBarHeight);
    canvas.drawRect(rect, paint);
    super.onDraw(canvas);
}
```
这个时候系统栏的背景就搞定了，运行下看下什么效果吧。
运行完毕后，你会发现虽然系统栏背景颜色变了，但是我们的`Toolbar`就这么高，整个title都移到上面去了，怎么办呢？重写`onMeasure()`方法，让我们的`Toolbar`的高度变成：原高度+系统栏高度。
```
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    //为了兼容6.0及以前版本多次measure、layout问题
    measuredHeight = 
      measuredHeight == 0? getMinimumHeight() : measuredHeight;
    setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), 
      measuredHeight +statusBarHeight);
}
```
再次运行，不错高度变了，但是为什么Title、icon之类的控件都快顶到系统栏了！看来我们还需要修改下`onLayout()`方法了。为什么是`onLayout`方法呢？是因为我们这些操作对控件的大小不会产生影响，只会对这些控件在`Toolbar`上的布局位置产生影响，所以我们需要重写下这个方法来调整内部控件的位置。
```
@Override
protected void onLayout(boolean changed, int l, int t, int r, int b) {
    setPadding(getPaddingLeft(), 
               statusBarHeight, 
               getPaddingRight(), 
               getPaddingBottom());
    super.onLayout(changed, l, t, r, b);
}
```
通过查看源码可以了解到，`Toolbar`是由`ViewGroup`实现的，其中各个控件的位置是通过私有方法计算得到的，而在这个方法中影响垂直位置计算的就是`padding`值，所以设置padding值将状态栏的那块高度空出来就OK了。
至此我们这个控件就搞定了，赶快去试用吧！[源码链接](https://github.com/xiaoyangperfect/XToolbar/tree/master)
