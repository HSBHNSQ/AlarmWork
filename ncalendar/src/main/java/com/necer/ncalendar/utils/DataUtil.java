package com.necer.ncalendar.utils;

import android.content.Context;
import android.content.SharedPreferences;


import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/12/21.
 */

public class DataUtil {

    private static DataUtil instance = new DataUtil();
    public static DataUtil getInstance(){return  instance;}

    private static final String SHIFT_WORK_CYCLE_ARRAY_NAME = "SHIFT_WORK_LIST";
    public List<String> getShiftWorkCycleList(Context context){
        File shiftCycle = new File(context.getFilesDir(),SHIFT_WORK_CYCLE_ARRAY_NAME);
        ObjectInputStream ois=null;
        ArrayList shiftCycleList = new ArrayList();
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean isOpen =  Pref.getBoolean("OPEN_SHIFT_WORK",false);
        if (!isOpen){
            return shiftCycleList;
        }
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
    private static final String SHIFT_WORK_START_DATE_TIME_NAME = "START_TIME";
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

}
