package org.swtchart;

/**
 * Series.
 */
public interface ISeries {

	/**
	 * A Series type.
	 */
	public enum SeriesType {

		/** the line */
		LINE("Line"),

		/** the bar */
		BAR("Bar");

		/** the label for series type */
		public final String label;

		/**
		 * Constructor.
		 * 
		 * @param label
		 *            the label for series type
		 */
		private SeriesType(String label) {
			this.label = label;
		}
	}

	/**
	 * Gets the series id.
	 * 
	 * @return the series id
	 */
	String getId();

	/**
	 * Sets the visibility state.
	 * 
	 * @param visible
	 *            the visibility state
	 */
	void setVisible(boolean visible);

	/**
	 * Gets the visibility state.
	 * 
	 * @return true if series is visible
	 */
	boolean isVisible();

	/**
	 * Gets the series type.
	 * 
	 * @return the series type
	 */
	SeriesType getType();

	/**
	 * Enables the stack series. The associated axis has to be category type. In
	 * addition, the series has to contain only positive values.
	 * 
	 * @param enabled
	 *            true if enabling stack series
	 * @throws IllegalArgumentException
	 *             if the associated axis is not valid category type, or if
	 *             series contains negative values.
	 */
	void enableStack(boolean enabled);

	/**
	 * Gets the state indicating if stack is enabled.
	 * 
	 * @return the state indicating if stack is enabled
	 */
	boolean isStackEnabled();

	/**
	 * Sets the X series.
	 * 
	 * @param series
	 *            the X series
	 */
	void setXSeries(double[] series);

	/**
	 * Sets the Y series.
	 * 
	 * @param series
	 *            the Y series
	 */
	void setYSeries(double[] series);

	/**
	 * Gets the X series. If the X series haven't been set yet,
     * <tt>null</tt> will be returned.
	 * 
	 * @return the X series
	 * 
	 */
	double[] getXSeries();

	/**
	 * Gets the Y series. If the Y series haven't been set yet,
     * <tt>null</tt> will be returned.
	 * 
	 * @return the Y series
	 * 
	 */
	double[] getYSeries();

	/**
	 * Gets the X axis id.
	 * 
	 * @return the X axis id
	 */
	int getXAxisId();

	/**
	 * Sets the X axis id.
	 * 
	 * @param id
	 *            the X axis id.
	 */
	void setXAxisId(int id);

	/**
	 * Gets the Y axis id.
	 * 
	 * @return the Y axis id
	 */
	int getYAxisId();

	/**
	 * Sets the Y axis id.
	 * 
	 * @param id
	 *            the Y axis id.
	 */
	void setYAxisId(int id);

	/**
	 * Gets the label.
	 * 
	 * @return the label
	 */
	ISeriesLabel getLabel();
}