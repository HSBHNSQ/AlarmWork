package com.liubowang.shiftwork.Model;

import java.io.Serializable;

/**
 * Created by heshaobo on 2017/12/21.
 */

public class ShiftWorkType implements Serializable{
    private static final long serialVersionUID = -2195877236279491965L;
    public String shiftWork;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;

    private ShiftWorkType(){
        super();
    }
    public ShiftWorkType(String name){
        super();
        shiftWork = name;
        startHour = 9;
        startMinute = 0;
        endHour = 18;
        endMinute = 0;
    }

    public ShiftWorkType copy(){
        ShiftWorkType workType = new ShiftWorkType(this.shiftWork);
        workType.startHour = this.startHour;
        workType.startMinute = this.startMinute;
        workType.endHour = this.endHour;
        workType.endMinute = this.endMinute;
        return workType;
    }


    @Override
    public String toString() {
        String s =
                super.toString()+"{" + "\n"
        +"shiftWork:" + shiftWork + "\n"
        +"startHour:" + startHour + "\n"
        +"startMinute:" + startMinute + "\n"
        +"endHour:" + endHour + "\n"
        +"endMinute:" + endMinute + "}\n";
        return s;
    }
}
