package com.liubowang.shiftwork.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.liubowang.shiftwork.Base.SWBaseActiviry;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.DisplayUtil;
import com.liubowang.shiftwork.Util.L;
import com.liubowang.shiftwork.Util.NotificationCenter;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;
import com.liubowang.shiftwork.View.CycleItemView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.util.ConvertUtils;

public class SetShiftWorkActivity extends SWBaseActiviry {

    private static final int ITEM_VIEW_HEIGHT = 40;
    private final String TAG = getClass().getSimpleName();
    private EditText shiftWorkNameEt;
    private LinearLayout detailContainer;
    private ImageView reduceIv;
    private ImageView increaseIv;
    private TextView cycleNumTv;
    private LinearLayout cycleItemContainer;
    private List<CycleItem> cycleItemList;
    private List<CycleItemView> cycleItemViews;
    private TextView startTimeTv;
    private int currentCycle = 3;
    private DateTime startDateTime;
    private ConstraintLayout contentContainerCl;
    private Switch openShiftWorkSw;
    private TextView goSetTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_shift_work);
        Toolbar toolbar = findViewById(R.id.tb_tool_bar_set_shift_work);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initUI();
        boolean isOpen = SharedPreferencesUtil.getBoolean(this,SWConst.OPEN_SHIFT_WORK,false);
        openShiftWorkSw.setChecked(isOpen);
        if (isOpen){
            contentContainerCl.setVisibility(View.VISIBLE);
            refreshCycleItems(false);
        }else {
            contentContainerCl.setVisibility(View.INVISIBLE);
        }
        refreshDetail();
        NotificationCenter.defaultCenter().addObserver(
                SWConst.NOTIFICATION_SHIFT_WORK_TYPE_CHANGED,
                shiftWorkTypeChanged);
    }
    private void initUI(){
        contentContainerCl = findViewById(R.id.cl_set_content_container_ssw);
        openShiftWorkSw = findViewById(R.id.sw_open_shfit_work_ssw);
        openShiftWorkSw.setOnCheckedChangeListener(onCheckedOpenShiftWorkListener);
        shiftWorkNameEt = findViewById(R.id.et_shift_work_name_ssw);
        String shiftWorkName = SharedPreferencesUtil.getString(SetShiftWorkActivity.this,SWConst.SHIFT_WORK_NAME,"");
        if (shiftWorkName.length() > 0) shiftWorkNameEt.setText(shiftWorkName);
        detailContainer = findViewById(R.id.ll_detail_container_ssw);
        reduceIv = findViewById(R.id.iv_reduce_ssw);
        increaseIv = findViewById(R.id.iv_increase_ssw);
        cycleNumTv = findViewById(R.id.tv_cycle_num_ssw);
        cycleItemContainer = findViewById(R.id.ll_cycle_item_container_ssw);
        startTimeTv = findViewById(R.id.tv_start_time_ssw);
        startTimeTv.setOnClickListener(onClickListener);
        reduceIv.setOnClickListener(onClickListener);
        increaseIv.setOnClickListener(onClickListener);;
        startDateTime = DataUtil.getInstance().getShiftWorkStartTime(this);
        int year = startDateTime.getYear();
        int month = startDateTime.getMonthOfYear();
        int day = startDateTime.getDayOfMonth();
        startTimeTv.setText(toTimeFormat(year,month,day));
        goSetTv = findViewById(R.id.tv_go_set_ssw);
        goSetTv.setOnClickListener(onClickListener);

    }

    public String toTimeFormat(int year,int month,int day){
        return String.format("%d-%02d-%02d",year,month,day);
    }

    private Observer shiftWorkTypeChanged = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            refreshDetail();
            refreshCycleItems(true);
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedOpenShiftWorkListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferencesUtil.putBoolean(SetShiftWorkActivity.this,SWConst.OPEN_SHIFT_WORK,isChecked);
            if (isChecked) {
                contentContainerCl.setVisibility(View.VISIBLE);
                refreshCycleItems(false);
            }else {
                contentContainerCl.setVisibility(View.INVISIBLE);
            }
            NotificationCenter.defaultCenter().postNotification(SWConst.NOTIFICATION_OPEN_SHIFT_WORK_CHANGED);
        }
    };

    private View.OnClickListener onClickListener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == reduceIv){
                if (cycleItemList.size() > 3){
                    cycleItemList.remove(cycleItemList.size()-1);
                    CycleItemView itemView = cycleItemViews.remove(cycleItemViews.size() -1);
                    cycleItemContainer.removeView(itemView);
                    currentCycle = cycleItemList.size();
                    cycleNumTv.setText(""+currentCycle);
                }
            }
            else if (v == increaseIv){
                CycleItem item = new CycleItem(new ShiftWorkType(getString(R.string.sw_bai_ban)));
                cycleItemList.add(item);
                item.dayOfCycle =  cycleItemList.size() ;
                cycleItemViews.add(addCycleItemView(item));
                currentCycle = cycleItemList.size();
                cycleNumTv.setText(""+currentCycle);
            }
            else if (v == startTimeTv){
                onYearMonthDayPicker(SetShiftWorkActivity.this);
            }
            else if (v == goSetTv){
                Intent intent = new Intent(SetShiftWorkActivity.this,TypeSetActivity.class);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        }
    };
    private void onYearMonthDayPicker(Activity activity) {
        final DatePicker picker = new DatePicker(activity);
        picker.setCanceledOnTouchOutside(true);
        picker.setUseWeight(true);
        picker.setTopPadding(ConvertUtils.toPx(activity, 10));
        picker.setRangeEnd(2080, 12, 31);
        picker.setRangeStart(1970, 1, 1);
        DateTime dateTime = new DateTime();
        picker.setSelectedItem(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
        picker.setResetWhileWheel(false);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
            @Override
            public void onDatePicked(String year, String month, String day) {
                int yearI = Integer.parseInt(year);
                int monthI = Integer.parseInt(month);
                int dayI = Integer.parseInt(day);
                startTimeTv.setText(year+"-"+month+"-"+day);
                startDateTime = new DateTime(yearI,monthI,dayI,8,0);
            }
        });
        picker.show();
    }

    private void refreshDetail(){
        detailContainer.removeAllViews();
        List<ShiftWorkType> shiftWorkTypeList = DataUtil.getInstance().getShiftWorkType(this);
        int height = DisplayUtil.dp2px(this,40);
        for (int i = 0;i < shiftWorkTypeList.size(); i ++){
            ShiftWorkType workType = shiftWorkTypeList.get(i);
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                             LinearLayout.LayoutParams.MATCH_PARENT,
                                                height);
            layoutParams.gravity = Gravity.CENTER;
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textView.setText(getTextWith(workType));
            detailContainer.addView(textView,layoutParams);
        }
    }

    private String getTextWith(ShiftWorkType workType){
        return workType.shiftWork +
                ":      " +
                DataUtil.toTimeFormat(workType.startHour,workType.startMinute) +
                "     -     " +
                DataUtil.toTimeFormat(workType.endHour,workType.endMinute);
    }

    private void refreshCycleItems(boolean justChangeWorkType){

        if (justChangeWorkType){
            List<ShiftWorkType> shiftWorkTypes = DataUtil.getInstance().getShiftWorkType(this);
            Map<String ,ShiftWorkType> shiftWorkTypeMap = new HashMap<>();
            for (ShiftWorkType workType: shiftWorkTypes){
                shiftWorkTypeMap.put(workType.shiftWork,workType);
            }
            for (CycleItem item : cycleItemList){
                ShiftWorkType workType = shiftWorkTypeMap.get(item.workType.shiftWork);
                if (workType != null){
                    item.workType = workType.copy();
                }
            }
        }else {
            cycleItemContainer.removeAllViews();
            cycleItemViews = new ArrayList<>();
            cycleItemList = DataUtil.getInstance().getCycleItems(this);
            currentCycle =  cycleItemList.size();
            for (int i = 0; i < cycleItemList.size(); i ++){
                CycleItem item = cycleItemList.get(i);
                L.d(TAG,"itemShiftWork:"+item.workType.shiftWork);
                item.dayOfCycle = i + 1;
                cycleItemViews.add(addCycleItemView(item));
            }
            cycleNumTv.setText(""+currentCycle);
        }
    }

    private CycleItemView addCycleItemView(CycleItem cycleItem){
        CycleItemView itemView = new CycleItemView(this);
        itemView.setCycleItem(cycleItem);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = DisplayUtil.dp2px(this,ITEM_VIEW_HEIGHT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,height);
        cycleItemContainer.addView(itemView,lp);
        return itemView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_set_shift_work,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save_set_shift_work){
            if (openShiftWorkSw.isChecked()){
                boolean isFirstSave = SharedPreferencesUtil.getBoolean(this,SWConst.FIRST_SAVE_REMIND,true);
                if (isFirstSave){
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setMessage(this.getString(R.string.sw_notes_content))
                            .setPositiveButton(this.getString(R.string.sure),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            toSaveAndFinish();
                                        }
                                    })
                            .create();
                    dialog.show();
                }else {
                    toSaveAndFinish();
                }

            }
            return true;
        }
        else if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSaveAndFinish(){
        String shiftWorkName = shiftWorkNameEt.getText().toString();
        SharedPreferencesUtil.putBoolean(this,SWConst.FIRST_SAVE_REMIND,false);
        SharedPreferencesUtil.putString(SetShiftWorkActivity.this,SWConst.SHIFT_WORK_NAME,shiftWorkName);
        DataUtil.getInstance().saveShiftWorkStartTime(SetShiftWorkActivity.this,startDateTime);
        DataUtil.getInstance().saveCycleItems(SetShiftWorkActivity.this,cycleItemList);
        NotificationCenter.defaultCenter().postNotification(SWConst.NOTIFICATION_SHIFT_WORK_CYCLE_CHANGED);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.defaultCenter().removeObserver(
                SWConst.NOTIFICATION_SHIFT_WORK_TYPE_CHANGED,
                shiftWorkTypeChanged);
    }
}
