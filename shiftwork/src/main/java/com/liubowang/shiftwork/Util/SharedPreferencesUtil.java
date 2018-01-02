package com.liubowang.shiftwork.Util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by heshaobo on 2017/12/21.
 */

public class SharedPreferencesUtil {

    public static void putBoolean(Context context, String key, Boolean values){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        Pref.edit().putBoolean(key,values).commit();
    }
    public static Boolean getBoolean(Context context,String key,Boolean defvalues){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        return  Pref.getBoolean(key,defvalues);
    }

    public static void putlong(Context context,String key,long values){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        Pref.edit().putLong(key,values).commit();
    }

    public static long getlong(Context context,String key,long defvalues){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        return Pref.getLong(key,defvalues);
    }
    public static void remove(Context context,String key){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        Pref.edit().remove(key);
    }

    public static void putString(Context context,String key,String values){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        Pref.edit().putString(key, values).commit();
    }
    public static String getString(Context context,String key,String defvalues){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        return Pref.getString(key,defvalues);
    }

    public static void putInt(Context context,String key,int values){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        Pref.edit().putInt(key, values).commit();
    }
    public static int getInt(Context context,String key,int defvalues){
        SharedPreferences Pref= context.getSharedPreferences("config",Context.MODE_MULTI_PROCESS);
        return Pref.getInt(key, defvalues);
    }
}
