package com.liubowang.shiftwork.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.liubowang.shiftwork.Base.SWBaseActiviry;
import com.liubowang.shiftwork.R;

public class ShowLogActivity extends SWBaseActiviry {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_log);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Intent intent = getIntent();
        String log = intent.getStringExtra("show_log");
        TextView textView = findViewById(R.id.tv_show_log);
        textView.setText(log);
    }
}
