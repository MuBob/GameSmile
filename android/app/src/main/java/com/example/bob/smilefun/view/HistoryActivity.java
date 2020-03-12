package com.example.bob.smilefun.view;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.GameInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private static final String TAG = "HistoryActivityTAG";
    private RecyclerView recyclerView;
    private List<GameInfo> historyList;
    private HistoryAdapter historyAdapter;
    private Handler handler;
    private RefreshThread refreshThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView=findViewById(R.id.recycler_record);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false));
        historyList=new ArrayList<>();
        historyAdapter=new HistoryAdapter(this, historyList);
        recyclerView.setAdapter(historyAdapter);
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what==1){
                    Log.i(TAG, "HistoryActivity.handleMessage: list"+historyList);
                    historyAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
        refreshHistory();

    }

    private void refreshHistory() {
        if(refreshThread==null||!refreshThread.isAlive()){
            refreshThread=new RefreshThread();
            refreshThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        if(refreshThread!=null&&refreshThread.isAlive()){
            refreshThread.interrupt();
        }
        super.onDestroy();
    }

    private class RefreshThread extends Thread{

        @Override
        public void run() {
            super.run();
            Cursor query = getContentResolver().query(GameInfo.URI_INFO, null, null, null, null);
            if(query!=null){
                historyList.clear();
                if(query.moveToFirst()){
                    do{
                        GameInfo gameInfo=new GameInfo(query);
                        historyList.add(gameInfo);
                    }while(query.moveToNext()&&!isInterrupted());
                }
                if(!query.isClosed()){
                    query.close();
                }
            }
            Collections.reverse(historyList);
            handler.sendEmptyMessage(1);
        }
    }


}
