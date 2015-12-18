package cn.onlysoft.xmultithreaddownload.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import cn.onlysoft.xmultithreaddownload.DownloadInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmren on 12/16/2015.
 */
public class DownloadInfoDao extends AbstractDao<DownloadInfo> {
    /**
     * debug tag.
     */
    private static final String TAG = "DownloadInfoDao";
    /**
     * table name : download
     */
    private static final String TABLE_NAME = "downloadInfo";

    private static final String FIELD_ID = "_id";
    private static final String FIELD_URL = "url";
    private static final String FIELD_DOWNLOAD_STATE = "downloadState";
    private static final String FIELD_FILEPATH = "filepath";
    private static final String FIELD_DOWNLOAD_PRECENT = "percent";
    private static final String FIELD_FILENAME = "filename";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_THUMBNAIL = "thumbnail";
    private static final String FIELD_FINISHED_SIZE = "finishedSize";
    private static final String FIELD_TOTAL_SIZE = "totalSize";

    public DownloadInfoDao(Context context) {
        super(context);
    }

    public static void createTable(SQLiteDatabase db) {
        Log.i(TAG, "create download table.");
        StringBuffer buffer = new StringBuffer("create table ");
        buffer.append(TABLE_NAME);
        buffer.append("(");
        buffer.append(FIELD_ID);
        buffer.append(" integer primary key autoincrement, ");
        buffer.append(FIELD_URL);
        buffer.append(" text unique, ");
        buffer.append(FIELD_DOWNLOAD_STATE);
        buffer.append(" integer,");
        buffer.append(FIELD_FILEPATH);
        buffer.append(" text, ");
        buffer.append(FIELD_DOWNLOAD_PRECENT);
        buffer.append(" integer,");
        buffer.append(FIELD_FILENAME);
        buffer.append(" text, ");
        buffer.append(FIELD_TITLE);
        buffer.append(" text, ");
        buffer.append(FIELD_THUMBNAIL);
        buffer.append(" text, ");
        buffer.append(FIELD_FINISHED_SIZE);
        buffer.append(" integer, ");
        buffer.append(FIELD_TOTAL_SIZE);
        buffer.append(" integer)");

        String sql = buffer.toString();
        Log.i(TAG, sql);
        db.execSQL(sql);
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL("drop table if exists " + TABLE_NAME);
    }

    void insert(DownloadInfo taskInfo) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, getContentValues(taskInfo));
    }

    public List<DownloadInfo> query(String uri) {
        List<DownloadInfo> taskInfos=new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        + " where "+FIELD_URL+" = ? ",
                new String[]{uri});
        while(cursor.moveToNext()){
            DownloadInfo downloadInfo=new DownloadInfo(); //just calculate count
            taskInfos.add(downloadInfo);
        }
        return taskInfos;
    }

    void update(DownloadInfo taskInfo) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TABLE_NAME, getContentValues(taskInfo), FIELD_URL + "=?", new String[]{
                taskInfo.getUri()
        });
    }

    public void update(String url, long finished) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuffer buffer = new StringBuffer("update ");
        buffer.append(TABLE_NAME);
        buffer.append(" set "+FIELD_FINISHED_SIZE);
        buffer.append(" = ? ");
        buffer.append(" where "+FIELD_URL);
        buffer.append(" = ? ");
        String sql = buffer.toString();
        Log.i(TAG, "update size sql printf"+sql);
        db.execSQL(sql,
                new Object[]{finished, url});
    }

    public void update(String url, int state) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuffer buffer = new StringBuffer("update ");
        buffer.append(TABLE_NAME);
        buffer.append(" set "+FIELD_DOWNLOAD_STATE);
        buffer.append(" = ? ");
        buffer.append(" where " + FIELD_URL);
        buffer.append(" = ? ");
        String sql = buffer.toString();
        Log.i(TAG, "update state sql printf" + sql);
        db.execSQL(sql,
                new Object[]{state, url});
    }

    void deleteByUrl(DownloadInfo downloadTask) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, FIELD_URL + " = ? ", new String[]{
                downloadTask.getUri()
        });
    }

    private ContentValues getContentValues(DownloadInfo downloadInfo) {
        ContentValues values = new ContentValues();
        values.put(FIELD_URL, downloadInfo.getUri());
        values.put(FIELD_DOWNLOAD_STATE, downloadInfo.getState());
        values.put(FIELD_FILEPATH, downloadInfo.getDir().getAbsolutePath());
        values.put(FIELD_FILENAME, downloadInfo.getName());
        values.put(FIELD_THUMBNAIL, downloadInfo.getIcon());
        values.put(FIELD_FINISHED_SIZE, downloadInfo.getFinished());
        values.put(FIELD_TOTAL_SIZE, downloadInfo.getLength());
        values.put(FIELD_DOWNLOAD_PRECENT, downloadInfo.getPercent());
        return values;
    }


    public List<DownloadInfo> query() {
        List<DownloadInfo> downloadInfos=new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        +" ",
                null);
        while(cursor.moveToNext()){
            DownloadInfo downloadInfo = getDownloadInfo(cursor);
            downloadInfos.add(downloadInfo);
        }
        cursor.close();
        return downloadInfos;
    }

    public List<DownloadInfo> query(int state) {
        List<DownloadInfo> downloadInfos=new ArrayList<>();
        List<DownloadInfo> taskInfos=new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "
                        + TABLE_NAME
                        +" where "+FIELD_DOWNLOAD_STATE+" = ? ",
                new String[]{state+""});
        while(cursor.moveToNext()){
            DownloadInfo downloadInfo=getDownloadInfo(cursor);
            taskInfos.add(downloadInfo);
        }
        cursor.close();
        return downloadInfos;
    }

    @NonNull
    private DownloadInfo getDownloadInfo(Cursor cursor) {
        DownloadInfo downloadInfo=new DownloadInfo(); //just calculate count
        downloadInfo.setName(cursor.getString(cursor.getColumnIndex(FIELD_FILENAME)));
        downloadInfo.setIcon(cursor.getString(cursor.getColumnIndex(FIELD_THUMBNAIL)));
        downloadInfo.setState(cursor.getInt(cursor.getColumnIndex(FIELD_DOWNLOAD_STATE)));
        downloadInfo.setFinished(cursor.getLong(cursor.getColumnIndex(FIELD_FINISHED_SIZE)));
        downloadInfo.setDir(new File(cursor.getString(cursor.getColumnIndex(FIELD_FILEPATH))));
        downloadInfo.setLength(cursor.getLong(cursor.getColumnIndex(FIELD_TOTAL_SIZE)));
        downloadInfo.setUri(cursor.getString(cursor.getColumnIndex(FIELD_URL)));
        downloadInfo.setPercent(cursor.getInt(cursor.getColumnIndex(FIELD_DOWNLOAD_PRECENT)));
        return downloadInfo;
    }
}
