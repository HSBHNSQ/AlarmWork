package com.liubowang.shiftwork.View;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.DisplayUtil;
import com.liubowang.shiftwork.Util.L;
import com.liubowang.shiftwork.Util.NotificationCenter;
import com.liubowang.shiftwork.Util.SWConst;

import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import cn.qqtheme.framework.picker.TimePicker;

/**
 * Created by heshaobo on 2017/12/21.
 */

public class TypeSetItemView extends ConstraintLayout {

    private final String TAG = getClass().getSimpleName();
    private static int MAX_DELETE_IV_WEIGHT = 0;
    private TextView titleTv;
    private TextView time1Tv;
    private TextView time2Tv;
    private View currentView;
    private ShiftWorkType workType;
    private ImageView deleteIv;
    private boolean eidt;

    public TypeSetItemView(Context context) {
        super(context);
        init( context);
    }

    public TypeSetItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init( context);
    }

    public TypeSetItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init( context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_type_set_item_view,this,true);
        MAX_DELETE_IV_WEIGHT = DisplayUtil.dp2px(context,45);
        titleTv = findViewById(R.id.tv_title_tsiv);
        time1Tv = findViewById(R.id.tv_time_1_tsiv);
        time2Tv = findViewById(R.id.tv_time_2_tsiv);
        deleteIv = findViewById(R.id.iv_delete_tsiv);
        deleteIv.setOnClickListener(onClickListener);
        time1Tv.setOnClickListener(onClickListener);
        time2Tv.setOnClickListener(onClickListener);

    }

    private void setTitle(String title){
        this.titleTv.setText(title);
    }

    private void setStartTime(String startTime){
        time1Tv.setText(startTime);
    }

    private void setEndTime(String endTime){
        time2Tv.setText(endTime);
    }

    public void setShiftWorkType(ShiftWorkType workType) {
        this.workType = workType;
        setTitle(workType.shiftWork);
        setStartTime(getTimeString(workType.startHour,workType.startMinute));
        setEndTime(getTimeString(workType.endHour,workType.endMinute));
    }

    public ShiftWorkType getShiftWorkType() {
        return workType;
    }

    public void setEidt(boolean eidt) {
        this.eidt = eidt;
        if (eidt){
            openDeleteIv();
        }
        else {
            closeDeleteIv();
        }
    }
    public boolean getEdit(){
        return eidt;
    }

    private void openDeleteIv(){
        startAnimation(0,MAX_DELETE_IV_WEIGHT);
    }
    private void closeDeleteIv(){
        startAnimation(MAX_DELETE_IV_WEIGHT,0);
    }

    private void startAnimation(int start,int end){
        ValueAnimator animator = ValueAnimator.ofInt(start,end);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                deleteIv.getLayoutParams().width = value;
                deleteIv.requestLayout();
            }
        });
        animator.start();
    }


    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == deleteIv){
                List<CycleItem> shiftWorkCycleItems = DataUtil.getInstance().getCycleItems(getContext());
                boolean canDelete = true;
                for (CycleItem item:shiftWorkCycleItems){
                    if (item.workType.shiftWork.equals(getShiftWorkType().shiftWork)){
                        canDelete = false;
                        break;
                    }
                }
                if (canDelete){
                    deleteAnimate();
                }else {
                    showNotDelete();
                }
            }
            else if (v == time1Tv || v == time2Tv){
                if (onTypeSetItemListener != null  ){
                    Activity activity = onTypeSetItemListener.getActivity();
                    if (activity != null){
                        currentView = v;
                        onTimePicker(activity);
                    }
                }
            }
        }
    };
    public void onTimePicker(Activity activity) {
        TimePicker picker = new TimePicker(activity, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        final int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                String time = hour + ":" + minute;
                L.d(TAG,time);
                if (currentView == time1Tv){
                    time1Tv.setText(time);
                    workType.startHour = Integer.parseInt(hour);
                    workType.startMinute = Integer.parseInt(minute);
                }
                else if (currentView == time2Tv){
                    time2Tv.setText(time);
                    workType.endHour = Integer.parseInt(hour);
                    workType.endMinute = Integer.parseInt(minute);
                }
            }
        });
        picker.show();
    }

    private String getTimeString(int hour,int minute){
        return String.format("%02d:%02d",hour,minute);
    }

    private OnTypeSetItemListener onTypeSetItemListener ;

    public void setOnTypeSetItemListener(OnTypeSetItemListener onTypeSetItemListener) {
        this.onTypeSetItemListener = onTypeSetItemListener;
    }

    public interface OnTypeSetItemListener{
        Activity getActivity();
        void onDeleteClick(TypeSetItemView itemView);
    }

    private void deleteAnimate(){
        int width = getWidth();
        ObjectAnimator moveOut = ObjectAnimator.ofFloat(this, "translationX", 0, width);
        moveOut.setDuration(600);
        moveOut.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (onTypeSetItemListener != null){
                    onTypeSetItemListener.onDeleteClick(TypeSetItemView.this);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        moveOut.start();
    }

    private void showNotDelete(){

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setMessage(getContext().getString(R.string.sw_not_delete))
                .setPositiveButton(getContext().getString(R.string.sure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .create();
        dialog.show();
    }


}
