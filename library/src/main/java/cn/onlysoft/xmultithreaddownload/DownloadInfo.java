package cn.onlysoft.xmultithreaddownload;

import cn.onlysoft.xmultithreaddownload.architecture.DownloadStatus;

import java.io.File;
import java.io.Serializable;

/**
 * Created by aspsine on 15-4-19.
 *
 * update by xmren add download state field
 */
public class DownloadInfo implements Serializable{
    private String name;
    private String uri;
    private File dir;
    private int progress;
    private long length;
    private long finished;
    private int percent;
    private boolean acceptRanges;

    private int state;
    private String icon;

    public DownloadInfo() {
    }

    public DownloadInfo(String name, String uri, File dir) {
        this.name = name;
        this.uri = uri;
        this.dir = dir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public boolean isAcceptRanges() {
        return acceptRanges;
    }

    public void setAcceptRanges(boolean acceptRanges) {
        this.acceptRanges = acceptRanges;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStatusText() {
        switch (state) {
            case DownloadStatus.STATUS_CONNECTING:
                return "Connecting";
            case DownloadStatus.STATUS_PROGRESS:
                return "Downloading";
            case DownloadStatus.STATUS_PAUSED:
                return "Pause";
            case DownloadStatus.STATUS_FAILED:
                return "Download Error";
            case DownloadStatus.STATUS_COMPLETED:
                return "install";
            case DownloadStatus.STATUS_CONNECTED:
                return "connected";
            default:
                return "Not Download";
        }
    }
}
