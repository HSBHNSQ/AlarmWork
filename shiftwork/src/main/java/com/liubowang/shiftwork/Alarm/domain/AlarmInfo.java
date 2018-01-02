package com.liubowang.shiftwork.Alarm.domain;


import com.liubowang.shiftwork.Alarm.dao.AlarmInfoDao;
import com.necer.ncalendar.utils.Utils;

import java.io.Serializable;

/**
 * Created by Joe on 2016/1/11.
 */
public class AlarmInfo implements Serializable {
    private static final long serialVersionUID = -3600522176879730282L;
    private int Hour;
    private int Minute;
    private int LazyLevel;
    private String Ring ;
    private String Tag;
    private int[] dayOfWeek;
    private String ringResId;
    private String UUID;

    public String getRingResId() {
        return ringResId;
    }

    public void setRingResId(String ringResId) {
        this.ringResId = ringResId;
    }

    public  AlarmInfo(){
        super();
        UUID = java.util.UUID.randomUUID().toString();
    }

    public String getId(){
        String id=""+Hour+Minute+ AlarmInfoDao.getDataDayofWeek(dayOfWeek);
        return id;
    }

    @Override
    public String toString() {
        return "AlarmInfo{" +
                getId()+
                ", Tag='" + Tag + '\'' +
                ", Ring='" + Ring + '\'' +
                ", Hour='" + Hour + '\'' +
                ", Minute='" + Minute + '\'' +
                ", LazyLevel=" + LazyLevel +
                '}';
    }

    public int getHour() {
        return Hour;
    }

    public void setHour(int hour) {
        Hour = hour;
    }

    public int getMinute() {
        return Minute;
    }

    public void setMinute(int minute) {
        Minute = minute;
    }

    public int getLazyLevel() {
        return LazyLevel;
    }

    public void setLazyLevel(int lazyLevel) {
        LazyLevel = lazyLevel;
    }


    public String getRing() {
        return Ring;
    }

    public void setRing(String ring) {
        Ring = ring;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public int[] getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int[] dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
}
