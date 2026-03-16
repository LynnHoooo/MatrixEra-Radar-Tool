package com.jstyle.test2208.daomananger;

import android.text.TextUtils;


import com.jstyle.test2208.Util.SharedPreferenceUtils;
import com.jstyle.test2208.dao.SleepDataDao;
import com.jstyle.test2208.model.SleepData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */

public class SleepDataDaoManager {
    public static void insertData(SleepData sleepData){
        DbManager.getInstance().getDaoSession().getSleepDataDao().insertOrReplace(sleepData);
    }
    public static void insertData(List<SleepData> SleepDataList){
        DbManager.getInstance().getDaoSession().getSleepDataDao().insertOrReplaceInTx(SleepDataList);
    }
    public static List<SleepData> queryData(String date){

        return queryData(date,date);
    }
    public static List<SleepData> queryAllData(){
        QueryBuilder<SleepData> queryBuilder = DbManager.getInstance().getDaoSession().getSleepDataDao().queryBuilder();

        return queryBuilder.list();
    }
    public static List<SleepData> queryData(String startDate, String endDate){
        List<SleepData> sleepDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return sleepDataList;
        QueryBuilder<SleepData> queryBuilder = DbManager.getInstance().getDaoSession().getSleepDataDao().queryBuilder();
        sleepDataList = queryBuilder.where( SleepDataDao.Properties.Address.eq(address),SleepDataDao.Properties.Time.between(startDate+" 00:00:00" , endDate+" 23:59:59")).orderAsc(SleepDataDao.Properties.Time).list();
        return sleepDataList;
    }
    public static void deleteAll(){
        DbManager.getInstance().getDaoSession().getSleepDataDao().deleteAll();;
    }
    public static String getLastInsertDataTime() {
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(address))return "";
        QueryBuilder<SleepData> queryBuilder = DbManager.getInstance().getDaoSession().getSleepDataDao().queryBuilder().where(SleepDataDao.Properties.Address.eq(address));
        return queryBuilder.list().size() == 0 ? "" : queryBuilder.orderDesc(SleepDataDao.Properties.Time).list().get(0).getTime();
    }
}
