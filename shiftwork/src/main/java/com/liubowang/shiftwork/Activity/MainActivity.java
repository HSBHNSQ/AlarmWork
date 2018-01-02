package com.liubowang.shiftwork.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.liubowang.shiftwork.Alarm.dao.AlarmInfoDao;
import com.liubowang.shiftwork.Alarm.domain.AlarmClock;
import com.liubowang.shiftwork.Alarm.domain.AlarmInfo;
import com.liubowang.shiftwork.Base.SWBaseActiviry;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.L;
import com.liubowang.shiftwork.Util.NotificationCenter;
import com.liubowang.shiftwork.Util.RotationDataUtil;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;
import com.liubowang.shiftwork.View.CalendarView;
import com.liubowang.shiftwork.View.LeftMenuContentView;
import com.liubowang.shiftwork.View.ShowDetailView;
import com.necer.ncalendar.utils.Utils;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends SWBaseActiviry {




    private final String TAG = getClass().getSimpleName();
    private static final String ROTATION_DATA_KEY = "ROTATION_DATA_KEY_Z_F_L";
    private static final String CURRENT_DATE_TIME_KEY = "CURRENT_DATE_TIME_KEY_Z_F_L";
    private DrawerLayout drawerLayout;
    private LeftMenuContentView leftMenuContentView;
    private ShowDetailView showDetailView;
    private CalendarView calendarView;
    private String ringName;
    private String ringId = "galebi.mp3";
    private RotationDataUtil dataUtil;
    private DateTime currentDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.tb_toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        drawerLayout = findViewById(R.id.dl_drawer_layout_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            ConstraintLayout.LayoutParams toolBarLp = (ConstraintLayout.LayoutParams)toolbar.getLayoutParams();
            toolBarLp.topMargin = getStatusBarHeight();
            toolbar.setLayoutParams(toolBarLp);
        }
        leftMenuContentView = findViewById(R.id.lmcv_left_menu_content_main);
        leftMenuContentView.setOnLeftMenuListener(onLeftMenuListener);

        showDetailView = findViewById(R.id.sdv_show_detail_main);
        calendarView = findViewById(R.id.cdv_calendar_main);
        if (savedInstanceState != null){
            dataUtil = savedInstanceState.getParcelable(ROTATION_DATA_KEY);
            currentDateTime = (DateTime) savedInstanceState.getSerializable(CURRENT_DATE_TIME_KEY);
        }else {
            dataUtil = getInitRotatioinData();
            currentDateTime = new DateTime();
        }
        showDetailView.updateDetail(dataUtil);
        calendarView.setOnCalendarActionListener(new CalendarView.OnCalendarActionListener() {
            @Override
            public Activity getActivityToShowDatePicker() {
                return MainActivity.this;
            }

            @Override
            public void onDateTimeClick(DateTime dateTime) {
                currentDateTime = dateTime;
            }
        });

        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){
            if (getSupportActionBar() != null){
                getSupportActionBar().hide();
            }
            ImageView editImage = findViewById(R.id.iv_edit_main);
            if (editImage != null){
                editImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editItemClick();
                    }
                });
            }
        }

        ringName = SharedPreferencesUtil.getString(this,SWConst.RING_NAME,"");
        ringId = SharedPreferencesUtil.getString(this,SWConst.RING_ID,"");
        NotificationCenter.defaultCenter().addObserver(
                SWConst.NOTIFICATION_SHIFT_WORK_CYCLE_CHANGED,
                shiftWorkCycleChangedObserver);
        NotificationCenter.defaultCenter().addObserver(
                SWConst.NOTIFICATION_OPEN_SHIFT_WORK_CHANGED,
                openShiftWorkChangedObserver);

//        calendarView.post(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });

        Intent intent = getIntent();
        if (intent.hasExtra("isFinish")){
            boolean isFinish = intent.getBooleanExtra("isFinish",false);
            if (isFinish){
                finish();
            }
        }

    }
    private void editItemClick(){
        Intent intent = new Intent(MainActivity.this,SetShiftWorkActivity.class);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        else if (id == R.id.menu_edit_main){
            editItemClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
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



//==================================================================================================
//            all listener
//==================================================================================================


    private Observer shiftWorkCycleChangedObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            L.d(TAG,"shiftWorkCycleChangedObserver");
            calendarView.refresh();
            deleteAllAlarm();
            resetAllAlarm();
            dataUtil = getInitRotatioinData();
            showDetailView.updateDetail(dataUtil);
        }
    };
    private Observer openShiftWorkChangedObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            L.d(TAG,"openShiftWorkChangedObserver");
            boolean isOpen = SharedPreferencesUtil.getBoolean(MainActivity.this,SWConst.OPEN_SHIFT_WORK,false);
            calendarView.refresh();
            turnOnOffAllAlarm(isOpen);
            dataUtil = getInitRotatioinData();
            showDetailView.updateDetail(dataUtil);
        }
    };

    private LeftMenuContentView.OnLeftMenuListener onLeftMenuListener = new LeftMenuContentView.OnLeftMenuListener() {
        @Override
        public void onRemindChanged(int minute) {
            int oldTime = SharedPreferencesUtil.getInt(MainActivity.this,SWConst.LEAD_TIME,0);
            if (oldTime != minute){
                SharedPreferencesUtil.putInt(MainActivity.this,SWConst.LEAD_TIME,minute);
                deleteAllAlarm();
                resetAllAlarm();
            }
        }

        @Override
        public void onVibrateSwitch(boolean open) {
            SharedPreferencesUtil.putBoolean(MainActivity.this,SWConst.VIBRATE_SWITCH,open);
            L.d(TAG,"onVibrateSwitch:" + open);
        }

        @Override
        public void onShiftWorkTypeClick() {
            L.d(TAG,"onShiftWorkTypeClick");
            Intent intent = new Intent(MainActivity.this,TypeSetActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
        }

        @Override
        public void onSuggestionClick() {
            L.d(TAG,"onSuggestionClick");
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            sendEmail();

        }

        @Override
        public void onCommentClick() {
            L.d(TAG,"onCommentClick");
            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            shareAppShop(getPackageName());
        }

        @Override
        public void onChooseRingClick() {
            startActivityForResult(new Intent(MainActivity.this, RingSetActivity.class), SWConst.ASK_FOR_RING);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (dataUtil == null){
            dataUtil = getInitRotatioinData();
        }
        if (currentDateTime == null){
            currentDateTime = new DateTime();
        }
        outState.putParcelable(ROTATION_DATA_KEY,dataUtil);
        outState.putSerializable(CURRENT_DATE_TIME_KEY,currentDateTime);
    }





    private void turnOnOffAllAlarm(boolean isOn){
        AlarmInfoDao dao=new AlarmInfoDao(this);
        List<AlarmInfo> alarmInfoList = dao.getAllInfo();
        AlarmClock alarmClock = new AlarmClock(this);
        for (AlarmInfo info: alarmInfoList){
            alarmClock.turnAlarm(info,info.getId(),isOn);
        }
    }

    private void deleteAllAlarm(){
        DataUtil.getInstance().deleteAllAlarm(this);
    }

    private void resetAllAlarm(){
        DataUtil.getInstance().resetAllAlarm(this,ringName,ringId);
    }

//    private AlarmInfo getAlarmInfo(int hour,int minute,String alarmTag){
//        AlarmInfo alarmInfo=new AlarmInfo();
//        alarmInfo.setHour(hour);
//        alarmInfo.setMinute(minute);
//        int[] day=getRepeatDay();
//        alarmInfo.setDayOfWeek(day);
//        alarmInfo.setTag(alarmTag);
//        alarmInfo.setRing(ringName);
//        alarmInfo.setRingResId(ringId);
//        return alarmInfo;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case SWConst.RING_SET_CANCEL:
                break;
            case SWConst.RING_SET_DONG:
                ringName = data.getStringExtra("songname");
                if(data.getStringExtra("songid")!=null){
                    ringId = data.getStringExtra("songid");
                }
                SharedPreferencesUtil.putString(this,SWConst.RING_NAME,ringName);
                SharedPreferencesUtil.putString(this,SWConst.RING_ID,ringId);
                NotificationCenter.defaultCenter().postNotification(SWConst.NOTIFICATION_RING_CHANGED);
                L.d(TAG,"ringId:"+ringId);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().removeObserver(
                SWConst.NOTIFICATION_SHIFT_WORK_CYCLE_CHANGED,
                shiftWorkCycleChangedObserver);
        NotificationCenter.defaultCenter().removeObserver(
                SWConst.NOTIFICATION_OPEN_SHIFT_WORK_CHANGED,
                openShiftWorkChangedObserver);
        L.d(TAG,"onDestroy");
    }


//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
