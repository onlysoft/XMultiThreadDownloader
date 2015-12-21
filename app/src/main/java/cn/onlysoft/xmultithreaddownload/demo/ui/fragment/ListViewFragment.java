package cn.onlysoft.xmultithreaddownload.demo.ui.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import cn.onlysoft.xmultithreaddownload.DownloadInfo;
import cn.onlysoft.xmultithreaddownload.DownloadManager;
import cn.onlysoft.xmultithreaddownload.DownloadService;
import cn.onlysoft.xmultithreaddownload.architecture.DownloadStatus;
import cn.onlysoft.xmultithreaddownload.demo.DataSource;
import cn.onlysoft.xmultithreaddownload.demo.R;
import cn.onlysoft.xmultithreaddownload.demo.entity.AppInfo;
import cn.onlysoft.xmultithreaddownload.demo.listener.OnItemClickListener;
import cn.onlysoft.xmultithreaddownload.demo.ui.adapter.ListViewRecordsAdapter;
import cn.onlysoft.xmultithreaddownload.demo.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * update by xmren LocalBroadcastManager is replaced by Broadcast to support multi-processes model
 */
public class ListViewFragment extends Fragment implements OnItemClickListener<DownloadInfo> {


    @Bind(R.id.listView)
    ListView listView;
    @Bind(R.id.btn_add)
    Button btn_add;

    private List<AppInfo> mAppInfos;
    private ListViewRecordsAdapter mAdapter;

    private File mDownloadDir;

    private DownloadReceiver mReceiver;
    private List<DownloadInfo> downloadInfos;
    private AsyncTask<Void, Void, Void> task;

    public ListViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadDir = new File(Environment.getExternalStorageDirectory(), "Download");
        mAppInfos = DataSource.getInstance().getData();
        mAdapter = new ListViewRecordsAdapter();
        mAdapter.setOnItemClickListener(this);
        task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                downloadInfos = DownloadManager.getInstance().getDownloadInfos();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                listView.setAdapter(mAdapter);
                mAdapter.setData(downloadInfos);
            }
        };
        task.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        ButterKnife.bind(this, view);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });
        return view;
    }

    private void addTask() {
        if (mAppInfos != null && mAppInfos.size() > 0) {
            AppInfo appInfo = mAppInfos.remove(mAppInfos.size() - 1);
            DownloadInfo downloadInfo = convertToDownLoadInfo(appInfo);
            if (downloadInfos == null) {
                downloadInfos = new ArrayList<>();
            }
            downloadInfos.add(downloadInfo);
            mAdapter.addOne(downloadInfo);
            download(downloadInfos.size() - 1, downloadInfo.getUri(), downloadInfo);
        }
    }

    private DownloadInfo convertToDownLoadInfo(AppInfo appInfo) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setDir(mDownloadDir);
        downloadInfo.setName(appInfo.getName());
        downloadInfo.setUri(appInfo.getUrl());
        downloadInfo.setIcon(appInfo.getImage());
        return downloadInfo;
    }


    @Override
    public void onResume() {
        super.onResume();
        register();
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegister();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(task!=null){
            task.cancel(true);
        }
    }

    private void register() {
        mReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_DOWNLOAD_BROAD_CAST);
//        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, intentFilter);
        getActivity().registerReceiver(mReceiver, intentFilter);
    }

    private void unRegister() {
        if (mReceiver != null) {
//            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mReceiver);
            getActivity().unregisterReceiver(mReceiver);
        }
    }

    private void download(int position, String tag, DownloadInfo info) {
        DownloadService.intentDownload(getActivity(), position, tag, info);
    }

    private void pause(String tag) {
        DownloadService.intentPause(getActivity(), tag);
    }

    private void pauseAll() {
        DownloadService.intentPauseAll(getActivity());
    }

    private void install(DownloadInfo appInfo) {
        File apk = new File(mDownloadDir, appInfo.getName() + ".apk");
        if (apk.isFile() && apk.exists()) {
            String packageName = Utils.getApkFilePackage(getActivity(), apk);
            if (Utils.isAppInstalled(getActivity(), packageName)) {
                appInfo.setState(DownloadStatus.STATUS_INSTALLED);
                mAdapter.notifyDataSetChanged();
            }else{
                Utils.installApp(getActivity(), apk);
            }
        }
    }


    @Override
    public void onItemClick(View v, int position, DownloadInfo appInfo) {
        if (appInfo.getState() == DownloadStatus.STATUS_PROGRESS || appInfo.getState() == DownloadStatus.STATUS_CONNECTING || appInfo.getState() == DownloadStatus.STATUS_CONNECTED) {
            pause(appInfo.getUri());
        } else if (appInfo.getState() == DownloadStatus.STATUS_COMPLETED) {
            install(appInfo);
        } else if (appInfo.getState() == DownloadStatus.STATUS_PAUSED) {
            download(position, appInfo.getUri(), appInfo);
        }else if(appInfo.getState()==DownloadStatus.STATUS_INSTALLED){
            Toast.makeText(getActivity().getApplicationContext(),"has install",Toast.LENGTH_SHORT).show();
        }
    }

    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action == null || !action.equals(DownloadService.ACTION_DOWNLOAD_BROAD_CAST)) {
                return;
            }
            final int position = intent.getIntExtra(DownloadService.EXTRA_POSITION, -1);
            final DownloadInfo tmpInfo = (DownloadInfo) intent.getSerializableExtra(DownloadService.EXTRA_APP_INFO);
            if (tmpInfo == null || position == -1) {
                return;
            }
            final DownloadInfo appInfo = downloadInfos.get(position);
            final int status = tmpInfo.getState();
            switch (status) {
                case AppInfo.STATUS_CONNECTING:
                    appInfo.setState(DownloadStatus.STATUS_CONNECTING);
                    if (isCurrentListViewItemVisible(position)) {
                        ListViewRecordsAdapter.ViewHolder holder = getViewHolder(position);
                        holder.tvStatus.setText(tmpInfo.getStatusText());
                        holder.btnDownload.setText("pause");
                    }
                    break;

                case AppInfo.STATUS_DOWNLOADING:
                    appInfo.setState(DownloadStatus.STATUS_PROGRESS);
                    appInfo.setProgress(tmpInfo.getProgress());
                    if (isCurrentListViewItemVisible(position)) {
                        ListViewRecordsAdapter.ViewHolder holder = getViewHolder(position);
                        holder.tvDownloadPerSize.setText(Utils.getDownloadPerSize(tmpInfo.getFinished(), tmpInfo.getLength()));
                        holder.progressBar.setProgress(tmpInfo.getProgress());
                        holder.tvStatus.setText(tmpInfo.getStatusText());
                        holder.btnDownload.setText("pause");
                    }
                    break;
                case AppInfo.STATUS_COMPLETE:
                    appInfo.setState(DownloadStatus.STATUS_COMPLETED);
                    appInfo.setProgress(tmpInfo.getProgress());
                    if (isCurrentListViewItemVisible(position)) {
                        ListViewRecordsAdapter.ViewHolder holder = getViewHolder(position);
                        holder.tvStatus.setText(tmpInfo.getStatusText());
                        holder.btnDownload.setText("install");
                        holder.tvDownloadPerSize.setText(Utils.getDownloadPerSize(tmpInfo.getFinished(), tmpInfo.getLength()));
                        holder.progressBar.setProgress(tmpInfo.getProgress());
                    }
                    break;

                case AppInfo.STATUS_PAUSED:
                    appInfo.setState(DownloadStatus.STATUS_PAUSED);
                    if (isCurrentListViewItemVisible(position)) {
                        ListViewRecordsAdapter.ViewHolder holder = getViewHolder(position);
                        holder.tvStatus.setText(tmpInfo.getStatusText());
                        holder.btnDownload.setText("resume");
                    }
                    break;

                case AppInfo.STATUS_DOWNLOAD_ERROR:
                    appInfo.setState(DownloadStatus.STATUS_FAILED);
                    if (isCurrentListViewItemVisible(position)) {
                        ListViewRecordsAdapter.ViewHolder holder = getViewHolder(position);
                        holder.tvStatus.setText(tmpInfo.getStatusText());
                        holder.tvDownloadPerSize.setText("");
                        holder.btnDownload.setText("重试");
                    }
                    break;

            }
        }
    }

    private boolean isCurrentListViewItemVisible(int position) {
        int first = listView.getFirstVisiblePosition();
        int last = listView.getLastVisiblePosition();
        return first <= position && position <= last;
    }

    private ListViewRecordsAdapter.ViewHolder getViewHolder(int position) {
        int childPosition = position - listView.getFirstVisiblePosition();
        View view = listView.getChildAt(childPosition);
        return (ListViewRecordsAdapter.ViewHolder) view.getTag();
    }

}
