package com.example.bob.smilefun.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.utils.UpdateManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_READ = 101;
    private static final int REQUEST_GAME = 102;
    private static final int REQUEST_SETTING = 103;
    private static final int REQUEST_INTERNET = 104;
    private static final int REQUEST_STORAGE = 105;
    private static final int REQUEST_INTERNET_UPDATE = 106;
    private static final String[] permissions_network = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE};
    private static final String[] permissions_storage=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    if(checkSelfPermission(Manifest.permission.INTERNET) != PERMISSION_GRANTED){
        requestPermissions(permissions_network, REQUEST_INTERNET);
    }else if(checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PERMISSION_GRANTED){
        requestPermissions(permissions_network, REQUEST_INTERNET);
    }else if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED){
        requestPermissions(permissions_storage, REQUEST_STORAGE);
    }
        }
        updateManager = new UpdateManager(MainActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GAME) {
        }
    }

    public void clickGameInfo(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivityForResult(intent, REQUEST_READ);
    }

    public void clickStartGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivityForResult(intent, REQUEST_GAME);
    }

    public void clickSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, REQUEST_SETTING);
    }

    public void clickUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if(checkSelfPermission(Manifest.permission.INTERNET) != PERMISSION_GRANTED){
                requestPermissions(permissions_network, REQUEST_INTERNET_UPDATE);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PERMISSION_GRANTED){
                requestPermissions(permissions_network, REQUEST_INTERNET_UPDATE);
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED){
                requestPermissions(permissions_storage, REQUEST_INTERNET_UPDATE);
            }

        }
        updateManager.checkUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults[0] != PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "应用没有获得网络访问授权，暂时无法检查更新，如需更新应用，请前往\"设置-应用程序-SmileFun\"授权该应用网络访问权限", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == REQUEST_STORAGE){
            if (grantResults[0] != PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "应用没有获得存储授权，暂时无法检查更新，如需更新应用，请前往\"设置-应用程序-SmileFun\"授权该应用写入存储权限", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == REQUEST_INTERNET_UPDATE) {
            boolean isGrant=true;
            for (int grantResult : grantResults) {
                if (grantResult != PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "应用没有获得相关授权，暂时无法检查更新，如需更新应用，请前往\"设置-应用程序-SmileFun\"授权该应用相关权限", Toast.LENGTH_LONG).show();
                    isGrant = false;
                    break;
                }
            }
            if (isGrant) {
                if (isNetworkConnected()) {
                    updateManager.checkUpdate();
                } else {
                    Toast.makeText(MainActivity.this, "网络连接未打开，暂时无法检查更新", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }
}
