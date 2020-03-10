package com.example.bob.smilefun.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.bob.smilefun.R;
import com.example.bob.smilefun.utils.UpdateManager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements UpdateManager.OnUpdateListener{

    private static final int REQUEST_READ = 101;
    private static final int REQUEST_GAME = 102;
    private static final int REQUEST_HISTORY = 103;
    private static final int REQUEST_SETTING = 104;
    private static final int REQUEST_PERMISSION_FIRST= 110;
    private static final int REQUEST_INTERNET_UPDATE = 111;
    private static final int REQUEST_API28_INSTALL = 112;
    private static final String[] permissions_update = new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private UpdateManager updateManager;
    private static final String TAG = "MainActivityTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                requestPermissions(permissions_update, REQUEST_PERMISSION_FIRST);
            }
        }
        updateManager = new UpdateManager(MainActivity.this);
    }

    public void clickGameInfo(View view) {
        Intent intent = new Intent(this, ReadActivity.class);
        startActivityForResult(intent, REQUEST_READ);
    }

    public void clickStartGame(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivityForResult(intent, REQUEST_GAME);
    }

    public void clickHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivityForResult(intent, REQUEST_HISTORY);
    }

    public void clickSetting(View view) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, REQUEST_SETTING);
    }

    public void clickUpdate(View view) {
        findViewById(R.id.btn_update).setEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean isInstallPermission = getPackageManager().canRequestPackageInstalls();
            Log.i(TAG, "MainActivity.clickUpdate: install permission?="+isInstallPermission);
            if (!isInstallPermission) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.request_permission_install)
                        .setMessage(R.string.msg_permission_install)
                        .setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    Uri packageURI = Uri.parse("package:" + getPackageName());
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
                                    startActivityForResult(intent, REQUEST_API28_INSTALL);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
            }else{
                checkPermissions();
            }
        } else {
            checkPermissions();
        }

    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PERMISSION_GRANTED
                    ||checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                requestPermissions(permissions_update, REQUEST_INTERNET_UPDATE);
            }else{
                checkNetworkToUpdate();
            }
        } else {
            checkNetworkToUpdate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_FIRST) {
            boolean isGranted=true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    isGranted=false;
                    break;
                }
            }
            if(!isGranted){
                Toast.makeText(MainActivity.this, R.string.permission_fail_first, Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_INTERNET_UPDATE) {
            boolean isGrant = true;
            for (int grantResult : grantResults) {
                if (grantResult != PERMISSION_GRANTED) {
                     isGrant = false;
                    break;
                }
            }
            if (isGrant) {
                checkNetworkToUpdate();
            }else{
                Toast.makeText(MainActivity.this, R.string.permission_fail_first, Toast.LENGTH_LONG).show();
                findViewById(R.id.btn_update).setEnabled(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "MainActivity.onActivityResult: request="+requestCode);
        if (requestCode == REQUEST_GAME) {
        } else if (requestCode == REQUEST_API28_INSTALL) {
            checkPermissions();
            Log.i(TAG, "MainActivity.onActivityResult: result="+resultCode);
        }
    }

    private void checkNetworkToUpdate() {
        if (isNetworkConnected()) {
            updateManager.checkUpdate(this);
        } else {
            Toast.makeText(MainActivity.this, R.string.check_update_fail_no_network, Toast.LENGTH_SHORT).show();
            findViewById(R.id.btn_update).setEnabled(false);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }


    @Override
    public void onChecked(int version) {
        int versionCode = updateManager.getVersionCode(this);
        Log.i(TAG, "MainActivity.onChecked: cur version="+versionCode+", service version="+version);
        if(version>versionCode){
            updateManager.showNoticeDialog();
        }else{
            Toast.makeText(this, R.string.update_check_last, Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.btn_update).setEnabled(false);
    }

}
