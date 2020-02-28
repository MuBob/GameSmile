package com.example.bob.smilefun.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.db.GameSetting;
import com.example.bob.smilefun.utils.ImageUtil;
import com.example.bob.smilefun.utils.MeasureUtil;
import com.example.bob.smilefun.utils.SPUtil;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivityTAG";
    private LinearLayout containtLayout;
    private int lineCount;
    private int columnCount;
    private  AlertDialog successDialog, failDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        containtLayout = findViewById(R.id.ll_containt);
        initLayout();
        AlertDialog.Builder successBuilder=new AlertDialog.Builder(GameActivity.this);
        successBuilder.setNegativeButton("退出", exitListener);
        successBuilder.setPositiveButton("下一关", nextListener);
        successBuilder.setTitle("恭喜您");
        successDialog = successBuilder.create();
        AlertDialog.Builder failBuilder=new AlertDialog.Builder(GameActivity.this);
        failBuilder.setNegativeButton("退出", exitListener);
        failBuilder.setPositiveButton("重新开始", retryListener);
        failBuilder.setTitle("抱歉");
        failDialog=failBuilder.create();
    }

    /**
     * 创建布局
     */
    private void initLayout() {
        lineCount=SPUtil.build(getApplicationContext()).get(GameSetting.NUM_LINE, GameSetting.COUNT_LINE);
        columnCount=SPUtil.build(getApplicationContext()).get(GameSetting.NUM_COLUMN, GameSetting.COUNT_COLUMN);
        //ImageView fit center of Screen
        int width,height;
        int screenWidth = MeasureUtil.getScreenWidth(this);
        int screenHeight = MeasureUtil.getScreenHeight(this);
        width=screenWidth/columnCount;
        int heightWithWidth=(int)(width/MeasureUtil.AspectRatio);
        height=screenHeight/lineCount;
        int widthWithHeight=(int)(height*MeasureUtil.AspectRatio);
        if(width<widthWithHeight){
            height=heightWithWidth;
        }else{
            width=widthWithHeight;
        }

        ImageUtil imageUtil=new ImageUtil();
        imageUtil.beginCalculate(lineCount*columnCount);
        containtLayout.removeAllViews();
        for (int i = 0; i < lineCount; i++) {
            LinearLayout rowLayout=createRowLayout();
            for (int j = 0; j < columnCount; j++) {
                ImageView imageView=createImageView(width, height);
                imageUtil.calculate();
                imageView.setImageResource(imageUtil.getResId());
                imageView.setOnClickListener(imageUtil.isBingo()?successListener:failListener);
                rowLayout.addView(imageView);
            }
            containtLayout.addView(rowLayout);
        }
        imageUtil.endCalculate();
    }

    /**
     * 创建每一行布局
     * @return
     */
    private LinearLayout createRowLayout(){
        LinearLayout rowLayout=new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin=5;
        layoutParams.rightMargin=10;
        layoutParams.leftMargin=10;
        rowLayout.setLayoutParams(layoutParams);
        rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        return rowLayout;
    }

    private int level=1;

    /**
     * 创建每张图片
     * @return
     */
    private ImageView createImageView(int width, int height){
        ImageView imageView=new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                width, height);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(1,0,1,0);
        return imageView;
    }

    View.OnClickListener successListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            successDialog.setMessage("当前第"+(level++)+"关已通过");
           successDialog.show();
        }
    };
    View.OnClickListener failListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            failDialog.setMessage("您在第"+(level)+"关已失误");
            failDialog.show();
        }
    };

    private DialogInterface.OnClickListener retryListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };
    private DialogInterface.OnClickListener nextListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            initLayout();
        }
    };
    private DialogInterface.OnClickListener exitListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setResult(RESULT_OK);
            finish();
        }
    };

}
