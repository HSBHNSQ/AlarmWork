package com.liubowang.shiftwork.View;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DisplayUtil;
import com.liubowang.shiftwork.Util.RotationDataUtil;

/**
 * Created by heshaobo on 2017/12/15.
 */

public class ShowDetailView extends ConstraintLayout {

    private final String TAG = getClass().getSimpleName();

    private TextView today_shift_work;
    private TextView today_work_time;
    private TextView next1_shift_work;
    private TextView next1_work_time;
    private TextView next2_shift_work;
    private TextView next2_work_time;
    private TextView today_day;
    private TextView today_month;
    private TextView today_week;
    private TextView day_text;
    private TextView month_text;

    public ShowDetailView(Context context) {
        super(context);
        init(context);
    }

    public ShowDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShowDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_show_detail_view,this,true);

        today_shift_work = findViewById(R.id.tv_today_shift_work_sdv);
//        today_work_time = findViewById(R.id.tv_today_shift_work_sdv);
        next1_shift_work = findViewById(R.id.tv_next1_shift_work_sdv);
        next1_work_time = findViewById(R.id.tv_next1_shift_work_time_sdv);
        next2_shift_work = findViewById(R.id.tv_next2_shift_work_sdv);
        next2_work_time = findViewById(R.id.tv_next2_shift_work_time_sdv);
        today_day = findViewById(R.id.tv_today_number_sdv);
        today_month = findViewById(R.id.tv_month_number_sdv);
        today_week = findViewById(R.id.tv_week_day_sdv);
        day_text = findViewById(R.id.tv_day_text_sdv);
        month_text = findViewById(R.id.tv_month_text_sdv);
        this.post(new Runnable(){
            @Override
            public void run() {
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                float min = Math.min(width,height) ;
                int spBig = DisplayUtil.px2sp(getContext(),min/2.5f);
                today_day.setTextSize(TypedValue.COMPLEX_UNIT_SP,spBig);
                int spNormal = DisplayUtil.px2sp(getContext(),min/17f);
                today_shift_work.setTextSize(TypedValue.COMPLEX_UNIT_SP,spNormal);
                today_month.setTextSize(TypedValue.COMPLEX_UNIT_SP,spNormal);
                today_week.setTextSize(TypedValue.COMPLEX_UNIT_SP,spNormal);
                day_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,spNormal);
                month_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,spNormal);
                int spSmall = DisplayUtil.px2sp(getContext(),min/30f);
                spSmall = spSmall < 14 ? 14 : spSmall;
                next1_shift_work.setTextSize(TypedValue.COMPLEX_UNIT_SP,spSmall);
                next1_work_time.setTextSize(TypedValue.COMPLEX_UNIT_SP,spSmall);
                next2_shift_work.setTextSize(TypedValue.COMPLEX_UNIT_SP,spSmall);
                next2_work_time.setTextSize(TypedValue.COMPLEX_UNIT_SP,spSmall);
            }
        });
    }

    public void updateDetail(RotationDataUtil dataUtil){
        today_shift_work.setText(dataUtil.today_shift_work);
//        today_work_time.setText(dataUtil.today_work_time);
        next1_shift_work.setText(dataUtil.next1_shift_work);
        next1_work_time.setText(dataUtil.next1_work_time);
        next2_shift_work.setText(dataUtil.next2_shift_work);
        next2_work_time.setText(dataUtil.next2_work_time);
        today_day.setText(dataUtil.today_day);
        today_month.setText(dataUtil.today_month);
        today_week.setText(dataUtil.today_week);
    }

}
