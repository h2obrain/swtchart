package org.swtchart.internal.axis;

import org.swtchart.Chart;
import org.swtchart.Constants;
import org.swtchart.IAxis.Direction;
import org.swtchart.internal.Title;

/**
 * An Axis title.
 */
public class AxisTitle extends Title {

	/** the axis */
	private Axis axis;

	/** the default text for X Axis */
	private static final String DEFAULT_TEXT_FOR_XAXIS = "X Axis";

	/** the default text for X Axis */
	private static final String DEFAULT_TEXT_FOR_YAXIS = "Y Axis";

	/** the direction of axis */
	private Direction direction;

	/**
	 * Constructor.
	 * 
	 * @param chart
	 *            the chart
	 * @param style
	 *            the style
	 * @param axis
	 *            the axis
	 * @param direction
	 *            the direction
	 */
	public AxisTitle(Chart chart, int style, Axis axis, Direction direction) {
		super(chart, style);
		this.axis = axis;
		this.direction = direction;
		setFont(Constants.MEDIUM_FONT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.Title#getDefaultText()
	 */
	@Override
	protected String getDefaultText() {
		if (direction == Direction.X) {
			return DEFAULT_TEXT_FOR_XAXIS;
		}
		return DEFAULT_TEXT_FOR_YAXIS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.swtchart.internal.Title#isHorizontal()
	 */
	@Override
	protected boolean isHorizontal() {
		return axis.isHorizontalAxis();
	}
}
