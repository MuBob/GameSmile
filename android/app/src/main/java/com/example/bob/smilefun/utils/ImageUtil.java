package com.example.bob.smilefun.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.bob.smilefun.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageUtil {

    private List<Integer> smileRes, angryRes;
    private int smileCount, angryCount;

    private static final String TAG = "ImageUtilTAG";
    private int count;
    private int bingoIndex;
    private boolean bingo;
    private int resId;
    private List<Integer> saveIds;
    private Context context;

    public ImageUtil(Context context) {
        this.saveIds=new ArrayList<>();
        this.context=context;
        smileRes=new ArrayList<>();
        angryRes=new ArrayList<>();
        changeSmiles(null);
        changeAngrys(null);
    }

    public void changeSmiles(String containName){
        if(containName==null||containName.equals("")){
            containName="smile_";
        }
        smileRes.clear();
        smileRes.addAll(getImageResourceId(containName));
        smileCount=smileRes.size();
        Log.i(TAG, "ImageUtil.changeSmiles: name="+containName+", size="+smileCount);
    }

    public void changeAngrys(String containName){
        if(containName==null||containName.equals("")){
            containName="angry_";
        }
        angryRes.clear();
        angryRes.addAll(getImageResourceId(containName));
        angryCount=angryRes.size();
        Log.i(TAG, "ImageUtil.changeAngrys: name="+containName+", size="+angryCount);
    }

    public void beginCalculate(int count){
        this.count=count;
        bingoIndex=random(count);
    }

    public void endCalculate(){
        saveIds.clear();
    }

    public void calculate(){
        if(--count==bingoIndex){
            bingo=true;
            resId= smileRes.get(random(smileCount));
        }else {
            do {
                resId= angryRes.get(random(angryCount));
            }while (saveIds.contains(resId));
            bingo=false;
        }
        saveIds.add(resId);
    }

    public int getResId(){
        return resId;
    }

    public boolean isBingo() {
        return bingo;
    }

    private int random(int range){
        Random random=new Random();
        return random.nextInt(range);
    }


    public List<Integer> getImageResourceId(String imgName) {
        List<Integer> imgList = new ArrayList<>();
        Resources resources = context.getResources();
        Field[] fields = R.mipmap.class.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            String name = fields[i].getName();
            if (name.contains(imgName)) {
                int resId = resources.getIdentifier(name, "mipmap", context.getPackageName());
//                Drawable drawable = resources.getDrawable(resId);
                imgList.add(resId);
            }
        }
        return imgList;
    }

}
