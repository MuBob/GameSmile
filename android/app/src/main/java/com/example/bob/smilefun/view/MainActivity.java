package com.example.bob.smilefun.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.bob.smilefun.R;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ=101;
    private static final int REQUEST_GAME=102;
    private static final int REQUEST_SETTING=103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_GAME){
        }
    }

    public void clickGameInfo(View view) {
        Intent intent=new Intent(this, ReadActivity.class);
        startActivityForResult(intent, REQUEST_READ);
    }

    public void clickStartGame(View view) {
        Intent intent=new Intent(this, GameActivity.class);
        startActivityForResult(intent, REQUEST_GAME);
    }

    public void clickSetting(View view) {
        Intent intent=new Intent(this, SettingActivity.class);
        startActivityForResult(intent, REQUEST_SETTING);
    }

}
