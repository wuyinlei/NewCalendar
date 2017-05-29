package com.mingchu.newcalendar;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wuyinlei on 2017/5/28.
 *
 * @function 日历适配器
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private  Resources res;
    private  Context context;
    private boolean isLeapyear = false; // 是否为闰年
    private int mDaysOfMonth = 0; // 某月的天数
    private int mDayOfWeek = 0; // 具体某一天是星期几
    private int mLastDaysOfMonth = 0; // 上一个月的总天数

    private String[] dayNumber = new String[42]; //

    private SpecialCalendar mSpecialCalendar;   //阳历  获取某月星期几
    private LunarCalendar mLunarCalendar;  //阴历相关事件
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");

    // 系统当前时间
    private String sysDate = "";
    private String sys_year = "";
    private String sys_month = "";
    private String sys_day = "";

    private ArrayList<Date> mCells;
    private int currentFlag = -1;

    private String showYear = ""; // 用于在头部显示的年份
    private String showMonth = ""; // 用于在头部显示的月份
    private String animalsYear = "";
    private String leapMonth = ""; // 闰哪一个月
    private String cyclical = ""; // 天干地支

    private String currentYear = "";  //当前年
    private String currentMonth = ""; //当前月
    private String currentDay = ""; //当前天

    public CalendarAdapter(ArrayList<Date> cells) {
        mCells = cells;
        mSpecialCalendar = new SpecialCalendar();

        Date date = new Date();
        sysDate = sdf.format(date); // 当期日期
        sys_year = sysDate.split("-")[0];
        sys_month = sysDate.split("-")[1];
        sys_day = sysDate.split("-")[2];
    }


    /**
     * 适配器
     * @param context  上下文
     * @param rs  Resources
     * @param jumpMonth  jump月份
     * @param jumpYear  jump年份
     * @param year_c   当前year
     * @param month_c  当前月份
     * @param day_c  当前天
     */
    public CalendarAdapter(Context context, Resources rs, int jumpMonth, int jumpYear, int year_c, int month_c, int day_c) {
        this.context = context;
        mSpecialCalendar = new SpecialCalendar();
        mLunarCalendar = new LunarCalendar();
        this.res = rs;

        int stepYear = year_c + jumpYear;
        int stepMonth = month_c + jumpMonth;
        if (stepMonth > 0) {
            // 往下一个月滑动
            if (stepMonth % 12 == 0) {
                stepYear = year_c + stepMonth / 12 - 1;
                stepMonth = 12;
            } else {
                stepYear = year_c + stepMonth / 12;
                stepMonth = stepMonth % 12;
            }
        } else {
            // 往上一个月滑动
            stepYear = year_c - 1 + stepMonth / 12;
            stepMonth = stepMonth % 12 + 12;
            if (stepMonth % 12 == 0) {

            }
        }

        currentYear = String.valueOf(stepYear); // 得到当前的年份
        currentMonth = String.valueOf(stepMonth); // 得到本月
        // （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
        currentDay = String.valueOf(day_c); // 得到当前日期是哪天

        getCalendar(Integer.parseInt(currentYear), Integer.parseInt(currentMonth));

    }


    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.calendar_text_day, parent, false);
        return new CalendarAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.ViewHolder holder, int position) {
//        final Date date = mCells.get(position);

        Log.d("CalendarAdapter", dayNumber[position]);
        String[] split = dayNumber[position].split("\\.");
        final String specialResult = split[0];
        String lunarResult = split[1];
        holder.mTvDay.setText(specialResult);
        holder.mTvLunarDay.setText(lunarResult);

//        holder.mTvDay.setText(String.valueOf(date.getDate()));
//        Date nowDay = new Date();
//
//        //是否是当前月份
//        boolean isThisMonth;
//
//        isThisMonth = date.getMonth() == nowDay.getMonth();
//
//        if (isThisMonth) {
//            //有效的月份  当前月份
//            holder.mTvDay.setTextColor(Color.parseColor("#000000"));
//        } else {
//            //不是当前月份
//            holder.mTvDay.setTextColor(Color.parseColor("#666666"));
//        }
//
//        if (date.getDate() == nowDay.getDate() && nowDay.getMonth() == date.getMonth()
//                && date.getYear() == nowDay.getYear()) {
//            //当天
//            holder.mTvDay.isToday = true;
//            holder.mTvDay.setTextColor(Color.parseColor("#ff0000"));
//
//        }
//
        holder.mTvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, specialResult, Toast.LENGTH_SHORT).show();
            }
        });

        holder.mTvDay.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "长按点击事件", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dayNumber.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CalendarTextView mTvDay;
        private TextView mTvLunarDay;

        ViewHolder(View itemView) {
            super(itemView);
            mTvDay = (CalendarTextView) itemView.findViewById(R.id.tv_calendar_day);
            mTvLunarDay = (TextView) itemView.findViewById(R.id.tv_lunar_day);
        }
    }


    // 得到某年的某月的天数且这月的第一天是星期几
    public void getCalendar(int year, int month) {
        isLeapyear = mSpecialCalendar.isLeapYear(year); // 是否为闰年
        mDaysOfMonth = mSpecialCalendar.getDaysOfMonth(isLeapyear, month); // 某月的总天数
        mDayOfWeek = mSpecialCalendar.getWeekdayOfMonth(year, month); // 某月第一天为星期几
        mLastDaysOfMonth = mSpecialCalendar.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
//        Log.d("DAY", isLeapyear + " ======  " + daysOfMonth + "  ============  " + dayOfWeek + "  =========   " + lastDaysOfMonth);
        getweek(year, month);
    }


    // 将一个月中的每一天的值添加入数组dayNuber中
    private void getweek(int year, int month) {
        int j = 1;
        int flag = 0;
        String lunarDay = "";

        // 得到当前月的所有日程日期(这些日期需要标记)
        for (int i = 0; i < dayNumber.length; i++) {
            // 周一
            // if(i<7){
            // dayNumber[i]=week[i]+"."+" ";
            // }
            if (i < mDayOfWeek) { // 前一个月
                int temp = mLastDaysOfMonth - mDayOfWeek + 1;
                lunarDay = mLunarCalendar.getLunarDate(year, month - 1, temp + i, false);
                dayNumber[i] = (temp + i) + "." + lunarDay;

            } else if (i < mDaysOfMonth + mDayOfWeek) { // 本月
                String day = String.valueOf(i - mDayOfWeek + 1); // 得到的日期
                lunarDay = mLunarCalendar.getLunarDate(year, month, i - mDayOfWeek + 1, false);
                dayNumber[i] = i - mDayOfWeek + 1 + "." + lunarDay;
                // 对于当前月才去标记当前日期
                if (sys_year.equals(String.valueOf(year)) && sys_month.equals(String.valueOf(month)) && sys_day.equals(day)) {
                    // 标记当前日期
                    currentFlag = i;
                }
                setShowYear(String.valueOf(year));
                setShowMonth(String.valueOf(month));
                setAnimalsYear(mLunarCalendar.animalsYear(year));
                setLeapMonth(mLunarCalendar.leapMonth == 0 ? "" : String.valueOf(mLunarCalendar.leapMonth));
                setCyclical(mLunarCalendar.cyclical(year));
            } else { // 下一个月
                lunarDay = mLunarCalendar.getLunarDate(year, month + 1, j, false);
                dayNumber[i] = j + "." + lunarDay;
                j++;
            }
        }

        String abc = "";
        for (int i = 0; i < dayNumber.length; i++) {
            abc = abc + dayNumber[i] + ":";
        }
        Log.d("DAYNUMBER", abc);
    }


    public void setShowYear(String showYear) {
        this.showYear = showYear;
    }

    public void setShowMonth(String showMonth) {
        this.showMonth = showMonth;
    }

    public void setAnimalsYear(String animalsYear) {
        this.animalsYear = animalsYear;
    }

    public void setLeapMonth(String leapMonth) {
        this.leapMonth = leapMonth;
    }

    public void setCyclical(String cyclical) {
        this.cyclical = cyclical;
    }

    public Resources getRes() {
        return res;
    }

    public boolean isLeapyear() {
        return isLeapyear;
    }

    public String getShowYear() {
        return showYear;
    }

    public String getShowMonth() {
        return showMonth;
    }

    public String getLeapMonth() {
        return leapMonth;
    }

    public String getCyclical() {
        return cyclical;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public String getCurrentMonth() {
        return currentMonth;
    }

    public String getCurrentDay() {
        return currentDay;
    }

    //    private OnItemLongClickListener mOnLongClickListener;
//
//    public void setOnLongClickListener(OnItemLongClickListener onLongClickListener) {
//        mOnLongClickListener = onLongClickListener;
//    }
//
//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        mOnItemClickListener = onItemClickListener;
//    }
//
//    private OnItemClickListener mOnItemClickListener;
//
//    /**
//     * 日期点击事件
//     */
//    interface OnItemClickListener {
//        void ItemClick(View view, String date);
//    }
//
//    /**
//     * 日期长按点击事件
//     */
//    interface OnItemLongClickListener {
//        void onLongItemClick(View view, String tip);
//    }
}
