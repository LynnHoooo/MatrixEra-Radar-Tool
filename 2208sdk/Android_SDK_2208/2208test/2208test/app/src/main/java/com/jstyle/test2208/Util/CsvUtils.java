package com.jstyle.test2208.Util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;


import com.jstyle.test2208.BuildConfig;
import com.jstyle.test2208.Myapp;
import com.jstyle.test2208.R;
import com.jstyle.test2208.daomananger.HeartDataDaoManager;
import com.jstyle.test2208.daomananger.SleepDataDaoManager;
import com.jstyle.test2208.daomananger.StepDetailDataDaoManager;
import com.jstyle.test2208.model.CsvModel;
import com.jstyle.test2208.model.HeartData;
import com.jstyle.test2208.model.SleepData;
import com.jstyle.test2208.model.StepDetailData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CsvUtils {
    private final static String baseDir = Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + BuildConfig.APPLICATION_ID;
    private static final File CacheDir= Myapp.getInstance().getExternalCacheDir();
    public final static String testPath = (Build.VERSION.SDK_INT >= 30 ? CacheDir : baseDir) + "/test/";
    private static final long oneMinMillis = 60 * 1000l;

    public static void createCsvFile(List<List<String>> data) {
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        csvFile = new File(testPath + "test.csv");
        File parent = csvFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try {
            if (csvFile.exists()) csvFile.delete();
            csvFile.createNewFile();
                csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile,true), "GB2312"), 1024);
              for (List<String> dd:data) { writeRowB(dd, csvWtriter); }
            csvWtriter.flush();
            csvWtriter.close();
        } catch (IOException e) {
            Log.e("jdnjdnjsdn",e.toString());
            e.printStackTrace();
        }

    }



    public static void createCsvFileer(List<String> data) {
        File csvFile = null;
        BufferedWriter csvWtriter = null;
        csvFile = new File(testPath + "tester.csv");
        File parent = csvFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try {
                if (csvFile.exists()) csvFile.delete();
                csvFile.createNewFile();
                csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile,true), "GB2312"), 1024);
                for (String dd:data) {
                    writeRow(dd, csvWtriter);
                }
                csvWtriter.flush();
                csvWtriter.close();

        } catch (IOException e) {
            Log.e("jdnjdnjsdn",e.toString());
            e.printStackTrace();
        }

    }

    public static void shareByPhone(Context context, String path) {
        Uri imageUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri = FileProvider.getUriForFile(context.getApplicationContext(),
                    BuildConfig.APPLICATION_ID + ".provider", new File(path));
        } else {
            imageUri = Uri.fromFile(new File(path));
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("*/*");
        context.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }

    private static final String TAG = "CsvUtils";

    private static void writeRowA(List<String> row, BufferedWriter csvWriter) throws IOException {
        for ( String aa :row ){
            csvWriter.write(aa+",");
        }
        csvWriter.newLine();
    }

    private static void writeRowB(List<String> row, BufferedWriter csvWriter) throws IOException {
        for ( String aa :row ){
            csvWriter.write(aa+",");
        }
        csvWriter.newLine();
    }
    private static void writeRow(String DD, BufferedWriter csvWriter) throws IOException {
        csvWriter.newLine();
        String row[]=DD.split(" ");
        for (Object data : row) {
            String rowStr =data+",";
            csvWriter.write(rowStr);
        }
    }
    private static void writeRowContent(String[] values, BufferedWriter csvWriter) throws IOException {
        for (String data : values) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(TextUtils.isEmpty(data)?"--":data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();

    }

    private static void writeRowContent(CsvModel csvModel, BufferedWriter csvWriter) throws IOException {

        // for (String data : values) {
        StringBuffer sb = new StringBuffer();
        String rowStr = sb.append("\"").append(csvModel.getDate()).append("\",").toString();
        csvWriter.write(rowStr);
        sb = new StringBuffer();
        String heartRateString = sb.append("\"").append(csvModel.getHeartRate()).append("\",").toString();
        csvWriter.write(heartRateString);
        sb = new StringBuffer();
        String calS = sb.append("\"").append(csvModel.getCal()).append("\",").toString();
        csvWriter.write(calS);
        sb = new StringBuffer();
        String disS = sb.append("\"").append(csvModel.getDistance()).append("\",").toString();
        csvWriter.write(disS);
        sb = new StringBuffer();
        String stepS = sb.append("\"").append(csvModel.getStep()).append("\",").toString();
        csvWriter.write(stepS);
        sb = new StringBuffer();
        String sleepS = sb.append("\"").append(csvModel.getSleepQuality()).append("\",").toString();
        csvWriter.write(sleepS);
        //  }
        csvWriter.newLine();

    }

    private static void initData(SparseArray<CsvModel> hashMap, String startDate) throws IOException {
        int size = 1440 * 31;
        try {
            long startTime=format.parse(startDate).getTime();
            for (int i = 0; i < size; i++) {
                String date = getCountTime(i, startTime);
                CsvModel csvModel = new CsvModel();
                csvModel.setDate(date);
                hashMap.put(i, csvModel);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
    private static void initDataString(HashMap<Integer, String[]> hashMap, String startDate) throws IOException {

        int length = 6;
        int size = 1440 * 31;
//        for (int i = 0; i < size; i++) {
//            String[] data = new String[length];
//            String date = getCountTime(i, startDate);
//            data[0]=date;
//            hashMap.put(i, data);
//        }

    }

    private static List<Integer> getSleepTime(int[] fiveSleepData) {
        List<Integer> list = new ArrayList<>();
        int goBed = -1;
        int upBed = 0;
        int offCount = 0;
        for (int i = 0; i < fiveSleepData.length; i++) {
            int data = fiveSleepData[i];
            if (data != -1) {
                offCount = 0;
                if (goBed == -1) {
                    list.add(i);
                    goBed = i;
                }

            } else {
                offCount++;
                if (offCount == 6 && goBed != -1) {//30分钟离床
                    list.add(i - 5);
                    offCount = 0;
                    goBed = -1;
                }
            }
            if (i == fiveSleepData.length - 1 && goBed != -1) {
                list.add(i);
            }
        }
        return list;
    }

    public static int get1MIndex(String time, String defaultTime) {
        SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
        int count = 0;
        try {
            Date date = format.parse(time + " 00:00:00");
            Date dateBase = format.parse(defaultTime);
            long min = dateBase.getTime() - date.getTime();
            count = (int) (min / oneMinMillis);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return count;
    }
    static SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd HH:mm:ss");
    public static String getCountTime(int count, long defaultTime) {

        //   String base = "00:00:00";
        long time = defaultTime + count * oneMinMillis;

        return format.format(new Date(time));
    }

}
