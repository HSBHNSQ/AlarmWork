package com.liubowang.shiftwork.View;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liubowang.shiftwork.Activity.NotesAdapter;
import com.liubowang.shiftwork.R;;
import com.liubowang.shiftwork.Util.L;
import com.necer.ncalendar.calendar.NCalendar;
import com.necer.ncalendar.listener.OnCalendarChangedListener;

import org.joda.time.DateTime;


import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by heshaobo on 2017/12/15.
 */

public class CalendarView extends ConstraintLayout  {

    private final String TAG = getClass().getSimpleName();
    private NCalendar nCalendar ;
    private TextView monthTextView;
    private TextView yearTextView;
    private ImageView lastImageView;
    private ImageView nextImageView;
    private RecyclerView recyclerView;
    public CalendarView(Context context) {
        super(context);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_calendar_view,this,true);
        nCalendar = findViewById(R.id.nc_calendar_cdv);
        monthTextView = findViewById(R.id.tv_month_number_cdv);
        yearTextView  = findViewById(R.id.tv_year_number_cdv);
        lastImageView  = findViewById(R.id.iv_last_cdv);
        nextImageView  = findViewById(R.id.iv_next_cdv);
        recyclerView = findViewById(R.id.rv_recycle_view_cdv);

        monthTextView.setOnClickListener(onClickListener);
        yearTextView.setOnClickListener(onClickListener);
        lastImageView.setOnClickListener(onClickListener);
        nextImageView.setOnClickListener(onClickListener);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        NotesAdapter aaAdapter = new NotesAdapter(context);
        recyclerView.setAdapter(aaAdapter);
        nCalendar.setOnCalendarChangedListener(onCalendarChangedListener);
        nCalendar.post(new Runnable() {
            @Override
            public void run() {
                toToday();
            }
        });


    }



    private OnCalendarChangedListener onCalendarChangedListener = new OnCalendarChangedListener() {
        @Override
        public void onCalendarChanged(DateTime dateTime,String shiftWork) {
            monthTextView.setText(dateTime.getMonthOfYear() + "月");
            yearTextView.setText(dateTime.getYear() + "年");//+ dateTime.getMonthOfYear() + "月" + dateTime.getDayOfMonth() + "日");
            L.d(TAG,"dateTime:" + dateTime);
            L.d(TAG,"ShiftWork:" + shiftWork);
            if (onCalendarActionListener != null){
                onCalendarActionListener.onDateTimeClick(dateTime);
            }
        }
    };

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == monthTextView || v == yearTextView){
                if (onCalendarActionListener != null ){
                    Activity activity = onCalendarActionListener.getActivityToShowDatePicker();
                    if (activity != null){
                        onYearMonthDayPicker(activity);
                    }
                }
            }
            else if (v == lastImageView){
                toLastMonth();
            }
            else if (v == nextImageView){
                toNextMonth();
            }
        }
    };


    public void refresh(){
        nCalendar.refresh();
    }

    public void toLastMonth() {
        nCalendar.toLastPager();

    }

    public void toNextMonth() {
        nCalendar.toNextPager();
    }

    public void setDate(String date) {
        nCalendar.setDate(date);
    }


    public void setDate(DateTime dateTime){
        nCalendar.setDate(dateTime);
    }
    public void toMonth() {
        nCalendar.toMonth();
    }

    public void toWeek() {
        nCalendar.toWeek();
    }

    public void toToday() {
        nCalendar.toToday();
    }

    private void onYearMonthDayPicker(Activity activity) {
        final DatePicker picker = new DatePicker(activity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(activity, 10));
        picker.setRangeEnd(2080, 12, 31);
        picker.setRangeStart(1970, 1, 1);
        DateTime dateTime = new DateTime();
        picker.setSelectedItem(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                setDate(year +"-"+month+"-"+day);
            }
        });
        picker.show();
    }

    private OnCalendarActionListener onCalendarActionListener ;

    public void setOnCalendarActionListener(OnCalendarActionListener onCalendarActionListener) {
        this.onCalendarActionListener = onCalendarActionListener;
    }

    public interface OnCalendarActionListener{
         Activity getActivityToShowDatePicker();
         void onDateTimeClick(DateTime dateTime);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
