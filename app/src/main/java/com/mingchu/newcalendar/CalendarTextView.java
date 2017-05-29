package com.mingchu.newcalendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by wuyinlei on 2017/5/28.
 *
 * @function 自定义的TextView
 */

public class CalendarTextView extends android.support.v7.widget.AppCompatTextView {

    public boolean isToday = false;

    private Paint mPaint;

    public CalendarTextView(Context context) {
        this(context,null);
    }

    public CalendarTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CalendarTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initControl();
    }

    private void initControl() {
        //初始化画笔
        mPaint = new Paint();
        //设置paint的风格为“空心”
        mPaint.setStyle(Paint.Style.STROKE);
        //设置画笔颜色
        mPaint.setColor(Color.parseColor("#ff0000"));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //如果是当天就画红框
        if (isToday) {
            canvas.translate(getWidth() / 2, getHeight() / 2);
            canvas.drawCircle(0, 0, getWidth() / 2 - 10, mPaint);
        }
    }
}
