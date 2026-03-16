package com.jstyle.test2208.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;


/**
 * 自定义心电图
 *
 * @author manhongjie
 */
// public class EcgGraphicView<AttributeSet> extends View {
public class EcgGraphicView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private final SurfaceHolder mSurfaceHolder;
    private Paint mPaint, blackPaint, wangge_Paint, pixPaint;
    private Path mPath = new Path();


    // 屏幕的长�?
    private float SCREEN_WIDTH = 480;

    //
    private int POINT_LEN = 3000;

    private int rulerStartX = 0;

    // public float[] x = new float[POINT_LEN];
    // public float[] y = new float[POINT_LEN];
    // 数组中有3000个点
    public float[] x = new float[POINT_LEN];
    public float[] y = new float[POINT_LEN];

    public float scale = 0;
    public float x0 = 240;
    public float y0 = 192;
    private Context context;
    public float distanceY = 0;
    private boolean isDrawing;
    private Canvas mCanvas;

    public EcgGraphicView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        // 分辨率矩�?
        DisplayMetrics dm = new DisplayMetrics();
        // 取得窗口属�??

        if (context instanceof Activity) {
            Activity activity1 = (Activity) context;
            activity1.getWindowManager().getDefaultDisplay().getMetrics(dm);
            SCREEN_WIDTH = dm.widthPixels;
            Log.d("EcgGraphicView", "width:" + " " + SCREEN_WIDTH);
        }

        mPath = new Path();

        // 画心电图的画�?
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(ScreenUtils.dip2px(context, 1));
        mPaint.setTextSize(2);
        mPaint.setColor(Color.WHITE);
        this.context = context;
        // blackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // blackPaint.setStyle(Paint.Style.STROKE);
        // blackPaint.setStrokeWidth(2);
        // blackPaint.setTextSize(2);
        // blackPaint.setColor(Color.RED);
        // 网格线画�?
        // wangge_Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // wangge_Paint.setStyle(Paint.Style.STROKE);
        // wangge_Paint.setStrokeWidth(1.0f);
        // wangge_Paint.setTextSize(0.5f);
        // wangge_Paint.setColor(Color.parseColor("#000000"));

        // pixPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // pixPaint.setStyle(Paint.Style.FILL);
        // pixPaint.setStrokeWidth(2.0f);
        // pixPaint.setTextSize(20.0f);
        // pixPaint.setColor(Color.RED);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

//	@Override
//	public void onDraw(Canvas canvas) {
//
//		float width = SCREEN_WIDTH;
//		scale = 1;
//
//		// String ruleStr = new String(scale * 10 + "mm/100mv");
//		// 移动后的坐标
//		 makeFollowPathOne();
//
//		// rulerStartX = SCREEN_WIDTH - 60;
//		// 每列的间隔是32
//		// int colomn = (int) (width / 32);
//
//		// 画网格线行和�?
//
//		// for (int i = 0; i <= 13; i++) {
//		// canvas.drawLine(0, i * 32, width, i * 32, wangge_Paint);
//		// }
//		// for (int i = 0; i <= colomn; i++) {
//		// canvas.drawLine(i * 32, 0, i * 32, 12 * 32, wangge_Paint);
//		// }
//
//		// 画标�?
//
//		// canvas.drawLine(rulerStartX + 0, 192, rulerStartX + 8, 192,
//		// pixPaint);
//		// canvas.drawLine(rulerStartX + 24, 192, rulerStartX + 32, 192,
//		// pixPaint);
//		// canvas.drawLine(rulerStartX + 8, 128, rulerStartX + 24, 128,
//		// pixPaint);
//		// canvas.drawLine(rulerStartX + 8, 128, rulerStartX + 8, 192,
//		// pixPaint);
//		// canvas.drawLine(rulerStartX + 24, 128, rulerStartX + 24, 192,
//		// pixPaint);
//
//		// 文本�?25mm/s 10.0mm/100mv
//		// canvas.drawText(ruleStr, rulerStartX - 120, 40, pixPaint);
//		// canvas.drawText("25mm/s", rulerStartX - 200, 40, pixPaint);
//
//		canvas.drawPath(mPath, mPaint);
//	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
            SCREEN_WIDTH = MeasureSpec.getSize(widthMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //
    public void makeFollowPathOne(Canvas canvas) {

        float height = getHeight();
        int size = ecgValues.size();
        if (size == 0) return;
        mPath.reset();

        float width = SCREEN_WIDTH / maxSize;
        float heightI = height / 16000;
        mPath.moveTo(0, (8000 - ecgValues.get(0)) * heightI);
        for (int i = 1; i < size - 1; i++) {
            canvas.drawPoint(i * width, (8000 - ecgValues.get(i)) * heightI,mPaint);
          //  mPath.lineTo(i * width, (8000 - ecgValues.get(i)) * heightI);
        }


    }

    List<Integer> ecgValues = new ArrayList<>();
    float maxSize = 1516;

    public void setValue(int value) {
        if (ecgValues.size() > maxSize) ecgValues.remove(0);
        ecgValues.add(value);
    }

    public void clearData() {
        ecgValues.clear();
        ;

    }

    public void setYDistance(float distance) {
        distanceY = distance;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;

    }

    @Override
    public void run() {
        while (isDrawing) {
            drawing();

        }
    }

    private static final String TAG = "EcgGraphicView";

    private void drawing() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas == null) return;
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            makeFollowPathOne(mCanvas);
          //  mCanvas.drawPath(mPath, mPaint);

        } finally {
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
