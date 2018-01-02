package com.liubowang.shiftwork.View;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.liubowang.shiftwork.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/12/22.
 */

public class EditTextDialog extends AlertDialog {

    private Button cancelButton ;
    private Button sureButon;
    private EditText editText;
    private TextView titleTv;
    private List<String> hasDataSource = new ArrayList<>();

    public EditTextDialog(@NonNull Context context) {
        super(context);
    }

    public EditTextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public EditTextDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit_text_dialog);
        cancelButton = findViewById(R.id.b_cancel_dialog);
        sureButon = findViewById(R.id.b_sure_dialog);
        editText = findViewById(R.id.et_edit_text_dialog);
        cancelButton.setOnClickListener(onClickListener);
        sureButon.setOnClickListener(onClickListener);

//        titleTv = findViewById(R.id.)
    }

    public void setHasDataSource(List<String> hasDataSource) {
        this.hasDataSource = hasDataSource;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == cancelButton){
                if (onEditTextDialogListener != null){
                    onEditTextDialogListener.onCancel();
                }
                dismiss();
            }
            else if(v == sureButon){
                String shiftWork = editText.getText().toString();
                if (shiftWork.length() == 0){
                    startEditTextShake();
                }else {
                    boolean has = false;
                    for (String s : hasDataSource){
                        if (s.equals(shiftWork)){
                            has = true;
                            startEditTextShake();
                            break;
                        }
                    }
                    if (!has){
                        if (onEditTextDialogListener != null){
                            onEditTextDialogListener.onEditTextDone(shiftWork);
                        }
                        dismiss();
                    }
                }
            }
        }
    };

    @Override
    public void show() {
        super.show();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
//加上下面这一行弹出对话框时软键盘随之弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void startEditTextShake(){
        Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
        editText.startAnimation(shake);
    }

    private OnEditTextDialogListener onEditTextDialogListener ;

    public void setOnEditTextDialogListener(OnEditTextDialogListener onEditTextDialogListener) {
        this.onEditTextDialogListener = onEditTextDialogListener;
    }

    public interface OnEditTextDialogListener{
        void onEditTextDone(String s);
        void onCancel();
    }

}
