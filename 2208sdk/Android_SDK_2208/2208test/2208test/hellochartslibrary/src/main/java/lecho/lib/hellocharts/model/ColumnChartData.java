package lecho.lib.hellocharts.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for column chart.
 * 
 */
public class ColumnChartData extends AbstractChartData {
	public static final float DEFAULT_FILL_RATIO = 0.75f;
	public static final float DEFAULT_BASE_VALUE = 0.0f;
	private List<Column> columns = new ArrayList<Column>();
	private boolean isStacked = false;

	private float fillRatio = DEFAULT_FILL_RATIO;
	private float baseValue = DEFAULT_BASE_VALUE;
	private int bgChartColor;
	private float maxValue;
	private  int cicleSquare;
	private  int type=0;//当前的类型
	private  int Index=0;//当前的类型

    public int getIndex() {
        return Index;
    }

    public void setIndex(int index) {
        Index = index;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	Bitmap highBitmap;
	Bitmap lowBitmap;
	Bitmap maxStep;

	public Bitmap getMaxStepBitmap() {
		return maxStep;
	}

	public void setMaxStepBitmap(Bitmap maxStep) {
		this.maxStep = maxStep;
	}

	private int maxIndex=-1;

	public int getMaxIndex() {
		return maxIndex;
	}

	public void setMaxIndex(int maxIndex) {
		this.maxIndex = maxIndex;
	}

	public Bitmap getHighBitmap() {
		return highBitmap;
	}

	public void setHighBitmap(Bitmap highBitmap) {
		this.highBitmap = highBitmap;
	}

	public Bitmap getLowBitmap() {
		return lowBitmap;
	}

	public void setLowBitmap(Bitmap lowBitmap) {
		this.lowBitmap = lowBitmap;
	}

	List<BpValue>bpValues;

	public List<BpValue> getBpValues() {
		return bpValues;
	}

	public void setBpValues(List<BpValue> bpValues) {
		this.bpValues = bpValues;
	}
	public int getCicleSquare() {
		return cicleSquare;
	}

	public void setCicleSquare(int cicleSquare) {
		this.cicleSquare = cicleSquare;
	}

	public int getBgChartColor() {
		return bgChartColor;
	}
	public float getMaxValue() {
		return maxValue;
	}

	public void setBgChartColor(int bgChartColor,float maxValue) {
		this.bgChartColor = bgChartColor;
		this.maxValue=maxValue;
	}

	public ColumnChartData() {
	}

	public ColumnChartData(List<Column> columns) {
		setColumns(columns);
	}

	/**
	 * Copy constructor for deep copy.
	 */
	public ColumnChartData(ColumnChartData data) {
		super(data);
		this.isStacked = data.isStacked;
		this.fillRatio = data.fillRatio;

		for (Column column : data.columns) {
			this.columns.add(new Column(column));
		}
	}

	@Override
	public void update(float scale) {
		for (Column column : columns) {
			column.update(scale);
		}

	}

	@Override
	public void finish() {
		for (Column column : columns) {
			column.finish();
		}
	}



	public List<Column> getColumns() {
		return columns;
	}

	public ColumnChartData setColumns(List<Column> columns) {
		if (null == columns) {
			this.columns = new ArrayList<Column>();
		} else {
			this.columns = columns;
		}
		return this;
	}

	public boolean isStacked() {
		return isStacked;
	}

	/**
	 * Set true if you want stacked column chart.
	 * 
	 * @param isStacked
	 * @return
	 */
	public ColumnChartData setStacked(boolean isStacked) {
		this.isStacked = isStacked;
		return this;
	}

	public float getFillRatio() {
		return fillRatio;
	}

	/**
	 * Set fill ration for columns, value from 0 to 1, 1 means that there will be almost no free space between columns,
	 * 0 means that columns will have minimum width(2px).
	 * 
	 * @param fillRatio
	 * @return
	 */
	public ColumnChartData setFillRatio(float fillRatio) {
		if (fillRatio < 0) {
			fillRatio = 0;
		}
		if (fillRatio > 1) {
			fillRatio = 1;
		}
		this.fillRatio = fillRatio;
		return this;
	}

	/**
	 * @see #setBaseValue(float)
	 */
	public float getBaseValue() {
		return baseValue;
	}

	/**
	 * Set value below which values will be drawn as negative, by default 0.
	 */
	public ColumnChartData setBaseValue(float baseValue) {
		this.baseValue = baseValue;
		return this;
	}

	public static ColumnChartData generateDummyData() {
		final int numColumns = 4;
		ColumnChartData data = new ColumnChartData();
		List<Column> columns = new ArrayList<Column>(numColumns);
		List<ColumnValue> values;
		Column column;
		for (int i = 1; i <= numColumns; ++i) {
			values = new ArrayList<ColumnValue>(numColumns);
			values.add(new ColumnValue(0));
			column = new Column(values);
			columns.add(column);
		}

		data.setColumns(columns);
		return data;
	}

}
