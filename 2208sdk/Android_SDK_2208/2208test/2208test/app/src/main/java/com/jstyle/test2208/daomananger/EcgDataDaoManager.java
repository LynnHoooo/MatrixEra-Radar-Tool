package com.jstyle.test2208.daomananger;

import android.text.TextUtils;

import com.jstyle.test2208.dao.EcgHistoryDataDao;
import com.jstyle.test2208.model.EcgHistoryData;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */

public class EcgDataDaoManager {
    public static void insertData(EcgHistoryData healthData){
        DbManager.getInstance().getDaoSession().getEcgHistoryDataDao().insertOrReplace(healthData);
    }
    public static void insertData(List<EcgHistoryData>healthDataList){
        DbManager.getInstance().getDaoSession().getEcgHistoryDataDao().insertOrReplaceInTx(healthDataList);
    }
    public static EcgHistoryData getLastEcgData(String address) {
        QueryBuilder<EcgHistoryData> queryBuilder = DbManager.getInstance().getDaoSession().getEcgHistoryDataDao().queryBuilder();
        return queryBuilder.where(EcgHistoryDataDao.Properties.Address.eq(address)).list().size()==0?null:queryBuilder.where(EcgHistoryDataDao.Properties.Address.eq(address)).orderDesc(EcgHistoryDataDao.Properties.Time).list().get(0);
    }

    public static EcgHistoryData queryEcgHistoryData(String mac,String queryDate){
        EcgHistoryData ecgHistoryData=null;
        if(TextUtils.isEmpty(queryDate)||TextUtils.isEmpty(mac))return ecgHistoryData;
        QueryBuilder<EcgHistoryData> queryBuilder = DbManager.getInstance().getDaoSession().getEcgHistoryDataDao().queryBuilder();
        return queryBuilder.where(EcgHistoryDataDao.Properties.Address.eq(mac),EcgHistoryDataDao.Properties.Time.eq(queryDate)).unique();

    }
}



