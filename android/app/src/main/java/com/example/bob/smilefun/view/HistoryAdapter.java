package com.example.bob.smilefun.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.GameInfo;
import com.example.bob.smilefun.utils.DateUtil;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ItemView> {

    private static final String TAG = "HistoryActivityTAG";
    private Context context;
    private List<GameInfo> list;

    public HistoryAdapter(Context context, List<GameInfo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ItemView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_record, parent, false);
        return new ItemView(view);
    }

    @Override
    public void onBindViewHolder(ItemView holder, int position) {
        GameInfo gameInfo = list.get(position);
        Log.i(TAG, "HistoryAdapter.onBindViewHolder: position=" + position + ", gameInfo=" + gameInfo);
        holder.indexText.setText(String.valueOf(position + 1));
        holder.dateText.setText(DateUtil.getFormat(gameInfo.getStartTime()));
        holder.levelText.setText(String.valueOf(gameInfo.getLevel() - 1));
        if(gameInfo.getState()==GameInfo.STATE_END){
            holder.timeText.setText(DateUtil.getDuration(gameInfo.getStartTime(), gameInfo.getEndTime()));
        }else{
            holder.timeText.setText("--");
        }
        holder.difficultText.setText(String.format("%dx%d", gameInfo.getDifficultLine(), gameInfo.getDifficultRow()));
    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ItemView extends RecyclerView.ViewHolder {
        private TextView indexText, dateText, levelText, timeText, difficultText;

        public ItemView(View itemView) {
            super(itemView);
            indexText = itemView.findViewById(R.id.item_record_index);
            dateText = itemView.findViewById(R.id.item_record_date);
            levelText = itemView.findViewById(R.id.item_record_level);
            timeText = itemView.findViewById(R.id.item_record_time);
            difficultText = itemView.findViewById(R.id.item_record_difficult);
        }
    }

}
