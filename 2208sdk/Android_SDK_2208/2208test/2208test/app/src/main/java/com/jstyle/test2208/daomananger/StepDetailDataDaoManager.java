package com.jstyle.test2208.daomananger;

import android.text.TextUtils;


import com.jstyle.test2208.Util.SharedPreferenceUtils;
import com.jstyle.test2208.dao.StepDetailDataDao;
import com.jstyle.test2208.model.StepDetailData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */

public class StepDetailDataDaoManager {
    public static void insertData(List<StepDetailData> stepDataList){
        DbManager.getInstance().getDaoSession().getStepDetailDataDao().insertOrReplaceInTx(stepDataList);
    }
    public static List<StepDetailData> queryData(String date){
        return queryData(date,date);
    }
    public static List<StepDetailData> queryAllData(){
        QueryBuilder<StepDetailData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDetailDataDao().queryBuilder();
        return queryBuilder.list();
    }
    public static List<StepDetailData> queryAllDataAddress(){
        List<StepDetailData> stepDataList=new ArrayList<>();
        QueryBuilder<StepDetailData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDetailDataDao().queryBuilder();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
       if(TextUtils.isEmpty(address))return stepDataList;
        return queryBuilder.where( StepDetailDataDao.Properties.Address.eq(address)).list();
    }
    public static List<StepDetailData> queryData(String startDate, String endDate){
        List<StepDetailData> stepDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);

        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return stepDataList;
        QueryBuilder<StepDetailData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDetailDataDao().queryBuilder();
        stepDataList = queryBuilder.where( StepDetailDataDao.Properties.Address.eq(address),StepDetailDataDao.Properties.Date.between(startDate+" 00:00:00", endDate+" 23:59:59" )).orderAsc(StepDetailDataDao.Properties.Date).list();
        return stepDataList;
    }
    public static List<StepDetailData> queryGpsStep(String startDate, String endDate){
        List<StepDetailData> stepDataList=new ArrayList<>();
        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate))return stepDataList;
        QueryBuilder<StepDetailData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDetailDataDao().queryBuilder();
        stepDataList = queryBuilder.where( StepDetailDataDao.Properties.Date.between(startDate, endDate )).orderAsc(StepDetailDataDao.Properties.Date).list();
        return stepDataList;
    }
    public static String getLastInsertDataTime() {
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(address))return "";
        QueryBuilder<StepDetailData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDetailDataDao().queryBuilder();
        return queryBuilder.where(StepDetailDataDao.Properties.Address.eq(address)).list().size() == 0 ? "" : queryBuilder.where(StepDetailDataDao.Properties.Address.eq(address)).orderDesc(StepDetailDataDao.Properties.Date).list().get(0).getDate();
    }
    public static void deleteAll(){
        DbManager.getInstance().getDaoSession().getStepDetailDataDao().deleteAll();;
    }
}
