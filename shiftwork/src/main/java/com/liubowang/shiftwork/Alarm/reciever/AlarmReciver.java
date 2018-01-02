package com.liubowang.shiftwork.Alarm.reciever;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.liubowang.shiftwork.Activity.MainActivity;
import com.liubowang.shiftwork.Activity.ShowLogActivity;
import com.liubowang.shiftwork.Activity.WakeUpActivity;
import com.liubowang.shiftwork.Alarm.dao.AlarmInfoDao;
import com.liubowang.shiftwork.Alarm.domain.AlarmClock;
import com.liubowang.shiftwork.Alarm.domain.AlarmInfo;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.L;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;
import com.necer.ncalendar.utils.Utils;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Joe on 2016/1/11.
 */
public class AlarmReciver  extends BroadcastReceiver {


    private final String TAG = getClass().getSimpleName();
    private Context context;
    private String getid;
    private String resid = "galebi.mp3";
    private int id;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Log.d("alarm","收到广播");
        Toast.makeText(context, "收到广播", Toast.LENGTH_SHORT).show();
        getid = intent.getStringExtra("getid");
        String alarmid = intent.getStringExtra("alarmid");
        Bundle bundle = intent.getExtras();
        AlarmInfo currentAlarm=(AlarmInfo)bundle.getSerializable("alarminfo");
        String message="收到广播:" ;

        id = intent.getIntExtra("alarmid", 0);
        message += "\nintent:"+intent.toString();
        message += "\nbundle:"+bundle.toString();


        if(intent.getBooleanExtra("cancel",false)){
            cancelAlarm(intent);
            return;
        }
        Toast.makeText(context, "开始获取信息", Toast.LENGTH_SHORT).show();
        if (currentAlarm != null){
            message += "\nalarminfo:" + currentAlarm.toString();
        }
        if (getid != null){
            message += "\ngetid:" + getid;
        }
        if (alarmid != null){
            message += "\nalarmid" + alarmid;
        }

        AlarmInfoDao dao = new AlarmInfoDao(context);
        currentAlarm = dao.findById(getid);
        if (currentAlarm == null){
            return;
        }
        if (currentAlarm != null){
            message += "\nalarminfo:" + currentAlarm.toString();
        }
        message += "\nonlyId:" + id;

        DateTime startDataTime = DataUtil.getInstance().getShiftWorkStartTime(context);
        List<CycleItem> shiftWorkCycleItems = DataUtil.getInstance().getCycleItems(context);
        Toast.makeText(context, "开始计算", Toast.LENGTH_SHORT).show();
        boolean canRemind = false;
        if (startDataTime != null && shiftWorkCycleItems != null){
            int dt = Utils.getIntervalDays(startDataTime,new DateTime());
            int index = dt % shiftWorkCycleItems.size();
            if (index >= 0){
                CycleItem cycleItem = shiftWorkCycleItems.get(index);
                int leadTime = SharedPreferencesUtil.getInt(context, SWConst.LEAD_TIME,0);
                L.d(TAG,"leadTime:" + leadTime);
                int leadM = leadTime % 60;
                int leadH = leadTime / 60;
                int hourR = currentAlarm.getHour() + leadH;
                int minuteR = currentAlarm.getMinute() + leadM;
                boolean isNextDay = false;
                if (hourR >= 24){
                    hourR = hourR - 24;
                    isNextDay = true;
                }
                if (minuteR >= 60){
                    hourR = hourR + 1;
                    minuteR = minuteR - 60;
                    if (hourR >= 24){
                        hourR = hourR - 24;
                        isNextDay = true;
                    }
                }
                if (isNextDay){
                    cycleItem = shiftWorkCycleItems.get((index + 1) % shiftWorkCycleItems.size());
                }
                boolean sameHour = cycleItem.workType.startHour == hourR;
                boolean sameMinute = cycleItem.workType.startMinute == minuteR;
                message += "\n"+"sameHour = " + sameHour +":sameMinute = " + sameMinute ;
                message += "\n" + cycleItem.workType.toString();
                message += "\n" + "hourR = " + hourR + ":minuteR" +minuteR;
                message += "\n" + "cycleItem.isRemind = " + cycleItem.isRemind;
                Toast.makeText(context, "sameHour = " + sameHour +":sameMinute = " + sameMinute , Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "cycleItem.isRemind = " + cycleItem.isRemind , Toast.LENGTH_SHORT).show();
                if (sameHour){
                    if (sameMinute){
                        if (cycleItem.isRemind){
                            canRemind = true;
                        }
                    }
                }
            }
        }
        Toast.makeText(context, "收到广播:canRemind = " + canRemind, Toast.LENGTH_SHORT).show();
        if (!canRemind) {
            message += "currentCycleItem == null" ;
            Intent log = new Intent(context, ShowLogActivity.class);
            log.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            log.putExtra("show_log",message);
            context.startActivity(log);
            runAlarmAgain(intent,getid);
            return;
        }
        wakePhoneAndUnlock();
        Intent wakeUp  = new Intent(context, WakeUpActivity.class);
        wakeUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resid = currentAlarm.getRingResId();
        wakeUp.putExtra("ringId",resid);
        context.startActivity(wakeUp);
//        Intent main  = new Intent(context, MainActivity.class);
//        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        resid = currentAlarm.getRingResId();
//        main.putExtra("ringId",resid);
//        main.putExtra("isRing",true);
//        context.startActivity(main);
        runAlarmAgain(intent,getid);
    }

    private void runAlarmAgain(Intent intent, String id) {
        Log.d("alarm","再次启动闹钟");
        AlarmClock alarmClock=new AlarmClock(context);
        alarmClock.turnAlarm(null,getid,true);
    }

    //点亮屏幕并解锁
    private void wakePhoneAndUnlock() {
//        //点亮屏幕
//        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.FULL_WAKE_LOCK, "WakeLock");
        mWakelock.acquire();//唤醒屏幕
////......
//        KeyguardManager mManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock mKeyguardLock = mManager.newKeyguardLock("Lock");
////让键盘锁失效
//        mKeyguardLock.disableKeyguard();
//        mWakelock.release();//释放
    }
    private void cancelAlarm(Intent intent) {
        Toast.makeText(context, "cancelAlarm", Toast.LENGTH_SHORT).show();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi=PendingIntent.getBroadcast(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pi);
    }

//    public void onReceive(Context context, Intent intent) {
//        this.context=context;
//        Log.d("alarm","收到广播");
//        Toast.makeText(context, "收到广播", Toast.LENGTH_SHORT).show();
//        getid = intent.getStringExtra("getid");
//        String alarmid = intent.getStringExtra("alarmid");
//        Bundle bundle = intent.getExtras();
//        AlarmInfo currentAlarm=(AlarmInfo)bundle.getSerializable("alarminfo");
//        String message="收到广播:" ;
//
//        id = intent.getIntExtra("alarmid", 0);
//        message += "\nintent:"+intent.toString();
//        message += "\nbundle:"+bundle.toString();
//
//
//        if(intent.getBooleanExtra("cancel",false)){
//            cancelAlarm(intent);
//            return;
//        }
//        Toast.makeText(context, "开始获取信息", Toast.LENGTH_SHORT).show();
//        if (currentAlarm != null){
//            message += "\nalarminfo:" + currentAlarm.toString();
//        }
//        if (getid != null){
//            message += "\ngetid:" + getid;
//        }
//        if (alarmid != null){
//            message += "\nalarmid" + alarmid;
//        }
//
//        AlarmInfoDao dao = new AlarmInfoDao(context);
//        currentAlarm = dao.findById(getid);
//        if (currentAlarm == null){
//            return;
//        }
//        if (currentAlarm != null){
//            message += "\nalarminfo:" + currentAlarm.toString();
//        }
//        message += "\nonlyId:" + id;
//
//        DateTime startDataTime = DataUtil.getInstance().getShiftWorkStartTime(context);
//        List<CycleItem> shiftWorkCycleItems = DataUtil.getInstance().getCycleItems(context);
//        Toast.makeText(context, "开始计算", Toast.LENGTH_SHORT).show();
//        boolean canRemind = false;
//        if (startDataTime != null && shiftWorkCycleItems != null){
//            int dt = Utils.getIntervalDays(startDataTime,new DateTime());
//            int index = dt % shiftWorkCycleItems.size();
//            if (index >= 0){
//                CycleItem cycleItem = shiftWorkCycleItems.get(index);
//                int leadTime = SharedPreferencesUtil.getInt(context, SWConst.LEAD_TIME,0);
//                int leadM = leadTime % 60;
//                int leadH = leadTime / 60;
//                int hourR = currentAlarm.getHour() + leadH;
//                int minuteR = currentAlarm.getMinute() + leadM;
//                boolean isNextDay = false;
//                if (hourR >= 24){
//                    hourR = hourR - 24;
//                    isNextDay = true;
//                }
//                if (minuteR >= 60){
//                    hourR = hourR + 1;
//                    minuteR = minuteR - 60;
//                    if (hourR >= 24){
//                        hourR = hourR - 24;
//                        isNextDay = true;
//                    }
//                }
//                if (isNextDay){
//                    cycleItem = shiftWorkCycleItems.get((index + 1) % shiftWorkCycleItems.size());
//                }
//                boolean sameHour = cycleItem.workType.startHour == hourR;
//                boolean sameMinute = cycleItem.workType.startMinute == minuteR;
//                message += "\n"+"sameHour = " + sameHour +":sameMinute = " + sameMinute ;
//                message += "\n" + cycleItem.workType.toString();
//                message += "\n" + "hourR = " + hourR + ":minuteR" +minuteR;
//                message += "\n" + "cycleItem.isRemind = " + cycleItem.isRemind;
//                Toast.makeText(context, "sameHour = " + sameHour +":sameMinute = " + sameMinute , Toast.LENGTH_SHORT).show();
//                Toast.makeText(context, "cycleItem.isRemind = " + cycleItem.isRemind , Toast.LENGTH_SHORT).show();
//                if (sameHour){
//                    if (sameMinute){
//                        if (cycleItem.isRemind){
//                            canRemind = true;
//                        }
//                    }
//                }
//            }
//        }
//        Toast.makeText(context, "收到广播:canRemind = " + canRemind, Toast.LENGTH_SHORT).show();
//        if (!canRemind) {
//            Intent log = new Intent(context, ShowLogActivity.class);
//            log.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            log.putExtra("show_log",message);
//            context.startActivity(log);
//            runAlarmAgain(intent,getid);
//            return;
//        }
//        wakePhoneAndUnlock();
//        Intent wakeUp  = new Intent(context, WakeUpActivity.class);
//        wakeUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        resid = currentAlarm.getRingResId();
//        wakeUp.putExtra("ringId",resid);
//        context.startActivity(wakeUp);
////        Intent main  = new Intent(context, MainActivity.class);
////        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        resid = currentAlarm.getRingResId();
////        main.putExtra("ringId",resid);
////        main.putExtra("isRing",true);
////        context.startActivity(main);
//        runAlarmAgain(intent,getid);
//    }
}
