package com.jstyle.test2208.daomananger;

import android.text.TextUtils;


import com.jstyle.test2208.Util.SharedPreferenceUtils;
import com.jstyle.test2208.dao.StepDataDao;
import com.jstyle.test2208.model.StepData;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */

public class StepDataDaoManager {
    public static void insertData(StepData stepData){
        DbManager.getInstance().getDaoSession().getStepDataDao().insertOrReplace(stepData);
    }
    public static void insertData(List<StepData> stepDataList){
        DbManager.getInstance().getDaoSession().getStepDataDao().insertOrReplaceInTx(stepDataList);
    }
    public static StepData queryData(String date){
        StepData stepData=null;
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);
        if(TextUtils.isEmpty(date)|| TextUtils.isEmpty(address))return stepData;
        QueryBuilder<StepData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDataDao().queryBuilder();
        stepData = queryBuilder.where( StepDataDao.Properties.Address.eq(address),StepDataDao.Properties.Date.eq(date )).unique();
        return stepData;
    }
    public static List<StepData> queryAllData(){
        List<StepData> stepDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);

        if(TextUtils.isEmpty(address))return stepDataList;
        QueryBuilder<StepData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDataDao().queryBuilder();

        stepDataList = queryBuilder.where( StepDataDao.Properties.Address.eq(address)).orderDesc(StepDataDao.Properties.Date).list();
        return stepDataList;
    }
    public static List<StepData> queryData(String startDate, String endDate){
        List<StepData> stepDataList=new ArrayList<>();
        String address= SharedPreferenceUtils.getSpString(SharedPreferenceUtils.KEY_ADDRESS);

        if(TextUtils.isEmpty(startDate)|| TextUtils.isEmpty(endDate)|| TextUtils.isEmpty(address))return stepDataList;
        QueryBuilder<StepData> queryBuilder = DbManager.getInstance().getDaoSession().getStepDataDao().queryBuilder();
        stepDataList = queryBuilder.where( StepDataDao.Properties.Address.eq(address),StepDataDao.Properties.Date.between(startDate , endDate)).orderDesc(StepDataDao.Properties.Date).list();
        return stepDataList;
    }
    public static void deleteAll(){
        DbManager.getInstance().getDaoSession().getStepDataDao().deleteAll();;
    }
}
