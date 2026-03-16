package com.jstyle.test2208.Util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.jstyle.test2208.BuildConfig;
import com.jstyle.test2208.Myapp;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SD卡管理操�?
 * @author Jerry Lee
 *
 */
public class SDUtil {
	private static final String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APPLICATION_ID;
	private static final File CacheDir= Myapp.getInstance().getExternalCacheDir();
	public static final String log = (Build.VERSION.SDK_INT >= 30 ? CacheDir : baseDir)+ "/log/";
	private static double MB = 1024;
	private static double FREE_SD_SPACE_NEEDED_TO_CACHE = 10;
	private static double IMAGE_EXPIRE_TIME = 10;

	

	

	public static String getDateString(Date date) {
		return formatDate(date, "yyyy-MM-dd");
	}
	public static String formatDate(Date date, String format) {
		String result = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			result = sdf.format(date);
		} catch (Exception e) {

		}

		return result;
	}

	

	public static void createFile(String fileName){
		if (fileName == null)
			return;
		// 不存在则创建目录
		File dir = new File(log);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String rFileName = log+fileName+".txt";
		File file = new File(rFileName);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 保存蓝牙命令log（测试用�?
	 * 
	 * @param fileName
	 * @param content
	 */
	public static void saveBTLog(String fileName, String content) {
		if (content == null || fileName == null)
			return;
		// 不存在则创建目录
		File dir = new File(log);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String rFileName = log+fileName+".txt";
		File file = new File(rFileName);
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		OutputStreamWriter os = null;
		try {
			os = new OutputStreamWriter(new FileOutputStream(file,true)) ;
			os.write("\r\n"+content);
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}


	/**
	 * 文件分享
	 *
	 * @param context
	 * @param path
	 */
	public static void sharePdfByPhone(Activity context, String path) {
		Uri uri = null;
		Intent shareIntent = new Intent();
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(path));
			shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			uri = Uri.fromFile(new File(path));
		}
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
		shareIntent.setType("*/*");
		context.startActivity(Intent.createChooser(shareIntent, "share"));
	}
}