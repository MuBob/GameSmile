package com.example.bob.smilefun.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.bob.smilefun.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UpdateManager {
    private static final int DOWNLOAD = 1;
    private static final int DOWNLOAD_FINISH = 2;
    private static final int CHECK_OLD = 3;
    private static final int CHECK_NEW = 4;
    HashMap<String, String> mHashMap;
    private String mSavePath;
    private int progress;
    private boolean cancelUpdate = false;

    private Context mContext;
    private ProgressBar mProgress;
    private Dialog mDownloadDialog;

    private DownloadManager downloadManager;
    private long downloadId;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:
                    // 设置进度条位置
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOAD_FINISH:
                    // 安装文件
                    installApk();
                    break;
                case CHECK_NEW:
                    Toast.makeText(mContext, "当前软件为最新，没有更新", Toast.LENGTH_LONG).show();
                    break;
                case CHECK_OLD:
                    showNoticeDialog();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    /**
     * 检测软件更新
     */
    public void checkUpdate() {
        new checkUpdateThread().start();
    }

    private static final String path = "https://mubob.github.io/GameSmile/web/version.xml";

    private static final String TAG = "UpdateManagerTAG";

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    private boolean isUpdate() {
        int versionCode = getVersionCode(mContext);
        InputStream inStream = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(path);
            conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            Log.i(TAG, "UpdateManager.isUpdate: code=" + responseCode);
            if (responseCode == 200) {
                inStream = conn.getInputStream();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG, "UpdateManager.isUpdate:MalformedURLException e=" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "UpdateManager.isUpdate:IOException e=" + e.getMessage());
        }
        if (inStream != null) {
            XmlUtil service = new XmlUtil();
            try {
                mHashMap = service.parseXml(inStream);
                Log.i(TAG, "UpdateManager.isUpdate: hash=" + mHashMap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "UpdateManager.isUpdate:Exception e=" + e.getMessage());
            } finally {
                try {
                    inStream.close();
                    conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "UpdateManager.isUpdate:IOException e=" + e.getMessage());
                }
            }
            if (null != mHashMap) {
                try {
                    int serviceCode = Integer.valueOf(mHashMap.get("version"));
                    Log.i(TAG, "UpdateManager.isUpdate: serviceVersion=" + serviceCode + ", cur version=" + versionCode);
                    // 版本判断
                    if (serviceCode > versionCode) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "UpdateManager.isUpdate:Exception e=" + e.getMessage());
                }
            }
        }
        return false;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo("com.example.bob.smilefun", 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("SmileFun版本检查");
        builder.setMessage("有新版本需要联网更新");
        builder.setPositiveButton("立即更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
//                showDownloadDialog();
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("SmileFun更新中");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.update_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setNegativeButton("取消更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancelUpdate = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
//        new downloadApkThread().start();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mHashMap.get("url")));
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mHashMap.get("name"));
        request.setDestinationUri(Uri.fromFile(file));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(mHashMap.get("name"));
        request.setDescription("SmileFun正在下载中......");
        request.setAllowedOverRoaming(false);
        request.setVisibleInDownloadsUi(true);
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);
        Log.i(TAG, "managerDownloadApkThread.run: downloadId=" + downloadId);
        IntentFilter filter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//            filter.addAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
//            filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        mContext.registerReceiver(downLoadCompleteReceiver,filter);
    }

    private class checkUpdateThread extends Thread {
        @Override
        public void run() {
            if (isUpdate()) {
                mHandler.sendEmptyMessage(CHECK_OLD);
            } else {
                mHandler.sendEmptyMessage(CHECK_NEW);
            }
        }
    }

    private DownLoadCompleteReceiver downLoadCompleteReceiver = new DownLoadCompleteReceiver();

    public class DownLoadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "DownLoadCompleteReceiver.onReceive: action=" + intent.getAction());
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Log.i(TAG, "DownLoadCompleteReceiver.onReceive: id=" + id);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(id);
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                if (dm != null) {
                    Cursor c = dm.query(query);
                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: c size="+c.getCount());
                    if (c != null && c.moveToFirst()) {
                        try {
                            Log.i(TAG, "DownLoadCompleteReceiver.onReceive: start cursor first ");
                            int status = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
                            Log.i(TAG, "DownLoadCompleteReceiver.onReceive: statues=" + status);
                            int fileUriIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                            String fileUri = c.getString(fileUriIdx);
                            Log.i(TAG, "DownLoadCompleteReceiver.onReceive: fileUri=" + fileUri);
                            String fileName = null;
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                if (fileUri != null) {
                                    fileName = Uri.parse(fileUri).getPath();
                                }
                            } else {
                                int fileNameIdx = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
                                fileName = c.getString(fileNameIdx);
                            }
                            Log.i(TAG, "DownLoadCompleteReceiver.onReceive: filename=" + fileName);

                            switch (status) {
                                case DownloadManager.STATUS_PAUSED:
                                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 下载暂停");
                                    break;
                                case DownloadManager.STATUS_PENDING:
                                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 下载延迟");
                                    break;
                                case DownloadManager.STATUS_RUNNING:
                                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 正在下载");
                                    break;
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 下载完成");
                                    Uri uri = null;
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                        uri = FileProvider.getUriForFile(mContext, "com.example.bob.smilefun.fileprovider", new File(fileName));
                                    }else{
                                        uri=Uri.fromFile(new File(fileName));
                                    }
                                    if (uri != null) {
                                        Intent install = new Intent(Intent.ACTION_VIEW);
                                        install.setDataAndType(uri, "application/vnd.android.package-archive");
                                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(install);
                                    }
                                    mContext.unregisterReceiver(this);
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 下载失败");
                                    mContext.unregisterReceiver(this);
                                    break;
                                default:
                                    Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 下载出现未知错误");
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "DownLoadCompleteReceiver.onReceive: Exception e=" + e.getMessage());
                            return;
                        } finally {
                            c.close();
                        }
                    }else {
                        Log.i(TAG, "DownLoadCompleteReceiver.onReceive: 下载取消");
                        Toast.makeText(mContext, "下载已取消", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download";
                    URL url = new URL(mHashMap.get("url"));
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    File apkFile = new File(mSavePath, mHashMap.get("name"));
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
            mDownloadDialog.dismiss();
        }
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, mHashMap.get("name"));
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }


}
