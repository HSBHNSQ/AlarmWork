package com.liubowang.shiftwork.View;

import android.content.Context;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.liubowang.shiftwork.R;
import com.liubowang.shiftwork.Util.L;
import com.liubowang.shiftwork.Util.NotificationCenter;
import com.liubowang.shiftwork.Util.SWConst;
import com.liubowang.shiftwork.Util.SharedPreferencesUtil;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by heshaobo on 2017/12/15.
 */

public class LeftMenuContentView extends ConstraintLayout {

    private final String TAG = getClass().getSimpleName();
    private Spinner spinner;
    private Switch  aSwitch;
    private TextView shiftWorkTypeTv ;
    private TextView suggestionTv ;
    private TextView commentTv ;
    private TextView setRingTv;
    private TextView ringNameTv;


    public LeftMenuContentView(Context context) {
        super(context);
        init(context);
    }

    public LeftMenuContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LeftMenuContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.layout_left_menu_content_view,this,true);
        spinner = findViewById(R.id.si_spinner_remind_lmcv);
        aSwitch = findViewById(R.id.sh_switch_vibration_lmcv);
        boolean isOn = SharedPreferencesUtil.getBoolean(context, SWConst.VIBRATE_SWITCH,true);
        aSwitch.setChecked(isOn);
        shiftWorkTypeTv = findViewById(R.id.tv_shift_work_type_lmcv);
        suggestionTv = findViewById(R.id.tv_suggestion_lmcv);
        commentTv = findViewById(R.id.tv_comment_lmcv);
        shiftWorkTypeTv.setOnClickListener(onClickListener);
        suggestionTv.setOnClickListener(onClickListener);
        commentTv.setOnClickListener(onClickListener);
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        aSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        setRingTv = findViewById(R.id.tv_set_ring_lmcv);
        setRingTv.setOnClickListener(onClickListener);
        ringNameTv = findViewById(R.id.tv_ring_name_lmcv);
        ringNameTv.setOnClickListener(onClickListener);
        String ringName = SharedPreferencesUtil.getString(context,SWConst.RING_NAME,"");
        ringNameTv.setText(ringName);
        int leadTime = SharedPreferencesUtil.getInt(context,SWConst.LEAD_TIME,0);
        int position = getPositionLeadTime(leadTime);
        spinner.setSelection(position);
        NotificationCenter.defaultCenter().addObserver(SWConst.NOTIFICATION_RING_CHANGED,ringChangeObserver);

    }

    private Observer ringChangeObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            String ringName = SharedPreferencesUtil.getString(getContext(),SWConst.RING_NAME,"");
            ringNameTv.setText(ringName);
        }
    };



    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (onLeftMenuListener != null){
                if (isChecked) {
                    Vibrator vibrator = (Vibrator) getContext().getSystemService(getContext().VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
                        vibrator.vibrate(2000);
                    }
                }
                onLeftMenuListener.onVibrateSwitch(isChecked);
            }
        }
    };
//<string-array name="remind_advance_times">
//        <item>无</item>
//        <item>10分钟</item>
//        <item>30分钟</item>
//        <item>1小时</item>
//        <item>2小时</item>
// </string-array>

    private int getPositionLeadTime(int minute){
        int position = 0;
        if (minute == 0)
            position = 0;
        else if (minute == 10)
            position = 1;
        else if (minute == 30)
            position = 2;
        else if (minute == 60)
            position = 3;
        else if (minute == 120)
            position = 4;
        return position;
    }

    private int getLeadTime(int position){
        int minute = 0;
        if (position == 0)
            minute = 0;
        else if (position == 1)
            minute = 10;
        else if (position == 2)
            minute = 30;
        else if (position == 3)
            minute = 60;
        else if (position == 4)
            minute = 120;
        return minute;
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (onLeftMenuListener != null){
                    String[] remind_advance_times = getResources().getStringArray(R.array.remind_advance_times);
                    L.d(TAG,remind_advance_times[position]);
                    int minute = getLeadTime(position);
                    onLeftMenuListener.onRemindChanged(minute);
                }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == shiftWorkTypeTv){
                if (onLeftMenuListener != null){
                    onLeftMenuListener.onShiftWorkTypeClick();
                }
            }
            else if (view == suggestionTv){
                if (onLeftMenuListener != null){
                    onLeftMenuListener.onSuggestionClick();
                }
            }
            else if (view == commentTv){
                if (onLeftMenuListener != null){
                    onLeftMenuListener.onCommentClick();
                }
            }
            else if (view == ringNameTv || view == setRingTv){
                if (onLeftMenuListener != null){
                    onLeftMenuListener.onChooseRingClick();
                }
            }
        }
    };





    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter
                .defaultCenter()
                .removeObserver(SWConst.NOTIFICATION_RING_CHANGED,ringChangeObserver);
    }

    private OnLeftMenuListener onLeftMenuListener ;

    public void setOnLeftMenuListener(OnLeftMenuListener onLeftMenuListener) {
        this.onLeftMenuListener = onLeftMenuListener;
    }

    public interface OnLeftMenuListener{
        void onRemindChanged(int minute);
        void onVibrateSwitch(boolean open);
        void onShiftWorkTypeClick();
        void onSuggestionClick();
        void onCommentClick();
        void onChooseRingClick();
    }
}
