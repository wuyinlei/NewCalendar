package com.mingchu.newcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wuyinlei on 2017/5/28.
 *
 * @function 日期控制类
 */

public class CalendarControl extends LinearLayout implements View.OnClickListener {

    private ImageView mIvLeft, mIvRight;
    private TextView mTvTitle;
    private RecyclerView mCalendarView;

    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<Date> mCells;

    private String displayFormat;

    public CalendarControl(Context context) {
        this(context, null);
    }

    public CalendarControl(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }


    private void initControl(Context context, AttributeSet attrs) {
        //获取自定义的属性
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.NewCanlendar);
        try {
            displayFormat = ta.getString(R.styleable.NewCanlendar_dateFromat);
            if (TextUtils.isEmpty(displayFormat)) {
                displayFormat = "MMM yyyy";
            }
        } catch (Exception ignored) {
        } finally {
            ta.recycle();
        }

        initView(context);

        bindControlEvent();

        renderCalendar(context);
    }

    private void bindControlEvent() {
        mIvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);

    }


    /**
     * 初始化控件
     *
     * @param context 上下文
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.calendar_view, this);

        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mCalendarView = (RecyclerView) findViewById(R.id.calendar_recycler_view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                jumpMonth--; // 上一个月
                mCalendar.add(Calendar.MONTH, -1);
                renderCalendar(v.getContext());
                break;

            case R.id.iv_right:
                jumpMonth++; // 下一个月
                mCalendar.add(Calendar.MONTH, 1);
                renderCalendar(v.getContext());
                break;
        }
    }

    private static int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
    private static int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)
    private int year_c = 0;
    private int month_c = 0;
    private int day_c = 0;

    //渲染控件
    private void renderCalendar(Context context) {

        SimpleDateFormat sdf = new SimpleDateFormat(displayFormat);
        mTvTitle.setText(sdf.format(mCalendar.getTime()));

        mCells = new ArrayList<>();
        Calendar calendar = (Calendar) mCalendar.clone();  //进行clone

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int prevDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -prevDays);

        int maxCellCount = 6 * 7;
        while (mCells.size() < maxCellCount) {
            mCells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Date date = new Date();
        SimpleDateFormat sdfss = new SimpleDateFormat("yyyy-M-d");
        String currentDate = sdfss.format(date); // 当期日期
        year_c = Integer.parseInt(currentDate.split("-")[0]);
        month_c = Integer.parseInt(currentDate.split("-")[1]);
        day_c = Integer.parseInt(currentDate.split("-")[2]);


        mCalendarView.setLayoutManager(new GridLayoutManager(context, 7));

        CalendarAdapter adapter = new CalendarAdapter(context,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
        mCalendarView.setAdapter(adapter);
    }


    private OnItemLongClickListener mOnLongClickListener;

    public void setOnLongClickListener(OnItemLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;

    /**
     * 日期点击事件
     */
    interface OnItemClickListener {
        void ItemClick(View view, Date date);
    }

    /**
     * 日期长按点击事件
     */
    interface OnItemLongClickListener {
        void onLongItemClick(View view, String tip);
    }

}
