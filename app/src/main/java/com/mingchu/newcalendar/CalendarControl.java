package com.mingchu.newcalendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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

/**
 * Created by wuyinlei on 2017/5/28.
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
        initControl(context,attrs);
    }


    private void initControl(Context context,AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.NewCanlendar);
        try {
            String format = ta.getString(R.styleable.NewCanlendar_dateFromat);
            displayFormat = format;
            if (TextUtils.isEmpty(displayFormat)){
                displayFormat = "MMM yyyy";
            }
        }catch (Exception e){
        }finally {
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
            final Date date = mCells.get(position);

            holder.mTvDay.setText(String.valueOf(date.getDate()));
            Date nowDay = new Date();
            boolean isThisMonth = false;

            if (date.getMonth() == nowDay.getMonth()){
                isThisMonth = true;
            } else {
                isThisMonth = false;
            }

            if (isThisMonth){
                //有效的月份  当月份
                holder.mTvDay.setTextColor(Color.parseColor("#000000"));
            } else {
                //不是当前月份
                holder.mTvDay.setTextColor(Color.parseColor("#666666"));
            }

            if (date.getDate() == nowDay.getDate() && nowDay.getMonth() == date.getMonth()
                    && date.getYear() == nowDay.getYear()){
                //当天
                holder.mTvDay.isToday = true;
                holder.mTvDay.setTextColor(Color.parseColor("#ff0000"));

            } else {

            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd");
            final String result = sdf.format(date.getDate());

            holder.mTvDay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null){
                        mOnItemClickListener.ItemClick(v,date);
                    }
                }
            });

            holder.mTvDay.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mOnLongClickListener != null){
                        mOnLongClickListener.onLongItemClick(v,"tip");
                    }
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return mCells == null ? 0 : mCells.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private CalendarTextView mTvDay;

            public ViewHolder(View itemView) {
                super(itemView);
                mTvDay = (CalendarTextView) itemView.findViewById(R.id.tv_calendar_day);


            }
        }





    }


    private OnItemLongClickListener mOnLongClickListener;

    public void setOnLongClickListener(OnItemLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;

    interface  OnItemClickListener {
        void ItemClick(View view,Date date);
    }

    interface  OnItemLongClickListener {
        void onLongItemClick(View view,String tip);
    }
}
