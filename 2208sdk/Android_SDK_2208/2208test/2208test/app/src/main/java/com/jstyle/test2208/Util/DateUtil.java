package com.jstyle.test2208.Util;

import android.content.Context;



import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/3/24.
 */

public class DateUtil {
    public static final long oneDayMillis = 24 * 60 * 60 * 1000l;
    private static final long oneMinMillis = 60 * 1000l;
    public static final String Default_FormatString="yy.MM.dd";
    public static final String SHOW_FormatString="MM-dd,yyyy";
    public static final SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
    static SimpleDateFormat defaultFormat = new SimpleDateFormat(Default_FormatString);
    public static final String DATA_MONTH_FormatString="yy.MM";
    public static final String DATA_Year_FormatString="yyyy.M";//数据年日期
    public static final int last_month = -1;
    public static final int this_month = 0;
    public static final int next_month = 1;
    public static final int nextWeek = 326;
    public static final int lastWeek = 396;


    /**
     * 明天的日期
     */
    public static String getTomorrowDateString(String today) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getDateLong(today) + oneDayMillis);
        return getDefaultFormatTime(c.getTime());
    }

    public static String getDateString(String today,int days) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getDateLong(today) - oneDayMillis*days);
        return getDefaultFormatTime(c.getTime());
    }
    /**
     * 昨天的日期
     */
    public static synchronized String getYesterdayDateString(String today) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getDateLong(today) - oneDayMillis);
        return getDefaultFormatTime(c.getTime());
    }

    /**
     * 获取指定格式的日期字符串
     *
     * @param time
     * @param formatString
     * @return
     */
    public static synchronized String getFormatTimeString(long time, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(new Date(time));
    }

    public static synchronized String getFormatTimeString(long time) {

        return format.format(new Date(time));
    }
    public static synchronized String getEcgFormatTimeString(long time) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }
    public static synchronized String getEcgFormatTodayString(long time) {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy.MM.dd");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 获取默认格式日期字符串
     *
     * @return
     */
    public static synchronized String getDefaultFormatTime(Date date) {

        return getFormatTimeString(date.getTime(), Default_FormatString);
    }

    /**字符串代表日期的毫秒数
     * @param date
     * @return
     */
    public static synchronized long getDateLong(String date) {

        long time = 0;
        try {
            Date dates = defaultFormat.parse(date);
            time = dates.getTime();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return time;
    }
    public static synchronized long getGpsDateLong(String date) {

        long time = 0;
        try {
            Date dates = format.parse(date);
            time = dates.getTime();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return time;
    }

    /**字符串转date
     * @param dateString
     * @return
     */
    public static Date String2Date(String dateString, String formatString){
        SimpleDateFormat format=new SimpleDateFormat(formatString);
        Date date=null;
        try {
            date=format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String getMonthString(String thisMonth, int monthType){
        Calendar calendar= Calendar.getInstance();
        if(monthType!=this_month) calendar.setTime(String2Date(thisMonth,DATA_MONTH_FormatString));
        calendar.add(Calendar.MONTH,monthType);
        return getFormatTimeString(calendar.getTimeInMillis(),DATA_MONTH_FormatString);
    }
    public static String[] getTodayWeek(long time) {
        SimpleDateFormat format = new SimpleDateFormat(Default_FormatString);
        String[] week = new String[7];
        Calendar calendar = Calendar.getInstance();
        if (time != 0) {
            calendar.setTimeInMillis(time);
        }
        setToFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            week[i] = format.format(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return week;
    }
    private static void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }
    public static String[] getWeekString(String time, int type) {
        SimpleDateFormat format = new SimpleDateFormat(Default_FormatString);
        Date date = null;
        try {
            date = format.parse(time);
        } catch (Exception e) {
            // TODO: handle exception
        }
        long nowL = type == lastWeek?date.getTime() - oneDayMillis :date.getTime() +oneDayMillis ;
        return getTodayWeek(nowL);
    }

    /**时间段在一天中的时间节点（一分钟一点)
     * @param time
     * @param defaultTime
     * @return
     */
    public static int get1MIndex(String time, String defaultTime) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        int count = 0;
        try {
            Date date = format.parse(time);
            Date dateBase = format.parse(defaultTime);
            long min = date.getTime() - dateBase.getTime();
            count = (int) (min / oneMinMillis);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    public static int get1SIndex(String time, String defaultTime) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        int count = 0;
        try {
            Date date = format.parse(time);
            Date dateBase = format.parse(defaultTime);
            long min = date.getTime() - dateBase.getTime();
            count = (int) (min / 1000l);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    public static int get5MIndex(String time, String defaultTime) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        int count = 0;
        try {
            Date date = format.parse(time);
            Date dateBase = format.parse(defaultTime);
            long min = date.getTime() - dateBase.getTime();
            count = (int) (min / (oneMinMillis*5));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    public static int getMIndex(String time, String defaultTime, int countMIN) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        int count = 0;
        try {
            Date date = format.parse(time);
            Date dateBase = format.parse(defaultTime);
            long min = date.getTime() - dateBase.getTime();
            count = (int) (min / (oneMinMillis*countMIN));
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    public static synchronized String getHistoryTime(long time) {
        long defaultTime = DateUtil.getDateLong("00.01.01");
        return DateUtil.getFormatTimeString(time * 1000*5 + defaultTime);
    }

    private static final String TAG = "DateUtil";
    public  synchronized static long getDefaultLongMi(String defaultTime){
        Date date=new Date();
      //  SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
     //   Log.i(TAG, "getDefaultLongMi: "+defaultTime);
        try {
            date = format.parse(defaultTime);
        }catch (Exception e){
         //   Log.i(TAG, "getDefaultLongMi: "+e.getMessage());
        }
        return date.getTime();
    }
    public static String getSleepTime(int minute) {
        int hour = minute / 60;
        int min = minute % 60;
        return String.format("%1$02dh%2$02dmin",hour,min);
    }
    public static String getActivityShowTime(int minute) {
        int hour = minute / 60;
        int min = minute % 60;
        return String.format("%1$02d:%2$02d",hour,min);
    }
    public static String getSportTime(int seconds) {
        int hour=0;
        int min = seconds /60;
        int second=seconds%60;
        if(min>=60){
            hour=min/60;
            min=min%60;
            second = seconds - hour * 3600 - min * 60;
        }
        return  String.format("%1$02d:%2$02d:%3$02d",hour,min,second);
    }
    public static String getExerciseListTotalTime(int seconds) {
        int hour=0;
        int min = seconds /60;
//        int second=seconds%60;
        if(min>=60){
            hour=min/60;
            min=min%60;
            //second = seconds - hour * 3600 - min * 60;
        }
        return  String.format("%1$02dh%2$02dmin",hour,min);
    }
    public static String getSportAxieTime(int seconds) {
        int hour=0;
        int min = seconds /60;
        int second=seconds%60;
        if(min>=60){
            hour=min/60;
            min=min%60;
            second = seconds - hour * 3600 - min * 60;
        }
        return hour==0? String.format("%1$02d:%2$02d",min,second): String.format("%1$02d:%2$02d:%3$02d",hour,min,second);
    }
    public static String getPaceTime(int seconds) {
        int min = seconds /60;
        int second=seconds%60;
//        if(min>60){
//            hour=min/60;
//            min=min%60;
//            second = seconds - hour * 3600 - min * 60;
//        }
        return String.format("%1$02d'%2$02d\"",min,second);
    }
    public static String getActivityTime(int seconds) {
        int hour=0;
        int min = seconds /60;
        int second=seconds%60;
        if(min>60){
            hour=min/60;
            min=min%60;
            second = seconds - hour * 3600 - min * 60;
        }
        return hour==0? String.format("%1$02d:%2$02d",min,second): String.format("%1$02d:%2$02d:%3$02d",hour,min,second);
    }
    public static String getPointSleepTime(float minute) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);

        return nf.format(minute/60f)+"h";
    }
    public static float getSleepTimeFloat(int minute) {
        int hour = minute / 60;
        int min = minute % 60;
        return hour+min/60f;
    }
    /**时间点换成时间段
     * @param count
     * @return
     */
    public static String getCountTime(int count, String defaultTime) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
     //   String base = "00:00:00";
        long time = 0;
        try {
            time = format.parse(defaultTime).getTime() + count * oneMinMillis;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return format.format(new Date(time));
    }
    public static String getSleepCountTime(int count, String defaultTime) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
     //   String base = "00:00:00";
        long time = 0;
        try {
            time = format.parse(defaultTime).getTime() + 5*count * oneMinMillis;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return format.format(new Date(time));
    }
    public static String[] getThisMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
     //  String[] month = new String[3];
        Calendar cal_1 = Calendar.getInstance();// 获取当前日期
        int max=cal_1.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] month = new String[max];
        for(int i=1;i<max+1;i++){
            cal_1.set(Calendar.DAY_OF_MONTH, i);
            month[i-1] = format.format(cal_1.getTime());
        }

        return month;
    }


    public static String[] getNextMonth(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
        Date dates = new Date();
        try {
            dates = format.parse(date);
        } catch (Exception e) {
            // TODO: handle exception
        }
        Calendar cal_1 = Calendar.getInstance();// 获取当前日期

        cal_1.setTime(dates);
        int months = cal_1 .get(Calendar.MONTH);
        int year =cal_1 .get(Calendar.YEAR);
        if (months == Calendar.DECEMBER) {
            year++;
            months = Calendar.JANUARY;
        } else {
            months++;
        }
        cal_1.set(Calendar.YEAR, year);
        cal_1.set(Calendar.MONTH, months);
        int max=cal_1.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] month = new String[max];
        for(int i=1;i<max+1;i++){
            cal_1.set(Calendar.DAY_OF_MONTH, i);
            month[i-1] = format.format(cal_1.getTime());
        }

//        cal_1.set(Calendar.DAY_OF_MONTH,
//                cal_1.getActualMaximum(Calendar.DAY_OF_MONTH));// 设置为1号,当前日期既为本月第一天
//        month[1] = format.format(cal_1.getTime());
//        month[2]=cal_1.getActualMaximum(Calendar.DAY_OF_MONTH)+"";
        return month;
    }
    public static String[] getMonth(int year, int month) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
        Calendar cal_1 = Calendar.getInstance();// 获取当前日期
        cal_1.set(Calendar.YEAR, year);
        cal_1.set(Calendar.MONTH, month);
        int max=cal_1.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] months = new String[max];
        for(int i=1;i<max+1;i++){
            cal_1.set(Calendar.DAY_OF_MONTH, i);
            months[i-1] = format.format(cal_1.getTime());
        }
        return months;
    }

    public static String[] getLastMonth(String date) {

        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");
        Date dates = new Date();
        try {
            dates = format.parse(date);
        } catch (Exception e) {
            // TODO: handle exception
        }

        Calendar cal_1 = Calendar.getInstance();// 获取当前日期
        cal_1.setTime(dates);
        int months = cal_1 .get(Calendar.MONTH);
        int year =cal_1 .get(Calendar.YEAR);
        if (months == Calendar.JANUARY) {
            year--;
            months = Calendar.DECEMBER;
        } else {
            months--;
        }
        cal_1.set(Calendar.YEAR, year);
        cal_1.set(Calendar.MONTH, months);
        int max=cal_1.getActualMaximum(Calendar.DAY_OF_MONTH);
        String[] month = new String[max];
        for(int i=1;i<max+1;i++){
            cal_1.set(Calendar.DAY_OF_MONTH, i);
            month[i-1] = format.format(cal_1.getTime());
        }
        // 设置为1号,当前日期既为本月第一天


//        cal_1.set(Calendar.DAY_OF_MONTH,
//                cal_1.getActualMaximum(Calendar.DAY_OF_MONTH));// 设置为1号,当前日期既为本月第一天
//        month[1] = format.format(cal_1.getTime());

        return month;
    }
    public static  boolean beforeData(String lastTime, String bleTime){
        boolean isBefore=false;
        try {
            Date lastDate=format.parse(lastTime);
            Date bleDate=format.parse(bleTime);
            isBefore=lastDate.before(bleDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  isBefore;
    }

    public static String getCountTime(int count) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String base = "12:00";
        long time = 0;
        try {
            time = format.parse(base).getTime() + count * 5 * 60 * 1000l;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return format.format(new Date(time));
    }
    public static String getShowDate(Context context, String monthString) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        long time = DateUtil.getDateLong(monthString);
        String format = "";
      //  if (language.equals("zh")) {
            format = "yyyy-MM";
//        } else {
//            format = "MM-yyyy";
//        }
        return DateUtil.getFormatTimeString(time, format);
    }
    private static String getTimestamp() {
        return System.currentTimeMillis() / 1000 + "";
    }
}
