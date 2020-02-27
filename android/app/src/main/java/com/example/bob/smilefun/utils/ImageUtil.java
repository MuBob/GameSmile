package com.example.bob.smilefun.utils;

import com.example.bob.smilefun.R;

import java.util.Random;

public class ImageUtil {

    private static int smileRes[] ={
            R.mipmap.smile_1,R.mipmap.smile_2,R.mipmap.smile_3,R.mipmap.smile_4,R.mipmap.smile_5,R.mipmap.smile_6,R.mipmap.smile_7,R.mipmap.smile_8,R.mipmap.smile_9,R.mipmap.smile_10,
            R.mipmap.smile_11,R.mipmap.smile_12,R.mipmap.smile_13,R.mipmap.smile_14,R.mipmap.smile_15,R.mipmap.smile_16,R.mipmap.smile_17,R.mipmap.smile_18,R.mipmap.smile_19,R.mipmap.smile_20,
            R.mipmap.smile_21,R.mipmap.smile_22,R.mipmap.smile_23,R.mipmap.smile_24,R.mipmap.smile_25,R.mipmap.smile_26,R.mipmap.smile_27,R.mipmap.smile_28,R.mipmap.smile_29,R.mipmap.smile_30,
            R.mipmap.smile_31,R.mipmap.smile_32,R.mipmap.smile_33,R.mipmap.smile_34,R.mipmap.smile_35,R.mipmap.smile_36,R.mipmap.smile_37,R.mipmap.smile_38,R.mipmap.smile_39,R.mipmap.smile_40,
            R.mipmap.smile_41,R.mipmap.smile_42,R.mipmap.smile_43,R.mipmap.smile_44,R.mipmap.smile_45,R.mipmap.smile_46,R.mipmap.smile_47,R.mipmap.smile_48,R.mipmap.smile_49,R.mipmap.smile_50,
            R.mipmap.smile_51,R.mipmap.smile_52,R.mipmap.smile_53,R.mipmap.smile_54,R.mipmap.smile_55,R.mipmap.smile_56,R.mipmap.smile_57,R.mipmap.smile_58,R.mipmap.smile_59,R.mipmap.smile_60,
            R.mipmap.smile_61,R.mipmap.smile_62,R.mipmap.smile_63,R.mipmap.smile_64,R.mipmap.smile_65,R.mipmap.smile_66,R.mipmap.smile_67,R.mipmap.smile_68,R.mipmap.smile_69,R.mipmap.smile_70,
            R.mipmap.smile_71,R.mipmap.smile_72,R.mipmap.smile_73,R.mipmap.smile_74,R.mipmap.smile_75,R.mipmap.smile_76,R.mipmap.smile_77,R.mipmap.smile_78,R.mipmap.smile_79,R.mipmap.smile_80,
            R.mipmap.smile_81,R.mipmap.smile_82,R.mipmap.smile_83,R.mipmap.smile_84,R.mipmap.smile_85,R.mipmap.smile_86,R.mipmap.smile_87,R.mipmap.smile_88,R.mipmap.smile_89,R.mipmap.smile_90,
            R.mipmap.smile_91,R.mipmap.smile_92,R.mipmap.smile_93,R.mipmap.smile_94,R.mipmap.smile_95,R.mipmap.smile_96,R.mipmap.smile_97,R.mipmap.smile_98,R.mipmap.smile_99,R.mipmap.smile_100,
    };

    private static int angryRes[]={
            R.mipmap.angle_1,R.mipmap.angle_2,R.mipmap.angle_3,R.mipmap.angle_4,R.mipmap.angle_5,            R.mipmap.angle_6,R.mipmap.angle_7,R.mipmap.angle_8,R.mipmap.angle_9,R.mipmap.angle_10,
            R.mipmap.angle_11,R.mipmap.angle_12,R.mipmap.angle_13,R.mipmap.angle_14,R.mipmap.angle_15,R.mipmap.angle_16,R.mipmap.angle_17,R.mipmap.angle_18,R.mipmap.angle_19,R.mipmap.angle_20,
            R.mipmap.angle_21,R.mipmap.angle_22,R.mipmap.angle_23,R.mipmap.angle_24,R.mipmap.angle_25,R.mipmap.angle_26,R.mipmap.angle_27,R.mipmap.angle_28,R.mipmap.angle_29,R.mipmap.angle_30,
            R.mipmap.angle_31,R.mipmap.angle_32,R.mipmap.angle_33,R.mipmap.angle_34,R.mipmap.angle_35,R.mipmap.angle_36,R.mipmap.angle_37,R.mipmap.angle_38,R.mipmap.angle_39,R.mipmap.angle_40,
            R.mipmap.angle_41,R.mipmap.angle_42,R.mipmap.angle_43,R.mipmap.angle_44,R.mipmap.angle_45,R.mipmap.angle_46,R.mipmap.angle_47,R.mipmap.angle_48,R.mipmap.angle_49,R.mipmap.angle_50,
    };

    private static final String TAG = "ImageUtilTAG";
    private int count;
    private int bingoIndex;
    private boolean bingo;
    private int resId;
    public void beginCalculate(int count){
        this.count=count;
        bingoIndex=random(count);
    }

    public void endCalculate(){

    }

    public void calculate(){
        if(--count==bingoIndex){
            bingo=true;
            resId= smileRes[random(100)];
        }else {
           resId= angryRes[random(50)];
           bingo=false;
        }
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

}
