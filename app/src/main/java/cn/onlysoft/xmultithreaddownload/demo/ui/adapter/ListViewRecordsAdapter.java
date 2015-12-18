package cn.onlysoft.xmultithreaddownload.demo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.onlysoft.xmultithreaddownload.DownloadInfo;
import cn.onlysoft.xmultithreaddownload.architecture.DownloadStatus;
import cn.onlysoft.xmultithreaddownload.demo.R;
import cn.onlysoft.xmultithreaddownload.demo.listener.OnItemClickListener;
import cn.onlysoft.xmultithreaddownload.demo.util.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aspsine on 2015/7/8.
 */
public class ListViewRecordsAdapter extends BaseAdapter {

    private List<DownloadInfo> downloadInfos;

    private OnItemClickListener mListener;

    public ListViewRecordsAdapter() {
        this.downloadInfos = new ArrayList<DownloadInfo>();
    }

    public void setData(List<DownloadInfo> appInfos) {
        this.downloadInfos.clear();
        this.downloadInfos.addAll(appInfos);
    }

    public void addOne(DownloadInfo downloadInfo){
        if(this.downloadInfos==null){
            downloadInfos=new ArrayList<>();
        }
        downloadInfos.add(downloadInfo);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return downloadInfos.size();
    }

    @Override
    public DownloadInfo getItem(int position) {
        return downloadInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_app, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DownloadInfo downloadInfo = downloadInfos.get(position);
        holder.tvName.setText(downloadInfo.getName());
        holder.tvDownloadPerSize.setText(Utils.getDownloadPerSize(downloadInfo.getFinished(), downloadInfo.getLength()));
        holder.tvStatus.setText(downloadInfo.getStatusText());
        holder.progressBar.setProgress(downloadInfo.getPercent());
        String btnText;
        switch (downloadInfo.getState()) {
            case DownloadStatus.STATUS_CONNECTING:
            case DownloadStatus.STATUS_PROGRESS:
            case DownloadStatus.STATUS_CONNECTED:
                btnText = "pause";
                break;
            case DownloadStatus.STATUS_PAUSED:
                btnText = "resume";
                break;
            case DownloadStatus.STATUS_FAILED:
                btnText = "retry";
                break;
            case DownloadStatus.STATUS_COMPLETED:
                btnText = "install";
                break;
            case DownloadStatus.STATUS_INSTALLED:
                btnText="has install";
                break;
            default:
                btnText = "start";
                break;
        }
        holder.btnDownload.setText(btnText);
        Picasso.with(parent.getContext()).load(downloadInfo.getIcon()).into(holder.ivIcon);
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClick(v, position, downloadInfo);
                }
            }
        });
        return convertView;
    }

    public final static class ViewHolder {

        @Bind(R.id.ivIcon)
        public ImageView ivIcon;

        @Bind(R.id.tvName)
        public TextView tvName;

        @Bind(R.id.btnDownload)
        public Button btnDownload;

        @Bind(R.id.tvDownloadPerSize)
        public TextView tvDownloadPerSize;

        @Bind(R.id.tvStatus)
        public TextView tvStatus;

        @Bind(R.id.progressBar)
        public ProgressBar progressBar;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
