package com.mingchu.newcalendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * Created by wuyinlei on 2017/5/28.
 */

public class CalendarControl extends LinearLayout implements View.OnClickListener {

    private ImageView mIvLeft, mIvRight;
    private TextView mTvTitle;
    private RecyclerView mCalendarView;

    private Calendar mCalendar = Calendar.getInstance();
    private ArrayList<Date> mCells;

    public CalendarControl(Context context) {
        this(context, null);
    }

    public CalendarControl(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarControl(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initControl(context);
    }


    private void initControl(Context context) {

        initView(context);

        bindControlEvent();

        renderCalendar(context);
    }

    private void bindControlEvent() {
        mIvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);

    }

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
                mCalendar.add(Calendar.MONTH, -1);
                renderCalendar(v.getContext());
                break;

            case R.id.iv_right:
                mCalendar.add(Calendar.MONTH, 1);
                renderCalendar(v.getContext());
                break;
        }
    }

    //渲染控件
    private void renderCalendar(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
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

        mCalendarView.setLayoutManager(new GridLayoutManager(context,7));
        mCalendarView.setAdapter(new CalendarAdapter());

    }



    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.calendar_text_day,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Date date = mCells.get(position);

            holder.mTvDay.setText(String.valueOf(date.getDate()));
        }

        @Override
        public int getItemCount() {
            return mCells == null ? 0 : mCells.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mTvDay;

            public ViewHolder(View itemView) {
                super(itemView);
                mTvDay = (TextView) itemView.findViewById(R.id.tv_calendar_day);
                mTvDay.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "点击我了", Toast.LENGTH_SHORT).show();
                    }
                });

                mTvDay.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Toast.makeText(v.getContext(), "长按点击我了", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
            }
        }
    }
}
