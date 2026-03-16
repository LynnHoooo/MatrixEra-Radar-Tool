package lecho.lib.hellocharts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PathEffect;
import lecho.lib.hellocharts.util.Utils;
import lecho.lib.hellocharts.view.Chart;

/**
 * Single line for line chart.
 * 
 */
public class Line {
	private static final int DEFAULT_LINE_STROKE_WIDTH_DP = 3;
	private static final int DEFAULT_POINT_RADIUS_DP = 6;
	private static final int DEFAULT_AREA_TRANSPARENCY = 64;
	private int color = Utils.DEFAULT_COLOR;
	private int darkenColor = Utils.DEFAULT_DARKEN_COLOR;
	/** Transparency of area when line is filled. **/
	private int areaTransparency = DEFAULT_AREA_TRANSPARENCY;
	private int strokeWidth = DEFAULT_LINE_STROKE_WIDTH_DP;
	private int pointRadius = DEFAULT_POINT_RADIUS_DP;
	private boolean hasPoints = true;
	private boolean hasLines = true;
	private boolean hasLabels = false;
	
	private boolean hasLabelsOnlyForSelected = false;
	private boolean isCubic = false;
	private boolean isFilled = false;
	private ValueShape shape = ValueShape.CIRCLE;
	private ArrayList<Bitmap> bitmap;
	private PathEffect pathEffect;
	private ValueFormatter formatter = new SimpleValueFormatter();
	private List<PointValue> values = new ArrayList<PointValue>();
	private Bitmap labelBitmap;
 	Bitmap maxBitmap,minBitmap;
 	private Bitmap goBedBitmap;
 	private Bitmap upBedBitmap;

	public List<Integer> getGoBedList() {
		return goBedList;
	}

	public void setGoBedList(List<Integer> goBedList) {
		this.goBedList = goBedList;
	}

	public List<Integer> getUpBedList() {
		return upBedList;
	}

	public void setUpBedList(List<Integer> upBedList) {
		this.upBedList = upBedList;
	}

	private List<Integer> goBedList=new ArrayList<>();
	private List<Integer> upBedList=new ArrayList<>();
	boolean isGradient;
	int maxOffset;

	public int getMaxOffset() {
		return maxOffset;
	}

	public void setMaxOffset(int maxOffset) {
		this.maxOffset = maxOffset;
	}

	public List<RangeColor> getRangeColors() {
		return rangeColors;
	}

	public void setRangeColors(List<RangeColor> rangeColors) {
		this.rangeColors = rangeColors;
	}

	List<RangeColor>rangeColors;
	int[]gradientColor= new int[]{Color.parseColor("#3dbfee"), Color.parseColor("#516dcd"), Color.parseColor("#453c83")};
	float[]positions= new float[]{0f, 0.7f, 1f};

	public float[] getPositions() {
		return positions;
	}

	public void setPositions(float[] positions) {
		this.positions = positions;
	}

	public int[] getGradientColor() {
		return gradientColor;
	}

	public void setGradientColor(int[] gradientColor) {
		this.gradientColor = gradientColor;
	}


	public Bitmap getGoBedBitmap() {
		return goBedBitmap;
	}

	public void setGoBedBitmap(Bitmap goBedBitmap) {
		this.goBedBitmap = goBedBitmap;
	}

	public Bitmap getUpBedBitmap() {
		return upBedBitmap;
	}

	public void setUpBedBitmap(Bitmap upBedBitmap) {
		this.upBedBitmap = upBedBitmap;
	}



	public int getPointColor() {
		return pointColor;
	}

	public Line setPointColor(int pointColor) {
		this.pointColor = pointColor;
		return this;
	}

	private int pointColor=0;

	public boolean isGradient() {
		return isGradient;
	}

	public Line setGradient(boolean gradient) {
		isGradient = gradient;
		return  this;
	}

	public float getRangeValue() {
		return rangeValue;
	}

	public void setRangeValue(float rangeValue) {
		this.rangeValue = rangeValue;
	}

	float rangeValue;
	boolean onlyDrawMaxMin;

	public boolean isOnlyDrawMaxMin() {
		return onlyDrawMaxMin;
	}

	public Line setOnlyDrawMaxMin(boolean onlyDrawMaxMin) {

		this.onlyDrawMaxMin = onlyDrawMaxMin;
		return  this;
	}

	public Bitmap getMaxBitmap() {
		return maxBitmap;
	}

	public void setMaxBitmap(Bitmap maxBitmap) {
		this.maxBitmap = maxBitmap;
	}

	public Bitmap getMinBitmap() {
		return minBitmap;
	}

	public void setMinBitmap(Bitmap minBitmap) {
		this.minBitmap = minBitmap;
	}

	private boolean isDefaultLine = true;

	public boolean isDefauleLine() {
		return this.isDefaultLine;
	}

	public void setDefalutLine(boolean isDefalut) {
		this.isDefaultLine = isDefalut;
	}

	public Line() {

	}

	public Line(List<PointValue> values) {
		setValues(values);
	}

	public Line(Line line) {
		this.color = line.color;
		this.darkenColor = line.color;
		this.areaTransparency = line.areaTransparency;
		this.strokeWidth = line.strokeWidth;
		this.pointRadius = line.pointRadius;
		this.hasPoints = line.hasPoints;
		this.hasLines = line.hasLines;
		this.hasLabels = line.hasLabels;
		this.hasLabelsOnlyForSelected = line.hasLabelsOnlyForSelected;
		this.isCubic = line.isCubic;
		this.isFilled = line.isFilled;
		this.shape = line.shape;
		this.pathEffect = line.pathEffect;
		this.formatter = line.formatter;

		for (PointValue pointValue : line.values) {
			this.values.add(new PointValue(pointValue));
		}
	}

	public void update(float scale) {
		for (PointValue value : values) {
			value.update(scale);
		}
	}

	public void finish() {
		for (PointValue value : values) {
			value.finish();
		}
	}

	public Line setValues(List<PointValue> values) {
		if (null == values) {
			this.values = Collections.emptyList();
		} else {
			this.values = values;
		}
		return this;
	}

	public List<PointValue> getValues() {
		return this.values;
	}

	public int getColor() {
		return color;
	}

	public Line setColor(int color) {
		this.color = color;
		this.darkenColor = Utils.darkenColor(color);
		return this;
	}

	public int getDarkenColor() {
		return darkenColor;
	}

	/**
	 * @see #setAreaTransparency(int)
	 */
	public int getAreaTransparency() {
		return areaTransparency;
	}

	/**
	 * Set area transparency(255 is full opacity) for filled lines
	 * 
	 * @param areaTransparency
	 * @return
	 */
	public Line setAreaTransparency(int areaTransparency) {
		this.areaTransparency = areaTransparency;
		return this;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public Line setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
		return this;
	}

	public boolean hasPoints() {
		return hasPoints;
	}

	public Line setHasPoints(boolean hasPoints) {
		this.hasPoints = hasPoints;
		return this;
	}

	public boolean hasLines() {
		return hasLines;
	}

	public Line setHasLines(boolean hasLines) {
		this.hasLines = hasLines;
		return this;
	}

	public boolean hasLabels() {
		return hasLabels;
	}

	public Line setHasLabels(boolean hasLabels) {
		this.hasLabels = hasLabels;
		if (hasLabels) {
			this.hasLabelsOnlyForSelected = false;
		}
		return this;
	}

	/**
	 * @see #setHasLabelsOnlyForSelected(boolean)
	 */
	public boolean hasLabelsOnlyForSelected() {
		return hasLabelsOnlyForSelected;
	}

	/**
	 * Set true if you want to show value labels only for selected value, works
	 * best when chart has isValueSelectionEnabled set to true
	 * {@link Chart#setValueSelectionEnabled(boolean)}.
	 */
	public Line setHasLabelsOnlyForSelected(boolean hasLabelsOnlyForSelected) {
		this.hasLabelsOnlyForSelected = hasLabelsOnlyForSelected;
		if (hasLabelsOnlyForSelected) {
			this.hasLabels = false;
		}
		return this;
	}

	public int getPointRadius() {
		return pointRadius;
	}

	/**
	 * Set radius for points for this line.
	 * 
	 * @param pointRadius
	 * @return
	 */
	public Line setPointRadius(int pointRadius) {
		this.pointRadius = pointRadius;
		return this;
	}

	public boolean isCubic() {
		return isCubic;
	}

	public Line setCubic(boolean isCubic) {
		this.isCubic = isCubic;
		return this;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public Line setFilled(boolean isFilled) {
		this.isFilled = isFilled;
		return this;
	}

	/**
	 * @see #setShape(ValueShape)
	 */
	public ValueShape getShape() {
		return shape;
	}

	public void setHeartRange(float[] range) {
		HEART_JINGTAI = range[0];
		HEART_RESHEN = range[1];
		HEART_RANZHI = range[2];
		HEART_YOUYANG = range[3];
		HEART_WUYANG = range[4];
		HEART_JILIE = range[5];
	}

	public Bitmap getHeartBitmap(float x) {
		int index = 0;
		if (x == HEART_JINGTAI) {
			index = 0;
			return bitmap.get(index);
		}
		if (x <= HEART_RESHEN) {
			index = 1;
		} else if (x > HEART_RESHEN && x <= HEART_RANZHI) {
			index = 2;
		} else if (x > HEART_RANZHI && x <= HEART_YOUYANG) {
			index = 3;
		} else if (x > HEART_YOUYANG && x <= HEART_WUYANG) {
			index = 4;
		} else if (x > HEART_WUYANG) {
			index = 5;
		}
		return bitmap.get(index);
	}

	public Bitmap getHeartBitmapsTATE(int  state) {
	
		return bitmap.get(state);
	}
	private Bitmap jstyleBitmap;

	public Bitmap getJstyleBitMap() {
		return this.jstyleBitmap;
	}

	public void setJstyleBitMap(Bitmap bitmap) {
		this.jstyleBitmap = bitmap;
	}

	private float HEART_JINGTAI;
	private float HEART_RESHEN;
	private float HEART_RANZHI;
	private float HEART_YOUYANG;
	private float HEART_WUYANG;
	private float HEART_JILIE;

	private int laberColor;
	private boolean hasLaberColor;

	public void setLaberTextColor(int color) {
		hasLaberColor = true;
		this.laberColor = color;
	}

	public boolean getHasLaberColor() {
		return hasLaberColor;
	}

	public int getLaberTextColor() {
		return laberColor;
	}

	/**
	 * Set shape for points, possible values: SQUARE, CIRCLE
	 * 
	 * @param shape
	 * @return
	 */
	public Line setShape(ValueShape shape) {
		this.shape = shape;
		return this;
	}

	public Line setShape(ValueShape shape, ArrayList<Bitmap> map) {
		this.shape = shape;
		this.bitmap = map;

		return this;
	}

	public PathEffect getPathEffect() {
		return pathEffect;
	}

	/**
	 * Set path effect for this line, note: it will slow down drawing, try to
	 * not use complicated effects, DashPathEffect should be safe choice.
	 * 
	 * @param pathEffect
	 */
	public void setPathEffect(PathEffect pathEffect) {
		this.pathEffect = pathEffect;
	}

	public ValueFormatter getFormatter() {
		return formatter;
	}

	public Line setFormatter(ValueFormatter formatter) {
		if (null == formatter) {
			this.formatter = new SimpleValueFormatter();
		} else {
			this.formatter = formatter;
		}
		return this;
	}

	// 7.26历史数据运动和睡眠laber背景
	public Line setLaberBitmap(Bitmap bitmap) {
		this.labelBitmap = bitmap;
		return this;
	}

	public Bitmap getLaberBitmap() {
		return this.labelBitmap;
	}
	
	

}
