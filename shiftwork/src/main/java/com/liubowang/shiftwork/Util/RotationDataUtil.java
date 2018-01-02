package com.liubowang.shiftwork.Util;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by heshaobo on 2017/12/18.
 */

public class RotationDataUtil implements Parcelable{
    public String today_shift_work;
    public String today_work_time;
    public String next1_shift_work;
    public String next1_work_time;
    public String next2_shift_work;
    public String next2_work_time;
    public String today_day;
    public String today_month;
    public String today_week;


    public RotationDataUtil(){
        super();
    }

    protected RotationDataUtil(Parcel in) {
        today_shift_work = in.readString();
        today_work_time = in.readString();
        next1_shift_work = in.readString();
        next1_work_time = in.readString();
        next2_shift_work = in.readString();
        next2_work_time = in.readString();
        today_day = in.readString();
        today_month = in.readString();
        today_week = in.readString();
    }

    public static final Creator<RotationDataUtil> CREATOR = new Creator<RotationDataUtil>() {
        @Override
        public RotationDataUtil createFromParcel(Parcel in) {
            return new RotationDataUtil(in);
        }

        @Override
        public RotationDataUtil[] newArray(int size) {
            return new RotationDataUtil[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(today_shift_work);
        dest.writeString(today_work_time);
        dest.writeString(next1_shift_work);
        dest.writeString(next1_work_time);
        dest.writeString(next2_shift_work);
        dest.writeString(next2_work_time);
        dest.writeString(today_day);
        dest.writeString(today_month);
        dest.writeString(today_week);
    }

}
