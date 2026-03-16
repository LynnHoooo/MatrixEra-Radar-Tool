package com.jstyle.test2208.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.demon.js_pdf.WebViewHelper;
import com.jstyle.blesdk2208.Util.PDFCreate;
import com.jstyle.blesdk2208.model.UserInfo;
import com.jstyle.test2208.BuildConfig;
import com.jstyle.test2208.Myapp;
import com.jstyle.test2208.R;
import com.jstyle.test2208.Util.SchedulersTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class PDFActivity extends BaseActivity {

    @BindView(R.id.history_webview)
    WebView webView;
    private Disposable disposablePdf;
    List<Integer> ecgData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ecgppg_pdf);
        ButterKnife.bind(this);
        ecgData=(ArrayList<Integer>) getIntent().getIntegerArrayListExtra("ecgData");
        WebviewSetting();
        requestPermission(PDFActivity.this);
    }

    private void init() {
        createFile(ecgData);
    }
    String Path="";
    private final static String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + BuildConfig.APPLICATION_ID;
    public final static String pdfPath=(Build.VERSION.SDK_INT >= 30? Myapp.getInstance().getExternalCacheDir():baseDir)+"/pdf/";
    private void createFile( final List<Integer> ecgData) {
        showProgressDialog("loding...");

       io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                File dir = new File(pdfPath);
                //有没有当前文件夹就创建一个，注意读写权限
                // Create one if there is no current folder. Pay attention to read and write permissions
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                Path= pdfPath+ "ecghistory" + ".pdf";
                UserInfo userInfo = new UserInfo();
                userInfo.setGender("gender:male");
                userInfo.setAge("age:18");
                userInfo.setDate("date:2022/02/18");
                userInfo.setName("name:hello");
                userInfo.setHeight("height:170cm");
                userInfo.setWeight("weight:75kg");
                userInfo.setEcgTitle("ECG Report(Lead Ⅰ)");//not null
                userInfo.setEcgReportTips("The report is for reference only");//not null
                PDFCreate.createPdf(Path, PDFActivity.this, ecgData, userInfo);
                emitter.onComplete();
            }
        }).compose(SchedulersTransformer.applySchedulers()).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("jssjsjj",e.toString());
                disMissProgressDialog();
            }

            @Override
            public void onComplete() {
                disMissProgressDialog();
                WebViewHelper.WebViewLoadPDF(webView, Path);
            }
        });


    }

    @OnClick({R.id.back_eegrt,R.id.report})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back_eegrt:
                finish();
                break;
            case R.id.report:
                sharePdfByPhone(PDFActivity.this,Path);
                break;
        }

    }

    private void requestPermission(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                init();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, 0);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            }
        }else{
            init();
        }
    }





    public void onRequestPermissionsResult(int requestCode,

                                           String[] permissions,

                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {

            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private  void WebviewSetting(){

        WebSettings webSettings = webView.getSettings();
        webSettings.setUserAgentString("User-Agent:Android");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSupportZoom(true);//缩放开关，设置此属性，仅支持双击缩放，不支持触摸缩放
        webSettings.setBuiltInZoomControls(true);  //设置是否可缩放，会出现缩放工具（若为true则上面的设值也默认为true）
        webSettings.setDisplayZoomControls(false);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
        webView.setBackgroundColor(Color.WHITE);
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






    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposablePdf != null && !disposablePdf.isDisposed()) {
            disposablePdf.dispose();
            disposablePdf=null;
        }
    }



}
