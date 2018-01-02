package com.liubowang.shiftwork.Model;

import java.io.Serializable;

/**
 * Created by heshaobo on 2017/12/23.
 */

public class CycleItem implements Serializable {
    private static final long serialVersionUID = -3007036686930344530L;
    public String shiftWorkName;
    public int dayOfCycle;
    public ShiftWorkType workType;
    public boolean isRemind = false;
    private String UUID = java.util.UUID.randomUUID().toString();
    private CycleItem(){ super();  }

    public CycleItem(ShiftWorkType workType){
        super();
        this.workType = workType;
    }

    public String getUUID() {
        return UUID;
    }

    public CycleItem copy(){
        CycleItem item = new CycleItem();
        item.workType = this.workType.copy();
        if (this.shiftWorkName != null){
            item.shiftWorkName = new String(shiftWorkName);
        }
        item.dayOfCycle = this.dayOfCycle;
        item.isRemind = this.isRemind;
        return item;
    }
}
