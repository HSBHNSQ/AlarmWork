package com.liubowang.shiftwork.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.liubowang.shiftwork.Alarm.service.AlarmRingService;
import com.liubowang.shiftwork.Base.SWBaseActiviry;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.RotationDataUtil;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;
import com.liubowang.shiftwork.View.ShowDetailView;
import com.necer.ncalendar.utils.Utils;

import org.joda.time.DateTime;

import java.util.List;

public class WakeUpActivity extends SWBaseActiviry {

    private Intent service;
    private ShowDetailView showDetailView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake_up);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        setFullScreen(true);
        service=new Intent(this, AlarmRingService.class);
        String resid = SharedPreferencesUtil.getString(this, SWConst.RING_ID,"");
        service.putExtra("resid", resid);
        startService(service);
        showDetailView = findViewById(R.id.sdv_detail_wu);
        showDetailView.updateDetail(getInitRotatioinData());
        findViewById(R.id.tv_close_wu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(service);
                Intent main = new Intent(WakeUpActivity.this,MainActivity.class);
                main.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                if (main.resolveActivity(getPackageManager()) != null) {
                    startActivity(main);
                }
                finish();
            }
        });
//        int leadTime = SharedPreferencesUtil.getInt(context, SWConst.LEAD_TIME,0);
//        int leadM = leadTime % 60;
//        int leadH = leadTime / 60;
    }
    private RotationDataUtil getInitRotatioinData(){
        RotationDataUtil dataUtil = new RotationDataUtil();
        DateTime startDataTime = DataUtil.getInstance().getShiftWorkStartTime(this);
        DateTime todayDateTime = new DateTime();
        List<CycleItem> shiftWorkCycleItems = DataUtil.getInstance().getCycleItems(this);
        CycleItem todayItem = null;
        CycleItem next1Item = null;
        CycleItem next2Item = null;
        if (startDataTime != null && shiftWorkCycleItems != null){
            int dt = Utils.getIntervalDays(startDataTime,todayDateTime);
            todayItem = getCycleItem(shiftWorkCycleItems,dt);
            next1Item = getCycleItem(shiftWorkCycleItems,dt + 1);
            next2Item = getCycleItem(shiftWorkCycleItems,dt + 2);
        }
        String today_shift_work = "";
        String today_work_time = "";
        if (todayItem != null){
            today_shift_work = getString(R.string.sw_today) + todayItem.workType.shiftWork;
            today_work_time = DataUtil.toTimeFormat(todayItem.workType.startHour,
                    todayItem.workType.startMinute);
        }
        String next1_shift_work = "";
        String next1_work_time = "";
        if (next1Item != null){
            next1_shift_work = getString(R.string.sw_tomorrow) + next1Item.workType.shiftWork;
            next1_work_time = DataUtil.toTimeFormat(next1Item.workType.startHour,
                    next1Item.workType.startMinute);
        }
        String next2_shift_work = "";
        String next2_work_time = "";
        if (next2Item != null){
            next2_shift_work = getString(R.string.sw_after_tomorrow) + next2Item.workType.shiftWork;
            next2_work_time = DataUtil.toTimeFormat(next2Item.workType.startHour,
                    next2Item.workType.startMinute);
        }
        dataUtil.today_shift_work = today_shift_work;
        dataUtil.today_work_time = today_work_time;
        dataUtil.next1_shift_work = next1_shift_work;
        dataUtil.next1_work_time = next1_work_time;
        dataUtil.next2_shift_work = next2_shift_work;
        dataUtil.next2_work_time = next2_work_time;
        dataUtil.today_day = todayDateTime.getDayOfMonth()+"";
        dataUtil.today_month = todayDateTime.getMonthOfYear() +"";
        dataUtil.today_week = DataUtil.getWeekDay(todayDateTime.getDayOfWeek());
        return dataUtil;
    }

    public CycleItem getCycleItem(List<CycleItem> cycleItemList, int dtDay){
        if (cycleItemList.size() == 0) return null;
        int index = dtDay % cycleItemList.size();
        if (index < 0){
            index = index + cycleItemList.size();
        }
        return cycleItemList.get(index).copy();
    }
    private void setFullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(service);
    }

//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
