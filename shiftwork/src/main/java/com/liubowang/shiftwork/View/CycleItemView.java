package com.liubowang.shiftwork.View;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.L;
import com.liubowang.shiftwork.Util.NotificationCenter;
import com.liubowang.shiftwork.Util.SWConst;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by heshaobo on 2017/12/20.
 */

public class CycleItemView extends ConstraintLayout {

    private final String TAG = getClass().getSimpleName();

    private TextView titleTextView;
    private Spinner spinner;
    private Switch aSwitch;
    private List<String> spinnerDataList;
    private List<ShiftWorkType> shiftWorkTypes;
    private CycleItem cycleItem;
    public CycleItemView(Context context) {
        super(context);
        init(context);
    }

    public CycleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CycleItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setCycleItem(CycleItem cycleItem) {
        this.cycleItem = cycleItem;
        titleTextView.setText(String.format(getContext().getString(R.string.sw_di_tian),cycleItem.dayOfCycle));
        aSwitch.setChecked(cycleItem.isRemind);
        setSpinnerSelection(findWorkTypePosition(cycleItem.workType));
    }

    private int findWorkTypePosition(ShiftWorkType workType){
        int position = 0;
        for(int i = 0;i < shiftWorkTypes.size(); i ++){
            ShiftWorkType swt = shiftWorkTypes.get(i);
            if (swt.shiftWork.equals(workType.shiftWork)){
                position = i;
                break;
            }
        }
        return position;
    }

    public CycleItem getCycleItem() {
        return cycleItem;
    }

    public void setSpinnerSelection(int position){
        spinner.setSelection(position);
    }
    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_cycle_item_view,this,true);
        titleTextView = findViewById(R.id.tv_title_civ);
        spinner = findViewById(R.id.sp_spinner_civ);
        aSwitch = findViewById(R.id.sw_remind_civ);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        setSpinnerDataSource();
        aSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        NotificationCenter.defaultCenter().addObserver(
                SWConst.NOTIFICATION_SHIFT_WORK_TYPE_CHANGED,
                shiftWorkTypeChanged);

    }

    private void setSpinnerDataSource(){
        spinnerDataList = getSpinnerList();
        CSpinnerAdapter adapter = new CSpinnerAdapter(spinnerDataList);
        spinner.setAdapter(adapter);
        if (cycleItem != null){
            setCycleItem(cycleItem);
        }
    }


    private List<String> getSpinnerList(){
        shiftWorkTypes = DataUtil.getInstance().getShiftWorkType(getContext());
        List<String> list = new ArrayList<>();
        for (ShiftWorkType workType:shiftWorkTypes){
            list.add(workType.shiftWork);
        }
        return list;
    }
    private Observer shiftWorkTypeChanged = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            setSpinnerDataSource();
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            cycleItem.isRemind = isChecked;
        }
    };

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            L.d(TAG,spinnerDataList.get(position));
            cycleItem.workType = shiftWorkTypes.get(position).copy();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    class CSpinnerAdapter extends BaseAdapter {

        private List<String> dataSource = new ArrayList<>();
        public CSpinnerAdapter(List<String> dataSource){
            super();
            this.dataSource = dataSource;
        }

        @Override
        public int getCount() {
            return dataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return dataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=LayoutInflater.from(parent.getContext());

            if(convertView==null)
            {
                convertView = inflater.inflate(R.layout.layout_cycle_item_spinner,null);
            }
            TextView textView = convertView.findViewById(R.id.tv_title_cis);
            textView.setText(dataSource.get(position));
            return convertView;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.defaultCenter().removeObserver(
                SWConst.NOTIFICATION_SHIFT_WORK_TYPE_CHANGED,
                shiftWorkTypeChanged);
    }
}
