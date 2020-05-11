package com.example.bob.smilefun.utils;

import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

public class ImageViewShowAnimation {
    private ImageView imageView;
    private Animation animHide, animShow;
    private static Animation.AnimationListener hideListener, showListener;
    @DrawableRes
    private  int resId;
    private View.OnClickListener onClickListener;

    public ImageViewShowAnimation(ImageView view) {
        this.imageView=view;

        animHide = new ScaleAnimation(1.0f, 0.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animHide.setDuration(500);
        animShow = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animShow.setDuration(500);

        hideListener=new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                imageView.setOnClickListener(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.startAnimation(animShow);
                imageView.setImageResource(resId);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        showListener=new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(onClickListener!=null){
                    imageView.setOnClickListener(onClickListener);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
        animHide.setAnimationListener(hideListener);
        animShow.setAnimationListener(showListener);
    }

    public void execute(@DrawableRes int resId, View.OnClickListener onClickListener){
        this.resId = resId;
        this.onClickListener = onClickListener;
        imageView.startAnimation(animHide);
    }


}
