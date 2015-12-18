package cn.onlysoft.xmultithreaddownload;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.onlysoft.xmultithreaddownload.R;
import cn.onlysoft.xmultithreaddownload.architecture.DownloadStatus;
import cn.onlysoft.xmultithreaddownload.util.L;

/**
 * Created by aspsine on 15/7/28.
 */
public class DownloadService extends Service {

    private static final String TAG = DownloadService.class.getSimpleName();

    public static final String ACTION_DOWNLOAD_BROAD_CAST = "com.aspsine.multithreaddownload.demo:action_download_broad_cast";

    public static final String ACTION_DOWNLOAD = "com.aspsine.multithreaddownload.demo:action_download";

    public static final String ACTION_PAUSE = "com.aspsine.multithreaddownload.demo:action_pause";

    public static final String ACTION_CANCEL = "com.aspsine.multithreaddownload.demo:action_cancel";

    public static final String ACTION_PAUSE_ALL = "com.aspsine.multithreaddownload.demo:action_pause_all";

    public static final String ACTION_CANCEL_ALL = "com.aspsine.multithreaddownload.demo:action_cancel_all";

    public static final String EXTRA_POSITION = "extra_position";

    public static final String EXTRA_TAG = "extra_tag";

    public static final String EXTRA_APP_INFO = "extra_app_info";

    /**
     * Dir: /Download
     */
    private File mDownloadDir;

    private DownloadManager mDownloadManager;

    private NotificationManager mNotificationManager;

    public static void intentDownload(Context context, int position, String tag, DownloadInfo info) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_POSITION, position);
        intent.putExtra(EXTRA_TAG, tag);
        intent.putExtra(EXTRA_APP_INFO, info);
        context.startService(intent);
    }

    public static void intentPause(Context context, String tag) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE);
        intent.putExtra(EXTRA_TAG, tag);
        context.startService(intent);
    }

    public static void intentPauseAll(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.setAction(ACTION_PAUSE_ALL);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        if (intent != null) {
            String action = intent.getAction();
            int position = intent.getIntExtra(EXTRA_POSITION, 0);
            DownloadInfo appInfo = (DownloadInfo) intent.getSerializableExtra(EXTRA_APP_INFO);
            String tag = intent.getStringExtra(EXTRA_TAG);
            switch (action) {
                case ACTION_DOWNLOAD:
                    download(position, appInfo, tag);
                    break;
                case ACTION_PAUSE:
                    pause(tag);
                    break;
                case ACTION_CANCEL:
                    cancel(tag);
                    break;
                case ACTION_PAUSE_ALL:
                    pauseAll();
                    break;
                case ACTION_CANCEL_ALL:
                    cancelAll();
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void download(final int position, final DownloadInfo appInfo, String tag) {
        final DownloadRequest request = new DownloadRequest.Builder()
                .setTitle(appInfo.getName() + ".apk")
                .setUri(appInfo.getUri())
                .setFolder(mDownloadDir)
                .setIconUrl(appInfo.getIcon())
                .build();
        mDownloadManager.download(request, tag, new DownloadCallBack(position, appInfo, mNotificationManager, getApplicationContext()));
    }

    private void pause(String tag) {
        mDownloadManager.pause(tag);
    }

    private void cancel(String tag) {
        mDownloadManager.cancel(tag);
    }

    private void pauseAll() {
        mDownloadManager.pauseAll();
    }

    private void cancelAll() {
        mDownloadManager.cancelAll();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDownloadManager = DownloadManager.getInstance();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        mDownloadDir = new File(Environment.getExternalStorageDirectory(), "Download");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDownloadManager.pauseAll();
    }

    public static class DownloadCallBack implements CallBack {

        private int mPosition;

        private DownloadInfo mAppInfo;

//        private LocalBroadcastManager mLocalBroadcastManager;

        private NotificationCompat.Builder mBuilder;

        private NotificationManager mNotificationManager;

        private long mLastTime;

        private WeakReference<Context> contextWeakReference;

        public DownloadCallBack(int position, DownloadInfo appInfo, NotificationManager notificationManager, Context context) {
            mPosition = position;
            mAppInfo = appInfo;
            mNotificationManager = notificationManager;
            contextWeakReference=new WeakReference<Context>(context);
//            mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
            mBuilder = new NotificationCompat.Builder(context);
        }

        @Override
        public void onStarted() {
            L.i(TAG, "onStart()");
            mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(mAppInfo.getName())
                    .setContentText("Init Download")
                    .setProgress(100, 0, true)
                    .setTicker("Start download " + mAppInfo.getName());
            updateNotification();
        }

        @Override
        public void onConnecting() {
            L.i(TAG, "onConnecting()");
            mBuilder.setContentText("Connecting")
                    .setProgress(100, 0, true);
            updateNotification();

            mAppInfo.setState(DownloadStatus.STATUS_CONNECTING);
            sendBroadCast(mAppInfo);
        }

        @Override
        public void onConnected(long total, boolean isRangeSupport) {
            L.i(TAG, "onConnected()");
            mBuilder.setContentText("Connected")
                    .setProgress(100, 0, true);
            updateNotification();
        }

        @Override
        public void onProgress(long finished, long total, int progress) {

            if (mLastTime == 0) {
                mLastTime = System.currentTimeMillis();
            }

            mAppInfo.setState(DownloadStatus.STATUS_PROGRESS);
            mAppInfo.setProgress(progress);
            mAppInfo.setLength(total);
            mAppInfo.setFinished(finished);

            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastTime > 500) {
                L.i(TAG, "onProgress()");
                mBuilder.setContentText("Downloading");
                mBuilder.setProgress(100, progress, false);
                updateNotification();

                sendBroadCast(mAppInfo);

                mLastTime = currentTime;
            }
        }

        @Override
        public void onCompleted() {
            L.i(TAG, "onCompleted()");
            mBuilder.setContentText("Download Complete");
            mBuilder.setProgress(0, 0, false);
            mBuilder.setTicker(mAppInfo.getName() + " download Complete");
            updateNotification();

            mAppInfo.setState(DownloadStatus.STATUS_COMPLETED);
            mAppInfo.setProgress(100);
            sendBroadCast(mAppInfo);
        }

        @Override
        public void onDownloadPaused() {
            L.i(TAG, "onDownloadPaused()");
            mBuilder.setContentText("Download Paused");
            mBuilder.setTicker(mAppInfo.getName() + " download Paused");
            updateNotification();

            mAppInfo.setState(DownloadStatus.STATUS_PAUSED);
            sendBroadCast(mAppInfo);
        }

        @Override
        public void onDownloadCanceled() {
            L.i(TAG, "onDownloadCanceled()");
            mBuilder.setContentText("Download Canceled");
            mBuilder.setTicker(mAppInfo.getName() + " download Canceled");
            updateNotification();
            mNotificationManager.cancel(mPosition);
        }

        @Override
        public void onFailed(DownloadException e) {
            L.i(TAG, "onFailed()");
            e.printStackTrace();
            mBuilder.setContentText("Download Failed");
            mBuilder.setTicker(mAppInfo.getName() + " download failed");
            mBuilder.setProgress(100, mAppInfo.getProgress(), false);
            updateNotification();

            mAppInfo.setState(DownloadStatus.STATUS_FAILED);
            sendBroadCast(mAppInfo);
        }

        private void updateNotification() {
            mNotificationManager.notify(mPosition + 1000, mBuilder.build());
        }

        private void sendBroadCast(DownloadInfo appInfo) {
            Intent intent = new Intent();
            intent.setAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
            intent.putExtra(EXTRA_POSITION, mPosition);
            intent.putExtra(EXTRA_APP_INFO, appInfo);
//            mLocalBroadcastManager.sendBroadcast(intent);
            Context context = contextWeakReference.get();
            if(context!=null){
                context.sendBroadcast(intent);
            }
        }
    }


}
