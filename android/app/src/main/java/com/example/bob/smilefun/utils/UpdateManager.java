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
    private static final int CHECKED_VERSION= 3;
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
                    installApk(mHashMap.get("name"));
                    break;
                case CHECKED_VERSION:
                    if(onUpdateListener!=null){
                        onUpdateListener.onChecked(msg.arg1);
                    }
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
    public void checkUpdate(OnUpdateListener listener) {
        this.onUpdateListener=listener;
        new checkUpdateThread().start();
    }

    private OnUpdateListener onUpdateListener;
    public interface OnUpdateListener{
        void onChecked(int version);
    }
    private static final String homePath="https://mubob.github.io/GameSmile/";
    private static final String versionPath = "web/version.xml";

    private static final String TAG = "UpdateManagerTAG";

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    private int isUpdate() {
        InputStream inStream = null;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(homePath+versionPath);
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
                    return serviceCode;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "UpdateManager.isUpdate:Exception e=" + e.getMessage());
                }
            }
        }
        return 0;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public int getVersionCode(Context context) {
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
    public void showNoticeDialog() {
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle(R.string.update_check);
        builder.setMessage(R.string.update_check_detail);
        builder.setPositiveButton(R.string.update_immediately, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // 显示下载对话框
//                showDownloadDialog();
                downloadApk();
            }
        });
        builder.setNegativeButton(R.string.update_later, new OnClickListener() {
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
        String urlPath = mHashMap.get("url");
        if(!urlPath.startsWith(homePath)){
            urlPath=homePath+urlPath;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlPath));
        File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mHashMap.get("name"));
        request.setDestinationUri(Uri.fromFile(file));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setTitle(mHashMap.get("name"));
        request.setDescription(mHashMap.get("name")+mContext.getString(R.string.update_downloading));
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
            int versionCode = isUpdate();
            Message message = mHandler.obtainMessage();
            message.what=CHECKED_VERSION;
            message.arg1=versionCode;
            mHandler.sendMessage(message);
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
                                   installApk(fileName);
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
                        Toast.makeText(mContext, R.string.download_cancel, Toast.LENGTH_SHORT).show();
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
                    String urlPath = mHashMap.get("url");
                    if(!urlPath.startsWith(homePath)){
                        urlPath=homePath+urlPath;
                    }
                    URL url = new URL(urlPath);
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
    private void installApk(String fileName) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", new File(fileName));
        }else{
            uri=Uri.fromFile(new File(fileName));
        }
        if (uri != null) {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(uri, "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // 给目标应用一个临时授权
            mContext.startActivity(install);
        }
    }
}
