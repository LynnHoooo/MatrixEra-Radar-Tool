package com.jstyle.test2208.daomananger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;


import com.jstyle.test2208.dao.DaoMaster;
import com.jstyle.test2208.dao.DaoSession;

import org.greenrobot.greendao.async.AsyncSession;

import java.io.File;

/**
 * Created by Administrator on 2017/4/10.
 */

public class DbManager {
    private static DbManager ourInstance;
    private static DaoSession mDaoSession;
    private static DaoMaster mDaoMaster;
    private static MySqlLiteOpenHelper devOpenHelper;

    public static void init(Context context){
        getInstance();
        String path= Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator;
        devOpenHelper=new MySqlLiteOpenHelper(context,"jstyle_2025.db");
    }
    public static DbManager getInstance() {
        if (ourInstance == null) {
            synchronized (DbManager.class) {
                if (ourInstance == null) {
                    ourInstance = new DbManager();
                }
            }
        }
        return ourInstance;
    }

    private DbManager(){

    }

    private SQLiteDatabase getReadDatabase() {
        return devOpenHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWriteDatabase() {

        return devOpenHelper.getWritableDatabase();
    }
    /**
     * 获取DaoMaster
     *
     * @param
     * @return
     */
    public  DaoMaster getDaoMaster() {
                if (null == mDaoMaster) {
                    mDaoMaster = new DaoMaster(getWriteDatabase());
                }
        return mDaoMaster;
    }

    /**
     * 获取DaoSession
     *
     * @param
     * @return
     */
    public DaoSession getDaoSession() {
            return getDaoMaster().newSession();
    }

    public static void insertByAsyncSession(){
        //getDaoSession()
    }

    public  AsyncSession getAsyncSession(){
        return getDaoSession().startAsyncSession();
    }

}
