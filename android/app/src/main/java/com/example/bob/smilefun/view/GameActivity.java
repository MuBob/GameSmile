package com.example.bob.smilefun.view;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.GameInfo;
import com.example.bob.smilefun.db.PreferenceSetting;
import com.example.bob.smilefun.utils.ImageUtil;
import com.example.bob.smilefun.utils.MeasureUtil;
import com.example.bob.smilefun.utils.SPUtil;

import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivityTAG";
    private LinearLayout containtLayout;
    private TextView levelText, timeText;
    private AlertDialog successDialog, failDialog, existDialog;
    private GameInfo gameInfo;
    private Timer timer;
    private TimerTask task;
    private Handler timerHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        containtLayout = findViewById(R.id.ll_containt);
        levelText=findViewById(R.id.tv_level);
        timeText=findViewById(R.id.tv_time);
        gameInfo=new GameInfo();
        initLayout();
        AlertDialog.Builder successBuilder = new AlertDialog.Builder(GameActivity.this);
        successBuilder.setNegativeButton(R.string.exist, exitListener);
        successBuilder.setPositiveButton(R.string.next_level, nextListener);
        successBuilder.setTitle(R.string.title_success);
        successDialog = successBuilder.create();
        AlertDialog.Builder failBuilder = new AlertDialog.Builder(GameActivity.this);
        failBuilder.setNegativeButton(R.string.exist, exitListener);
        failBuilder.setPositiveButton(R.string.retry, retryListener);
        failBuilder.setTitle(R.string.title_fail);
        failDialog = failBuilder.create();
        AlertDialog.Builder existBuild = new AlertDialog.Builder(GameActivity.this);
        existBuild.setNegativeButton(R.string.exist_false, null);
        existBuild.setPositiveButton(R.string.exist_success, exitListener);
        existBuild.setTitle(R.string.title_exist);
        existDialog = existBuild.create();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = timerHandler.obtainMessage();
                message.what=1;
                timerHandler.sendMessage(message);
            }
        };
        timerHandler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what==1){
                    if(gameInfo.getState()==GameInfo.STATE_RUNING){
                        gameInfo.setLevelTime(gameInfo.getLevelTime() + 1);
                        timeText.setText(String.format(getString(R.string.game_time_d),gameInfo.getLevelTime()));
                    }
                    return true;
                }
                return false;
            }
        });
        timer.schedule(task,0,1000);
    }

    /**
     * 创建布局
     */
    private void initLayout() {
        gameInfo.setLevel(gameInfo.getLevel() + 1);
        saveGameInfo();
        levelText.setText(String.format(getString(R.string.cur_level_d), gameInfo.getLevel()));
        int lineCount = SPUtil.build(getApplicationContext()).get(PreferenceSetting.NUM_LINE, PreferenceSetting.COUNT_LINE);
        int columnCount = SPUtil.build(getApplicationContext()).get(PreferenceSetting.NUM_COLUMN, PreferenceSetting.COUNT_COLUMN);
        gameInfo.setDifficult(lineCount, columnCount);
        //ImageView fit center of Screen
        int width, height;
        int screenWidth = MeasureUtil.getScreenWidth(this);
        int screenHeight = MeasureUtil.getScreenHeight(this);
        width = screenWidth / columnCount;
        int heightWithWidth = (int) (width / MeasureUtil.AspectRatio);
        height = screenHeight / lineCount;
        int widthWithHeight = (int) (height * MeasureUtil.AspectRatio);
        if (width < widthWithHeight) {
            height = heightWithWidth;
        } else {
            width = widthWithHeight;
        }

        ImageUtil imageUtil = new ImageUtil(this);
        imageUtil.beginCalculate(lineCount * columnCount);
        containtLayout.removeAllViews();
        for (int i = 0; i < lineCount; i++) {
            LinearLayout rowLayout = createRowLayout();
            for (int j = 0; j < columnCount; j++) {
                ImageView imageView = createImageView(width, height);
                imageUtil.calculate();
                imageView.setImageResource(imageUtil.getResId());
                imageView.setOnClickListener(imageUtil.isBingo() ? successListener : failListener);
                rowLayout.addView(imageView);
            }
            containtLayout.addView(rowLayout);
        }
        imageUtil.endCalculate();
    }

    private void saveGameInfo() {
        Log.i(TAG, "GameActivity.saveGameInfo: save game info="+gameInfo);
        if(gameInfo.getState()==GameInfo.STATE_START){
            gameInfo.setState(GameInfo.STATE_RUNING);
            gameInfo.setStartTime(System.currentTimeMillis());
            Uri insert = getContentResolver().insert(GameInfo.URI_INFO, gameInfo.getCV());
            long id = ContentUris.parseId(insert);
            if(id<0){
                Toast.makeText(GameActivity.this, R.string.faile_save_record, Toast.LENGTH_SHORT).show();
            }
            gameInfo.setId(id);
        }else if(gameInfo.getState()==GameInfo.STATE_RUNING){
            if(gameInfo.getId()>=0){
                getContentResolver().update(ContentUris.withAppendedId(GameInfo.URI_INFO,gameInfo.getId()), gameInfo.getCV(),null, null);
            }
        }else if(gameInfo.getState()==GameInfo.STATE_END){
            gameInfo.setEndTime(System.currentTimeMillis());
            if(gameInfo.getId()>=0){
                getContentResolver().update(ContentUris.withAppendedId(GameInfo.URI_INFO, gameInfo.getId()), gameInfo.getCV(),null, null);
            }
        }
    }

    /**
     * 创建每一行布局
     *
     * @return
     */
    private LinearLayout createRowLayout() {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = 5;
        layoutParams.rightMargin = 10;
        layoutParams.leftMargin = 10;
        rowLayout.setLayoutParams(layoutParams);
        rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        return rowLayout;
    }

    /**
     * 创建每张图片
     *
     * @return
     */
    private ImageView createImageView(int width, int height) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, height);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(1, 0, 1, 0);
        return imageView;
    }

    View.OnClickListener successListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            successDialog.setMessage(String.format(getString(R.string.success_level_d), gameInfo.getLevel()));
            successDialog.show();
        }
    };
    View.OnClickListener failListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            failDialog.setMessage(String.format(getString(R.string.fail_level_d), gameInfo.getLevel()));
            failDialog.show();
        }
    };

    private DialogInterface.OnClickListener retryListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            gameInfo.setState(GameInfo.STATE_END);
            saveGameInfo();
            gameInfo.clear();
            initLayout();
        }
    };
    private DialogInterface.OnClickListener nextListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            initLayout();
        }
    };
    private DialogInterface.OnClickListener exitListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            gameInfo.setState(GameInfo.STATE_END);
            saveGameInfo();
            task.cancel();
            setResult(RESULT_OK);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        existDialog.show();
//        super.onBackPressed();
    }
}
