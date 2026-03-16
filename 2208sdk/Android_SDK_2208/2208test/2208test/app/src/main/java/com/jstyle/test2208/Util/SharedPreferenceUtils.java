package com.jstyle.test2208.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtils {
    public static final String KEY_REMIND_PSD = "REMIND_PSD";
    public static final String KEY_account_type = "KEY_account_type";
    public static final String KEY_countryCode = "KEY_countryCode";
    public static final String KEY_TimeZone = "KEY_TimeZone";
    public static final String KEY_DFU_ADDRESS = "KEY_DFU_ADDRESS";
    public static final String KEY_Exercise_Mode = "KEY_Exercise_Mode";
    public static final String KEY_Enable_ActivityClock = "KEY_Enable_ActivityClock";
    public static final String KEY_UNIT_WEIGHT = "KEY_UNIT_WEIGHT";
    public static final String KEY_UNIT_HEIGHT = "KEY_UNIT_HEIGHT";
    public static final String KEY_RES_FILEPATH = "KEY_RES_FILEPATH";
    public static final String KEY_Is_First = "KEY_Is_First";
    public static final String KEY_From_Login = "KEY_From_Login";
    public static final String KEY_PHONE = "PHONE";
    public static final String KEY_UID = "uid";
    public static final String KEY_PSD = "PSD";
    public static final String KEY_ADDRESS = "ADDRESS";
    public static final String KEY_Language = "Language";
    public static final String KEY_LOGIN = "lOGIN";
    public static final String KEY_NICKNAME = "nickname";
    private static final String spName = "jstyle_sleep";
    public static final String KEY_AUTH_USERID = "KEY_AUTH_USERID";
    public static final String KEY_highHeart = "highHeart";
    public static final String KEY_highBrEATH = "highBrEATH";
    public static final String KEY_lowHeart = "lowHeart";
    public static final String KEY_lowBreath = "lowBreath";
    public static final String KEY_deviceName = "deviceName";
    public static final String KEY_PLAN_ON = "PLAN_ON";
    public static final String KEY_PLAN_first = "PLAN_first";
    public static final String KEY_Sleep_goal = "KEY_Sleep_goal";
    public static final String KEY_Step_goal = "KEY_Step_goal";
    public static final String KEY_deviceType = "KEY_deviceType";
    private static SharedPreferences sp;
    public static final String DefaultCountryCode = "0086-";
    public static void init(Context context) {
        sp = context.getSharedPreferences(spName, Activity.MODE_PRIVATE);
    }

    public static SharedPreferences getSharedPreferences() {
        return sp;
    }

    public static void setSpBoolean(String name, boolean bool) {

        getSharedPreferences().edit().putBoolean(name, bool).commit();
    }

    public static boolean getSpBoolean(String name) {
        return getSharedPreferences().getBoolean(name, false);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return getSharedPreferences().getBoolean(name, defaultValue);
    }

    public static void setSpString(String name, String value) {
        getSharedPreferences().edit().putString(name, value).commit();
    }

    public static String getSpString(String name) {
        return getString(name, null);
    }

    public static String getString(String name, String defaultValue) {
        return getSharedPreferences().getString(name, defaultValue);
    }

    public static void setSpInteger(String name, int value) {
        getSharedPreferences().edit().putInt(name, value).commit();
    }

    public static int getInteger(String name, int defaultValue) {
        return getSharedPreferences().getInt(name, defaultValue);
    }

    public static int getSpInteger(String name) {
        return getInteger(name, -1);
    }

    public static void setSpFloat(String name, float value) {
        getSharedPreferences().edit().putFloat(name, value).commit();
    }

    public static float getSpFloat(String name) {
        return getFloat(name, 0.0f);
    }

    public static float getFloat(String name, float defaultValue) {
        return getSharedPreferences().getFloat(name, defaultValue);
    }
}
