package cn.onlysoft.xmultithreaddownload.db;

import android.content.Context;

import cn.onlysoft.xmultithreaddownload.DownloadInfo;

import java.util.List;

/**
 * Created by aspsine on 15-4-19.
 */
public class DataBaseManager {
    private static DataBaseManager sDataBaseManager;
    private final ThreadInfoDao mThreadInfoDao;
    private final DownloadInfoDao mDownloadInfoDao;

    public static DataBaseManager getInstance(Context context) {
        if (sDataBaseManager == null) {
            sDataBaseManager = new DataBaseManager(context);
        }
        return sDataBaseManager;
    }

    private DataBaseManager(Context context) {
        mThreadInfoDao = new ThreadInfoDao(context);
        mDownloadInfoDao =new DownloadInfoDao(context);
    }

    public synchronized void insert(ThreadInfo threadInfo) {
        mThreadInfoDao.insert(threadInfo);
    }

    public synchronized void delete(String tag) {
        mThreadInfoDao.delete(tag);
    }

    public synchronized void update(String tag, int threadId, long finished) {
        mThreadInfoDao.update(tag, threadId, finished);
    }

    public List<ThreadInfo> getThreadInfos(String tag) {
        return mThreadInfoDao.getThreadInfos(tag);
    }

    public boolean exists(String tag, int threadId) {
        return mThreadInfoDao.exists(tag, threadId);
    }

    public synchronized void createOrUpdateTask(DownloadInfo taskInfo){
        List<DownloadInfo> taskInfos= mDownloadInfoDao.query(taskInfo.getUri());
        if(taskInfos!=null&&taskInfos.size()==1){
            mDownloadInfoDao.update(taskInfo);
        }else{
            mDownloadInfoDao.deleteByUrl(taskInfo);
            mDownloadInfoDao.insert(taskInfo);
        }
    }

    /**
     * get all download records
     * @return
     */
    public synchronized List<DownloadInfo> getDownloadInfo(){
        List<DownloadInfo> taskInfos=mDownloadInfoDao.query();
        return taskInfos;
    }

    /**
     * get download records by state
     * @param state
     * @return
     */
    public synchronized List<DownloadInfo> getDownloadInfoByState(int state){
        List<DownloadInfo> taskInfos=mDownloadInfoDao.query(state);
        return taskInfos;
    }
    public synchronized void updateTaskFinishedSize(String url, long finished){
        mDownloadInfoDao.update(url, finished);
    }

    public synchronized void updateTaskState(String url,int state){
        mDownloadInfoDao.update(url,state);
    }

    public synchronized void updateTask(DownloadInfo taskInfo){
        mDownloadInfoDao.update(taskInfo);
    }

    public List<DownloadInfo> getFinishedDownloadInfo() {
        List<DownloadInfo> taskInfos=mDownloadInfoDao.queryFinished();
        return taskInfos;
    }

    public List<DownloadInfo> getUnFinishedDownloadInfo() {
        List<DownloadInfo> taskInfos=mDownloadInfoDao.queryUnFinished();
        return taskInfos;
    }
}
