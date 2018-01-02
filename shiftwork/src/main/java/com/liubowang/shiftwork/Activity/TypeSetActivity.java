package com.liubowang.shiftwork.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liubowang.shiftwork.Base.SWBaseActiviry;
import com.liubowang.shiftwork.Model.CycleItem;
import com.liubowang.shiftwork.Model.ShiftWorkType;
import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.DataUtil;
import com.liubowang.shiftwork.Util.DisplayUtil;
import com.liubowang.shiftwork.Util.NotificationCenter;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;
import com.liubowang.shiftwork.View.EditTextDialog;
import com.liubowang.shiftwork.View.TypeSetItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeSetActivity extends SWBaseActiviry implements TypeSetItemView.OnTypeSetItemListener{

    private final String TAG = getClass().getSimpleName();
    private static final int ITEM_VIEW_HEIGHT = 40;
    private LinearLayout itemContainerView;
    private List<TypeSetItemView> typeSetItemViews = new ArrayList<>();
    private List<ShiftWorkType> shiftWorkTypes;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_set);
        Toolbar toolbar = findViewById(R.id.tb_toolbar_type_set);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        itemContainerView = findViewById(R.id.ll_item_content_type_set);
        imageView = findViewById(R.id.iv_add_item_type_set);
        imageView.setOnClickListener(onClickListener);
        configItem();
    }

    private void configItem (){
        shiftWorkTypes = DataUtil.getInstance().getShiftWorkType(this);
        for (int i = 0; i < shiftWorkTypes.size(); i ++){
            ShiftWorkType workType = shiftWorkTypes.get(i);
            boolean edit = true;
            if (i <= 2){
                edit = false;
            }
            typeSetItemViews.add(addItemView(workType,edit));
        }
    }

    private TypeSetItemView  addItemView(ShiftWorkType workType,boolean edit){
        TypeSetItemView itemView = new TypeSetItemView(this);
        itemView.setShiftWorkType(workType);
        itemView.setOnTypeSetItemListener(this);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = DisplayUtil.dp2px(this,ITEM_VIEW_HEIGHT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,height);
        itemContainerView.addView(itemView,lp);
        if (edit){
            itemView.setEidt(edit);
        }
        return itemView;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_type_set,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void resetCycleItemsAndAlarm(){
        List<CycleItem> shiftWorkCycleItems = DataUtil.getInstance().getCycleItems(this);
        Map<String ,ShiftWorkType> shiftWorkTypeMap = new HashMap<>();
        for (ShiftWorkType workType: shiftWorkTypes){
            shiftWorkTypeMap.put(workType.shiftWork,workType);
        }
        for (CycleItem item : shiftWorkCycleItems){
            ShiftWorkType workType = shiftWorkTypeMap.get(item.workType.shiftWork);
            if (workType != null){
                item.workType = workType.copy();
            }
        }
        DataUtil.getInstance().saveCycleItems(this,shiftWorkCycleItems);
        DataUtil.getInstance().deleteAllAlarm(this);
        String ringName = SharedPreferencesUtil.getString(this,SWConst.RING_NAME,"");
        String ringId = SharedPreferencesUtil.getString(this,SWConst.RING_ID,"");
        DataUtil.getInstance().resetAllAlarm(this,ringName,ringId);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_save_type_set){
            DataUtil.getInstance().saveShiftWorkType(this,shiftWorkTypes);
            resetCycleItemsAndAlarm();
            NotificationCenter.defaultCenter().postNotification(SWConst.NOTIFICATION_SHIFT_WORK_TYPE_CHANGED);
            finish();
            return true;
        }
        else if (id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onDeleteClick(final TypeSetItemView itemView) {
        if (typeSetItemViews.contains(itemView)){
            int index = typeSetItemViews.indexOf(itemView);
            itemContainerView.removeView(itemView);
            typeSetItemViews.remove(itemView);
            shiftWorkTypes.remove(index);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_add_item_type_set){
                EditTextDialog dialog = new EditTextDialog(TypeSetActivity.this);
                dialog.show();
                List<String> hasList = new ArrayList<>();
                for (ShiftWorkType workType:shiftWorkTypes){
                    hasList.add(workType.shiftWork);
                }
                dialog.setHasDataSource(hasList);
                dialog.setOnEditTextDialogListener(new EditTextDialog.OnEditTextDialogListener() {
                    @Override
                    public void onEditTextDone(String s) {
                        addItemClick(s);
                    }
                    @Override
                    public void onCancel() {

                    }
                });

            }
        }
    };



    private void addItemClick(String shiftWork){
        ShiftWorkType shiftWorkType = new ShiftWorkType(shiftWork);
        shiftWorkTypes.add(shiftWorkType);
        typeSetItemViews.add(addItemView(shiftWorkType,true));
    }



}
