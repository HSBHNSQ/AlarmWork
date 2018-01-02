package com.liubowang.shiftwork.Activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liubowang.shiftwork.R;
import com.necer.ncalendar.utils.MyLog;


/**
 * Created by necer on 2017/6/7.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;

    public NotesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_item, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        TextView textView = holder.textView;
        String sw_notes = "\n\n\n" + context.getString(R.string.sw_notes_content);
        textView.setText(sw_notes);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.d("position:::::" + position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView =  itemView.findViewById(R.id.tv_notes);
        }
    }

}




