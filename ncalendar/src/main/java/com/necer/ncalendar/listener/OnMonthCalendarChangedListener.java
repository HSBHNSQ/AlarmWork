package com.necer.ncalendar.listener;

import org.joda.time.DateTime;

/**
 * Created by necer on 2017/9/20.
 * QQ群:127278900
 */

public interface OnMonthCalendarChangedListener {
    void onMonthCalendarChanged(DateTime dateTime,String shiftWork);
}
