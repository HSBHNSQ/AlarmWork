package com.liubowang.shiftwork.Alarm.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;


import com.liubowang.shiftwork.Activity.MainActivity;
import com.liubowang.shiftwork.Alarm.dao.AlarmInfoDao;
import com.liubowang.shiftwork.Alarm.domain.AlarmClock;
import com.liubowang.shiftwork.Alarm.domain.AlarmInfo;
import com.liubowang.shiftwork.Alarm.utils.PrefUtils;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;

import java.util.ArrayList;

/**
 * Created by Joe on 2016/1/23.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarm","Boot收到广播");
        Toast.makeText(context, "Boot收到广播", Toast.LENGTH_LONG).show();
        checkAndStartAlarm(context);
//        Intent main  = new Intent(context, MainActivity.class);
//        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        main.putExtra("isFinish",true);
//        context.startActivity(main);
    }

    //开机时检查是否有闹钟需要开启
    private void checkAndStartAlarm(Context context) {
        Log.d("alarm","开始检查是否有闹钟");
        AlarmInfoDao dao=new AlarmInfoDao(context);
        ArrayList<AlarmInfo> list= (ArrayList<AlarmInfo>) dao.getAllInfo();
        AlarmClock clock=new AlarmClock(context);
        if(SharedPreferencesUtil.getBoolean(context, SWConst.OPEN_SHIFT_WORK, false)){
            for (AlarmInfo alarmInfo:list) {
                clock.turnAlarm(alarmInfo,null,true);
            }
        }
    }
}
