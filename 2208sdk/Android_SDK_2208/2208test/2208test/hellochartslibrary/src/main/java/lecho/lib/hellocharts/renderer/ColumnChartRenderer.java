package lecho.lib.hellocharts.renderer;

import lecho.lib.hellocharts.ChartComputator;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.BpValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ColumnValue;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.provider.ColumnChartDataProvider;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.ColumnChartView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import java.util.List;

/**
 * Magic renderer for ColumnChart.
 */
public class ColumnChartRenderer extends AbstractChartRenderer {
    public static final int DEFAULT_SUBCOLUMN_SPACING_DP = 1;
    public static final int DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP = 4;

    private static final int MODE_DRAW = 0;
    private static final int MODE_CHECK_TOUCH = 1;
    private static final int MODE_HIGHLIGHT = 2;
    private final PorterDuffXfermode porterDuffXfermode;

    protected ColumnChartDataProvider dataProvider;

    /**
     * Additional width for hightlighted column, used to give tauch feedback.
     */
    private int touchAdditionalWidth;

    /**
     * Spacing between sub-columns.
     */
    private int subcolumnSpacing;

    /**
     * Paint used to draw every column.
     */
    private Paint columnPaint = new Paint();

    /**
     * Holds coordinates for currently processed column/sub-column.
     */
    private RectF drawRect = new RectF();
    private RectF drawRectmian = new RectF();

    /**
     * Coordinated of user tauch.
     */
    private PointF touchedPoint = new PointF();
    /**
     * Used to pass tauched value to tauch listener.
     */
    private float[] valuesBuff = new float[1];
    private float fillRatio;
    private float baseValue;

    public ColumnChartRenderer(Context context, Chart chart, ColumnChartDataProvider dataProvider) {
        super(context, chart);
        this.dataProvider = dataProvider;
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST);
        subcolumnSpacing = Utils.dp2px(density, DEFAULT_SUBCOLUMN_SPACING_DP);
        touchAdditionalWidth = Utils.dp2px(density, DEFAULT_COLUMN_TOUCH_ADDITIONAL_WIDTH_DP);
        columnPaint.setAntiAlias(true);
        columnPaint.setStyle(Paint.Style.FILL);
        columnPaint.setStrokeCap(Cap.ROUND);

    }

    @Override
    public void initMaxViewport() {
     /*   if (isViewportCalculationEnabled) {
            calculateMaxViewport();
            chart.getChartComputator().setMaxViewport(tempMaxViewport);
        }*/
    }

    @Override
    public void initDataMeasuremetns() {
        chart.getChartComputator().setInternalMargin(labelMargin);// Using label margin because I'm lazy:P
    }

    @Override
    public void initDataAttributes() {
        super.initDataAttributes();
        ColumnChartData data = dataProvider.getColumnChartData();
        fillRatio = data.getFillRatio();
        baseValue = data.getBaseValue();
    }
    @Override
    public void drawUnclipped(Canvas canvas) {

    }


    public void draw(Canvas canvas) {
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        int columnIndex = 0;
        List<BpValue> bpValues = data.getBpValues();
        if (bpValues != null) {
            columnPaint.setColor(Color.parseColor("#66013499"));
            for (BpValue bpValue : bpValues) {
                processColumnForSubcolumns(canvas, columnWidth, columnIndex, MODE_DRAW, bpValue);
                ++columnIndex;
            }
        }
    }

    public boolean checkTouch(float touchX, float touchY) {
        Log.e("adassssssssss",touchX+"&&&&"+touchY);
        drawRectmian = new RectF();
        touchedPoint.x = touchX;
        touchedPoint.y = touchY;
        final ColumnChartData data = dataProvider.getColumnChartData();
        final float columnWidth = calculateColumnWidth();
        int columnIndex = 0;
        List<BpValue> list = data.getBpValues();
        if (list != null) {
            for (BpValue bpValue : list) {
                processColumnForSubcolumns(null, columnWidth, columnIndex, MODE_CHECK_TOUCH, bpValue);
                ++columnIndex;
            }
        }
        return isTouched();
    }










    private void processColumnForSubcolumns(Canvas canvas, float columnWidth, int columnIndex, int mode, BpValue columnValue) {
        Log.e("sdasda","************"+mode);

        final ChartComputator computator = chart.getChartComputator();
        int size = 1;
        float subcolumnWidth = (columnWidth - (subcolumnSpacing * (size - 1))) / size;
        if (subcolumnWidth < 1) {
            subcolumnWidth = 1;
        }
        // Columns are indexes from 0 to n, column index is also column X value
        final float rawX = computator.computeRawX(columnIndex);
        final float halfColumnWidth = columnWidth / 2;
        float subcolumnRawX = rawX - halfColumnWidth;
        switch (dataProvider.getColumnChartData().getType()) {
            case 0://心率绘图
                float rawBgTop = computator.computeRawY(200);
                float rawBgBottom = computator.computeRawY(0);
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.bottom = rawBgBottom;
                drawRectmian.top = rawBgTop;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                float rawBgToper = computator.computeRawY(100);
                float rawBgBottomer = computator.computeRawY(0);
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.bottom = rawBgBottomer;
                drawRectmian.top = rawBgToper;
                break;

        }
        switch (mode) {
            case MODE_DRAW:
                drawBpColumn(columnIndex, computator, columnValue, canvas, subcolumnRawX, subcolumnWidth);
                break;
            case MODE_CHECK_TOUCH:
                checkBpToDraw(columnIndex);
                break;
        }

    }


    private void checkBpToDraw(int columnIndex) {
        switch (dataProvider.getColumnChartData().getType()) {
            case 0://心率绘图
                if (drawRectmian.contains(touchedPoint.x,touchedPoint.y)) {
                    if (null != ColumnChartView.clickListener) {
                        ColumnChartView.clickListener.onItemClick(columnIndex, (int) (drawRectmian.left + drawRectmian.right) / 2);
                    }
                }
                break;
            case 1:
                if (drawRectmian.contains(touchedPoint.x,touchedPoint.y)) {//touchedPoint.x, touchedPoint.y
                    if (null != ColumnChartView.JSclickListener) {
                        ColumnChartView.JSclickListener.onItemClick(columnIndex, (int) (drawRectmian.left + drawRectmian.right) / 2);
                    }
                }
                break;
            case 2:
                if (drawRectmian.contains(touchedPoint.x,touchedPoint.y)) {//touchedPoint.x, touchedPoint.y
                    if (null != ColumnChartView.PlclickListener) {
                        ColumnChartView.PlclickListener.onItemClick(columnIndex, (int) (drawRectmian.left + drawRectmian.right) / 2);
                    }
                }
                break;
            case 3:
                if (drawRectmian.contains(touchedPoint.x,touchedPoint.y)) {//touchedPoint.x, touchedPoint.y
                    if (null != ColumnChartView.XlclickListener) {
                        ColumnChartView.XlclickListener.onItemClick(columnIndex, (int) (drawRectmian.left + drawRectmian.right) / 2);
                    }
                }
                break;
            case 4:
                if (drawRectmian.contains(touchedPoint.x,touchedPoint.y)) {//touchedPoint.x, touchedPoint.y
                    if (null != ColumnChartView.BYclickListener) {
                        ColumnChartView.BYclickListener.onItemClick(columnIndex, (int) (drawRectmian.left + drawRectmian.right) / 2);
                    }
                }
                break;
        }


    }

    private void drawBpColumn(int columnIndex, ChartComputator computator, BpValue columnValue, Canvas canvas, float subcolumnRawX, float subcolumnWidth) {
        switch (dataProvider.getColumnChartData().getType()) {
            case 0://心率绘图
                final float rawBgTop = computator.computeRawY(200);
                final float rawBgBottom = computator.computeRawY(0);
                columnPaint.setColor(Color.parseColor("#F6F6F6"));
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.top = rawBgTop;
                drawRectmian.bottom = rawBgBottom;
                canvas.drawRect(drawRectmian, columnPaint);
                if (columnValue.getHighValue() != 0 || columnValue.getLowValue() != 0) {//高低值的矩形
                    final float rawHighY = computator.computeRawY(columnValue.getHighValue());
                    final float rawHighYBottom = computator.computeRawY(columnValue.getLowValue());
                    columnPaint.setColor(columnValue.getHighValueColor());
                    drawRect.left = subcolumnRawX;
                    drawRect.right = subcolumnRawX + subcolumnWidth;
                    drawRect.top = rawHighY;
                    drawRect.bottom = rawHighYBottom;
                    canvas.drawRoundRect(drawRect, subcolumnWidth, subcolumnWidth, columnPaint);
                }
                break;
            case 1://yali
                final float rawBgToper = computator.computeRawY(100);
                final float rawBgBottoer = computator.computeRawY(0);
                columnPaint.setColor(Color.parseColor("#F6F6F6"));
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.top = rawBgToper;
                drawRectmian.bottom = rawBgBottoer;
                canvas.drawRect(drawRectmian, columnPaint);
                if (columnValue.getMaxjs() != 0 || columnValue.getMinjs() != 0) {//高低值的矩形
                    final float rawHighY = computator.computeRawY(columnValue.getMaxjs());
                    final float rawHighYBottom = computator.computeRawY(columnValue.getMinjs());
                    float rawHighYBottomer;
                    if (rawHighY == rawHighYBottom) {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMaxjs() - 6);
                    } else {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMinjs());
                    }
                    columnPaint.setColor(columnValue.getHighValueColor());
                    drawRect.left = subcolumnRawX;
                    drawRect.right = subcolumnRawX + subcolumnWidth;
                    drawRect.top = rawHighY;
                    drawRect.bottom = rawHighYBottomer;
                    canvas.drawRoundRect(drawRect, subcolumnWidth, subcolumnWidth, columnPaint);
                }
                break;
            case 2://yali
                final float rawBgTopPL = computator.computeRawY(100);
                final float rawBgBottoPL = computator.computeRawY(0);
                columnPaint.setColor(Color.parseColor("#F6F6F6"));
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.top = rawBgTopPL;
                drawRectmian.bottom = rawBgBottoPL;
                canvas.drawRect(drawRectmian, columnPaint);
                if (columnValue.getMaxpl() != 0 || columnValue.getMinpl() != 0) {//高低值的矩形
                    final float rawHighY = computator.computeRawY(columnValue.getMaxpl());
                    final float rawHighYBottom = computator.computeRawY(columnValue.getMinpl());
                    float rawHighYBottomer;
                    if (rawHighY == rawHighYBottom) {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMaxpl() - 6);
                    } else {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMinpl());
                    }
                    columnPaint.setColor(columnValue.getHighValueColor());
                    drawRect.left = subcolumnRawX;
                    drawRect.right = subcolumnRawX + subcolumnWidth;
                    drawRect.top = rawHighY;
                    drawRect.bottom = rawHighYBottomer;
                    canvas.drawRoundRect(drawRect, subcolumnWidth, subcolumnWidth, columnPaint);
                }
                break;

            case 3:
                final float rawBgTopdrawtXL = computator.computeRawY(100);
                final float rawBgBottotXL = computator.computeRawY(0);
                columnPaint.setColor(Color.parseColor("#F6F6F6"));
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.top = rawBgTopdrawtXL;
                drawRectmian.bottom = rawBgBottotXL;
                canvas.drawRect(drawRectmian, columnPaint);
                if (columnValue.getMaxxl() != 0 || columnValue.getMinxl() != 0) {//高低值的矩形
                    final float rawHighY = computator.computeRawY(columnValue.getMaxxl());
                    final float rawHighYBottom = computator.computeRawY(columnValue.getMinxl());
                    float rawHighYBottomer;
                    if (rawHighY == rawHighYBottom) {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMaxxl() - 6);
                    } else {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMinxl());
                    }

                    int low=Color.parseColor("#C0DBFF");
                    int avg=Color.parseColor("#3CD154");
                    int max=Color.parseColor("#FFD800");
                    if(columnValue.getMaxxl()<=25){
                        columnPaint.setColor(low);
                    }else if(columnValue.getMaxxl()>25&&columnValue.getMaxxl()<=75){
                        columnPaint.setColor(avg);
                    }else{
                        columnPaint.setColor(max);
                    }

                    drawRect.left = subcolumnRawX;
                    drawRect.right = subcolumnRawX + subcolumnWidth;
                    drawRect.top = rawHighY;
                    drawRect.bottom = rawHighYBottomer;
                    canvas.drawRoundRect(drawRect, subcolumnWidth, subcolumnWidth, columnPaint);

                }
                break;


            case 4://yali
                final float rawBgTopby = computator.computeRawY(100);
                final float rawBgBottoby = computator.computeRawY(0);
                columnPaint.setColor(Color.parseColor("#F6F6F6"));
                drawRectmian.left = subcolumnRawX;
                drawRectmian.right = subcolumnRawX + subcolumnWidth;
                drawRectmian.top = rawBgTopby;
                drawRectmian.bottom = rawBgBottoby;
                canvas.drawRect(drawRectmian, columnPaint);
                if (columnValue.getMaxby() != 0 || columnValue.getMinby() != 0) {//高低值的矩形
                    final float rawHighY = computator.computeRawY(columnValue.getMaxby());
                    final float rawHighYBottom = computator.computeRawY(columnValue.getMinby());
                    float rawHighYBottomer;
                    if (rawHighY == rawHighYBottom) {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMaxby() - 6);
                    } else {
                        rawHighYBottomer = computator.computeRawY(columnValue.getMinby());
                    }
                    columnPaint.setColor(columnValue.getHighValueColor());
                    drawRect.left = subcolumnRawX;
                    drawRect.right = subcolumnRawX + subcolumnWidth;
                    drawRect.top = rawHighY;
                    drawRect.bottom = rawHighYBottomer;
                    canvas.drawRoundRect(drawRect, subcolumnWidth, subcolumnWidth, columnPaint);
                }
                break;
        }
    }




    protected float calculateColumnWidth() {
        final ChartComputator computator = chart.getChartComputator();
        float columnWidth = fillRatio * computator.getContentRect().width() / computator.getVisibleViewport().width();
        if (columnWidth < 2) {
            columnWidth = 2;
        }
        return columnWidth * 2;
    }


}
