package com.jstyle.test2208.daomananger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jstyle.test2208.dao.DaoMaster;
import com.jstyle.test2208.dao.EcgHistoryDataDao;
import com.jstyle.test2208.dao.HeartDataDao;
import com.jstyle.test2208.dao.SleepDataDao;
import com.jstyle.test2208.dao.StepDataDao;
import com.jstyle.test2208.dao.StepDetailDataDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by Administrator on 2017/5/19.
 */

public class MySqlLiteOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = "MySqlLiteOpenHelper";
    public MySqlLiteOpenHelper(Context context, String name) {
        super(context, name);
    }

    public MySqlLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        Log.i(TAG,"oldVersion:"+oldVersion+",newVersion"+newVersion);
        MigrationHelper.getInstance().migrate(db, StepDataDao.class, StepDetailDataDao.class,
              HeartDataDao.class, SleepDataDao.class, EcgHistoryDataDao.class);

    }
}
