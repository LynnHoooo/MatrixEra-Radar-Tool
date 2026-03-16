package com.jstyle.test2208.daomananger;

import android.text.TextUtils;


import com.jstyle.test2208.Util.SharedPreferenceUtils;
import com.jstyle.test2208.dao.HeartDataDao;
import com.jstyle.test2208.model.HeartData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */

public class HeartDataDaoManager {
    public static void insertData(HeartData heartData){
        DbManager.getInstance().getDaoSession().getHeartDataDao().insertOrReplace(heartData);
    }
    public static void insertData(List<HeartData> heartDataList){
        DbManager.getInstance().getDaoSession().getHeartDataDao().insertOrReplaceInTx(heartDataList);
    }
    public static List<HeartData> queryData(String date){

        return queryData(date,date);
    }
    public static List<HeartData> queryAllData(){
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();

        return queryBuilder.list();
    }
    public static List<HeartData> queryAllDataByAddress(){
        List<HeartData> heartDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(address))return heartDataList;
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        return queryBuilder.where(HeartDataDao.Properties.Address.eq(address)).list();
    }
    public static List<HeartData> queryData(String startDate, String endDate){//睡眠从当天12点到第二天12点
        List<HeartData> heartDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return heartDataList;
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        heartDataList = queryBuilder.where(HeartDataDao.Properties.Address.eq(address), HeartDataDao.Properties.Time.between(startDate+" 00:00:00" , endDate+" 23:59:00")).orderAsc(HeartDataDao.Properties.Time).list();
        return heartDataList;
    }
    public static List<HeartData> queryExerciseData(String startDate, String endDate){
        List<HeartData> heartDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return heartDataList;
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        heartDataList = queryBuilder.where( HeartDataDao.Properties.Address.eq(address),HeartDataDao.Properties.Time.between(startDate+" 00:00:00" , endDate+" 23:59:00")).orderAsc(HeartDataDao.Properties.Time).list();
        return heartDataList;
    }
    public static List<HeartData> queryGpsData(String startDate, String endDate){
        List<HeartData> heartDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);;
        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return heartDataList;
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        heartDataList = queryBuilder.where( HeartDataDao.Properties.Address.eq(address),HeartDataDao.Properties.Time.between(startDate , endDate)).orderAsc(HeartDataDao.Properties.Time).list();
        return heartDataList;
    }
    public static List<HeartData> queryDayData(String queryDate){
        List<HeartData> heartDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(queryDate)|| TextUtils.isEmpty(address))return heartDataList;
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        heartDataList = queryBuilder.where(HeartDataDao.Properties.Address.eq(address), HeartDataDao.Properties.Time.between(queryDate+" 00:00:00" , queryDate+" 23:59:59")).orderAsc(HeartDataDao.Properties.Time).list();
        return heartDataList;
    }
    public static List<HeartData> queryYearData(String queryDate, String endDate){
        List<HeartData> heartDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(queryDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return heartDataList;
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        heartDataList = queryBuilder.where( HeartDataDao.Properties.Address.eq(address),HeartDataDao.Properties.Time.between(queryDate+" 00:00:00" , endDate+" 23:59:59")).orderAsc(HeartDataDao.Properties.Time).list();
        return heartDataList;
    }
    public static HeartData getLastHeartData() {
        QueryBuilder<HeartData> queryBuilder = DbManager.getInstance().getDaoSession().getHeartDataDao().queryBuilder();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(address))return null;
        return queryBuilder.where(HeartDataDao.Properties.Address.eq(address)).list().size() == 0 ? null : queryBuilder.where(HeartDataDao.Properties.Address.eq(address)).orderDesc(HeartDataDao.Properties.Time).list().get(0);
    }
    public static void deleteAll(){
        DbManager.getInstance().getDaoSession().getHeartDataDao().deleteAll();;
    }
}
