package com.jstyle.test2208;

import android.app.Application;

import com.jstyle.test2208.Util.SharedPreferenceUtils;
import com.jstyle.test2208.ble.BleManager;
import com.jstyle.test2208.daomananger.DbManager;

public class Myapp extends Application {
    private static Myapp instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DbManager.init(this);
        SharedPreferenceUtils.init(this);
        BleManager.init(this);
    }

    public static Myapp getInstance() {
        return instance;
    }
}
