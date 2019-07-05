package com.xy.xbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import androidx.annotation.Nullable;

public class Toolbar extends androidx.appcompat.widget.Toolbar {
    private int statusBarHeight;
    private Paint paint;
    private int color = getResources().getColor(R.color.colorPrimaryDark);
    private int measuredHeight;

    public Toolbar(Context context) {
        super(context);
        init(context, null);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Toolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Toolbar);
            color = typedArray.getColor(R.styleable.Toolbar_statusBarColor, getResources().getColor(R.color.colorPrimaryDark));
        }
        int id = context.getResources().getIdentifier("status_bar_height","dimen","android");
        statusBarHeight = context.getResources().getDimensionPixelOffset(id);
        paint = new Paint();
        paint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //为了兼容6.0及以前版本多次measure、layout问题
        measuredHeight = measuredHeight == 0? getMinimumHeight() : measuredHeight;
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), measuredHeight +statusBarHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setPadding(getPaddingLeft(), statusBarHeight, getPaddingRight(), getPaddingBottom());
        super.onLayout(changed, l, t, r, b);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect(0, 0, getMeasuredWidth(), (int) statusBarHeight);
        canvas.drawRect(rect, paint);
        super.onDraw(canvas);
    }


}
