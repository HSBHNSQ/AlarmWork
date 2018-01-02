package com.liubowang.shiftwork.Util;

import android.content.Context;

import com.liubowang.shiftwork.Activity.MainActivity;
import com.liubowang.shiftwork.Alarm.dao.AlarmInfoDao;
import com.liubowang.shiftwork.Alarm.domain.AlarmClock;
import com.liubowang.shiftwork.Alarm.domain.AlarmInfo;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by heshaobo on 2017/12/21.
 */

public class DataUtil {

    private static DataUtil instance = new DataUtil();
    private static final String SHIFT_WORK_TYPE_LIST_PATH_NAME = "shiftWorks";
    private static final String CYCLE_ITEM_LIST_PATH_NAME = "cycleItems";
    private static final String SHIFT_WORK_CYCLE_ARRAY_NAME = "SHIFT_WORK_LIST";
    public static DataUtil getInstance(){return  instance;}

    /**
     * @param min
     * @param max
     * @return [min,max)
     */
    public static int random(int min,int max){
        int N = min;  int M = max; int A = M - N;
        double B = Math.random() * A; return (int) B + N;
    }

    public ArrayList<ShiftWorkType> getShiftWorkType(Context context){
        File shiftWorkFile = new File(context.getFilesDir(),SHIFT_WORK_TYPE_LIST_PATH_NAME);

        ObjectInputStream ois=null;
        ArrayList shiftWorks = new ArrayList();
        try {
            //获取输入流
            ois=new ObjectInputStream(new FileInputStream(shiftWorkFile));
            //获取文件中的数据
            shiftWorks = (ArrayList) ois.readObject();
            //把数据显示在TextView中
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return shiftWorks;
    }

    public boolean saveShiftWorkType(Context context,List<ShiftWorkType>shiftWorkTypeList){
        File shiftWorkFile = new File(context.getFilesDir(),SHIFT_WORK_TYPE_LIST_PATH_NAME);
        ObjectOutputStream fos=null;
        try {
            //如果文件不存在就创建文件
            if (!shiftWorkFile.exists()){
                shiftWorkFile.createNewFile();
            }
            //获取输出流
            fos=new ObjectOutputStream(new FileOutputStream(shiftWorkFile));
            //这里不能再用普通的write的方法了
            //要使用writeObject
            fos.writeObject(shiftWorkTypeList);;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
                if (fos!=null) {
                    fos.close();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }


    public void deleteAllAlarm(Context context){
        AlarmInfoDao dao=new AlarmInfoDao(context);
        List<AlarmInfo> alarmInfoList = dao.getAllInfo();
        AlarmClock alarmClock = new AlarmClock(context);
        for (AlarmInfo info: alarmInfoList){
            alarmClock.turnAlarm(info,info.getId(),false);
            dao.deleteAlarm(info);
        }
    }

    public void resetAllAlarm(Context context,String ringName,String ringId){
        AlarmInfoDao dao=new AlarmInfoDao(context);
        List<ShiftWorkType> shiftWorkTypeList = DataUtil.getInstance().getShiftWorkType(context);
//        List<CycleItem> cycleItemList = DataUtil.getInstance().getCycleItems(context);

        int leadTime = SharedPreferencesUtil.getInt(context, SWConst.LEAD_TIME,0);
        int leadM = leadTime % 60;
        int leadH = leadTime / 60;

        AlarmClock alarmClock = new AlarmClock(context);
        for (int i = 0; i < shiftWorkTypeList.size(); i ++){
            ShiftWorkType workType = shiftWorkTypeList.get(i);
            int hour = workType.startHour - leadH;
            int minute = workType.startMinute - leadM;
            if (hour < 0){
                hour = hour + 24;
            }
            if (minute < 0){
                hour = hour - 1;
                minute = 60 + minute;
                if (hour < 0){
                    hour = hour + 24;
                }
            }
            String alarmTag = "";
//            for (CycleItem item: cycleItemList){
//                if (workType.shiftWork.equals(item.workType.shiftWork)){
//                    if (alarmTag.length() > 0){
//                        alarmTag = alarmTag + "," + item.getUUID();
//                    }else {
//                        alarmTag = alarmTag + item.getUUID();
//                    }
//                }
//            }
            AlarmInfo alarmInfo= getAlarmInfo(hour,minute,alarmTag,ringName,ringId);
            dao.addAlarmInfo(alarmInfo);
            alarmClock.turnAlarm(alarmInfo,null,true);
        }

    }

    private AlarmInfo getAlarmInfo(int hour,int minute,String alarmTag,String ringName,String ringId){
        AlarmInfo alarmInfo=new AlarmInfo();
        alarmInfo.setHour(hour);
        alarmInfo.setMinute(minute);
        int[] day=getRepeatDay();
        alarmInfo.setDayOfWeek(day);
        alarmInfo.setTag(alarmTag);
        alarmInfo.setRing(ringName);
        alarmInfo.setRingResId(ringId);
        return alarmInfo;
    }

    private int[] getRepeatDay() {
        String dayRepeat="";
        dayRepeat+="1"+",";
        dayRepeat+="2"+",";
        dayRepeat+="3"+",";
        dayRepeat+="4"+",";
        dayRepeat+="5"+",";
        dayRepeat+="6"+",";
        dayRepeat+="7"+",";
        return AlarmInfoDao.getAlarmDayofWeek(dayRepeat);
    }

    public ArrayList<CycleItem> getCycleItems(Context context){
        File cycleItemsFile = new File(context.getFilesDir(),CYCLE_ITEM_LIST_PATH_NAME);
        ObjectInputStream ois=null;
        ArrayList cycleItems = new ArrayList();
        boolean isOpen = SharedPreferencesUtil.getBoolean(context,SWConst.OPEN_SHIFT_WORK,false);
        if (!isOpen){
            return cycleItems;
        }
        try {
            //获取输入流
            ois=new ObjectInputStream(new FileInputStream(cycleItemsFile));
            //获取文件中的数据
            cycleItems = (ArrayList) ois.readObject();
            //把数据显示在TextView中
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cycleItems;
    }

    public boolean saveCycleItems(Context context,List<CycleItem> cycleItemList){
        File cycleItemFile = new File(context.getFilesDir(),CYCLE_ITEM_LIST_PATH_NAME);
        boolean success = false;
        success = saveArrayList(cycleItemList,cycleItemFile);
        if (success){
            List<String> shiftWorkCycle = new ArrayList<>();
            for (CycleItem item:cycleItemList){
                shiftWorkCycle.add(item.workType.shiftWork);
            }
            File shfile = new File(context.getFilesDir(),SHIFT_WORK_CYCLE_ARRAY_NAME);
            saveArrayList(shiftWorkCycle,shfile);
        }
        return success;
    }

    private List<String> getShiftWorkCycleList(Context context){
        File shiftCycle = new File(context.getFilesDir(),SHIFT_WORK_CYCLE_ARRAY_NAME);
        ObjectInputStream ois=null;
        ArrayList shiftCycleList = new ArrayList();
        try {
            //获取输入流
            ois=new ObjectInputStream(new FileInputStream(shiftCycle));
            //获取文件中的数据
            shiftCycleList = (ArrayList) ois.readObject();
            //把数据显示在TextView中
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return shiftCycleList;
    }

    private boolean saveArrayList(List list,File file){
        ObjectOutputStream fos=null;
        try {
            //如果文件不存在就创建文件
            if (!file.exists()){
                file.createNewFile();
            }
            //获取输出流
            fos=new ObjectOutputStream(new FileOutputStream(file));
            //这里不能再用普通的write的方法了
            //要使用writeObject
            fos.writeObject(list);;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
                if (fos!=null) {
                    fos.close();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    private static final String SHIFT_WORK_START_DATE_TIME_NAME = "START_TIME";
    public boolean saveShiftWorkStartTime(Context context,DateTime startTime){
        File startTimeFile = new File(context.getFilesDir(),SHIFT_WORK_START_DATE_TIME_NAME);
        ObjectOutputStream fos=null;
        try {
            if (!startTimeFile.exists()){
                startTimeFile.createNewFile();
            }
            fos=new ObjectOutputStream(new FileOutputStream(startTimeFile));
            fos.writeObject(startTime);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally{
            try {
                if (fos!=null) {
                    fos.close();
                    return true;
                }
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }
    public DateTime getShiftWorkStartTime(Context context){
        File startTimeFile = new File(context.getFilesDir(),SHIFT_WORK_START_DATE_TIME_NAME);
        ObjectInputStream ois=null;
        DateTime startTime = new DateTime();
        try {
            //获取输入流
            ois=new ObjectInputStream(new FileInputStream(startTimeFile));
            //获取文件中的数据
            startTime = (DateTime) ois.readObject();
            //把数据显示在TextView中
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if (ois!=null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return startTime;
    }
    /**
     * 获得两个日期相差几天
     * @param dateTime1
     * @param dateTime2
     * @return dateTime2 比 datetime1 多几天
     */
    public int getIntervalDays(DateTime dateTime1,DateTime dateTime2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dateTime1.toDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dateTime2.toDate());
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {
            return day2-day1;
        }
    }


    public static String getWeekDay(int dayOfWeek){
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        return weekDays[dayOfWeek];
    }
    public static String toTimeFormat(int hour,int minute){
        return String.format("%02d:%02d",hour,minute);
    }


}
