package com.liubowang.shiftwork.Alarm.domain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;


import com.liubowang.shiftwork.Alarm.dao.AlarmInfoDao;
import com.liubowang.shiftwork.Alarm.reciever.AlarmReciver;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Joe on 2016/1/13.
 */
public class AlarmClock {
    private AlarmInfo alarmInfo;
    private Context context;
    private final AlarmInfoDao dao;

    public AlarmClock(Context context) {
        this.context = context;
        dao = new AlarmInfoDao(context);
    }
    public void turnAlarm(AlarmInfo alarmInfo, String AlarmID, Boolean isOn){
        if(alarmInfo==null){
            Log.d("alarm","传入AlarmInfo不为空");
            alarmInfo=dao.findById(AlarmID);
        }
        if (alarmInfo == null) return;
        this.alarmInfo=alarmInfo;
        AlarmManager mAlamManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int id = dao.getOnlyId(alarmInfo);
        Intent intent = new Intent(context, AlarmReciver.class);
//        Bundle bundle=new Bundle();
//        bundle.putSerializable("alarminfo", alarmInfo);
//        intent.putExtras(bundle);
        intent.setAction("com.Joe.RING_ALARM");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("alarmid", id);
        intent.putExtra("cancel",false);
        intent.putExtra("getid",alarmInfo.getId());
        Log.d("alarm", "id" + id);
        //每个闹钟不同的pi
        PendingIntent pi= PendingIntent.getBroadcast(context,id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if(isOn){
            startAlarm(mAlamManager,pi);
        }else{
            cancelAlarm(intent);
        }
    }

    private void cancelAlarm(Intent intent) {
        Log.d("alarm","取消闹钟");
        intent.putExtra("cancel",true);
        context.sendBroadcast(intent);
    }


    public void startAlarm(AlarmManager mAlamManager, PendingIntent pi){
        Log.d("alarm","启动闹钟");
        Calendar c= Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,alarmInfo.getHour());
        c.set(Calendar.MINUTE,alarmInfo.getMinute());
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND, 0);
        //  L.d("alarm", "当前系统版本" + Build.VERSION.SDK_INT);
        if(c.getTimeInMillis()< System.currentTimeMillis()){
            if(Build.VERSION.SDK_INT>=19)
            {
                mAlamManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }else{
                mAlamManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
            }
        }else{
            if(Build.VERSION.SDK_INT>=19)
            {
                Log.d("alarm","执行定时任务");
                Date date=c.getTime();
                Log.d("alarm","定时的时间是"+date.toString());
                mAlamManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }else{
                mAlamManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
            }
        }

    }


}
