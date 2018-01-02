package com.liubowang.shiftwork.Base;

import android.app.Application;
import android.content.Context;

import com.liubowang.shiftwork.Activity.MainActivity;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;

import java.util.ArrayList;

/**
 * Created by heshaobo on 2017/11/14.
 */

public class SWApplication extends Application {

    private static Context mContext;
    public static Context getContext(){
        return mContext;
    }
    private final String TAG = getClass().getCanonicalName();
    private static SWApplication sharedApplication;
    public static SWApplication getSharedApplication() {
        return sharedApplication;
    }
    public boolean bannerADLoadSuccess = true;
    @Override
    public void onCreate() {
        super.onCreate();
        sharedApplication = this;
        mContext = getApplicationContext();
        configAD();
        if (SharedPreferencesUtil.getBoolean(this, SWConst.IS_FIRST_LAUNCH_APP,true)){
            firstSet();
        }
    }

    private void firstSet(){
        ArrayList<ShiftWorkType> shiftWorkTypeArrayList = new ArrayList<>();
        ShiftWorkType shiftWorkType1 = new ShiftWorkType(getString(R.string.sw_bai_ban));
        ShiftWorkType shiftWorkType2 = new ShiftWorkType( getString(R.string.sw_ye_ban));
        shiftWorkType2.startHour = 21;
        shiftWorkType2.endHour = 6;
        ShiftWorkType shiftWorkType3 = new ShiftWorkType( getString(R.string.sw_xiu_ban));
        shiftWorkTypeArrayList.add(shiftWorkType1);
        shiftWorkTypeArrayList.add(shiftWorkType2);
        shiftWorkTypeArrayList.add(shiftWorkType3);
        DataUtil.getInstance().saveShiftWorkType(this,shiftWorkTypeArrayList);

        ArrayList<CycleItem> cycleItemArrayList = new ArrayList<>();
        CycleItem cycleItem1 = new CycleItem(shiftWorkType1);
        CycleItem cycleItem2 = new CycleItem(shiftWorkType2);
        CycleItem cycleItem3 = new CycleItem(shiftWorkType3);
        cycleItemArrayList.add(cycleItem1);
        cycleItemArrayList.add(cycleItem2);
        cycleItemArrayList.add(cycleItem3);
        DataUtil.getInstance().saveCycleItems(this,cycleItemArrayList);
        SharedPreferencesUtil.putBoolean(this,SWConst.VIBRATE_SWITCH,true);
        SharedPreferencesUtil.putInt(this,SWConst.LEAD_TIME,0);
        SharedPreferencesUtil.putBoolean(this,SWConst.IS_FIRST_LAUNCH_APP,false);
    }

    private void configAD(){

    }


}
