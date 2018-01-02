package com.necer.ncalendar.listener;

import org.joda.time.DateTime;

/**
 * Created by necer on 2017/6/13.
 */

public interface OnClickMonthViewListener {

    void onClickCurrentMonth(DateTime dateTime,String shiftWork);

    void onClickLastMonth(DateTime dateTime,String shiftWork);

    void onClickNextMonth(DateTime dateTime,String shiftWork);

}
