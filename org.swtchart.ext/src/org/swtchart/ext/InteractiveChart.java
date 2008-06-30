package org.swtchart.ext;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.Range;
import org.swtchart.IAxis.Direction;
import org.swtchart.ext.internal.SelectionRectangle;
import org.swtchart.ext.internal.properties.AxisPage;
import org.swtchart.ext.internal.properties.AxisTickPage;
import org.swtchart.ext.internal.properties.ChartPage;
import org.swtchart.ext.internal.properties.GridPage;
import org.swtchart.ext.internal.properties.LegendPage;
import org.swtchart.ext.internal.properties.SeriesLabelPage;
import org.swtchart.ext.internal.properties.SeriesPage;

/**
 * An interactive chart which provides the following abilities.
 * <ul>
 * <li>scroll with arrow keys</li>
 * <li>zoom in and out with ctrl + arrow up/down keys</li>
 * <li>context menus for auto-scaling and zooming in/out.</li>
 * <li>properties dialog to configure the chart settings</li>
 * </ul>
 */
public class InteractiveChart extends Chart implements PaintListener {

	/** the selection rectangle for zoom in/out */
	protected SelectionRectangle selection;

	/** the clicked time in milliseconds */
	private long clickedTime;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the style
	 */
	public InteractiveChart(Composite parent, int style) {
		super(parent, style);
		init();
	}

	/**
	 * Initializes.
	 */
	private void init() {

		selection = new SelectionRectangle();

		Composite plot = getPlotArea();
		plot.addListener(SWT.Resize, this);
		plot.addListener(SWT.MouseDown, this);
		plot.addListener(SWT.MouseMove, this);
		plot.addListener(SWT.MouseUp, this);
		plot.addListener(SWT.KeyDown, this);

		plot.addPaintListener(this);

		createMenuItems();
	}

	/**
	 * Creates menu items.
	 */
	private void createMenuItems() {
		Menu menu = new Menu(getPlotArea());
		getPlotArea().setMenu(menu);

		// autoscale menu group
		MenuItem menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(Messages.AUTOSCALE_GROUP);
		Menu autoScaleMenu = new Menu(menuItem);
		menuItem.setMenu(autoScaleMenu);

		// autoscale both axes
		menuItem = new MenuItem(autoScaleMenu, SWT.PUSH);
		menuItem.setText(Messages.AUTOSCALE);
		menuItem.addListener(SWT.Selection, this);

		// autoscale X axis
		menuItem = new MenuItem(autoScaleMenu, SWT.PUSH);
		menuItem.setText(Messages.AUTOSCALE_X);
		menuItem.addListener(SWT.Selection, this);

		// autoscale Y axis
		menuItem = new MenuItem(autoScaleMenu, SWT.PUSH);
		menuItem.setText(Messages.AUTOSCALE_Y);
		menuItem.addListener(SWT.Selection, this);

		menuItem = new MenuItem(menu, SWT.SEPARATOR);

		// zoom in menu group
		menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(Messages.ZOOMIN_GROUP);
		Menu zoomInMenu = new Menu(menuItem);
		menuItem.setMenu(zoomInMenu);

		// zoom in both axes
		menuItem = new MenuItem(zoomInMenu, SWT.PUSH);
		menuItem.setText(Messages.ZOOMIN);
		menuItem.addListener(SWT.Selection, this);

		// zoom in X axis
		menuItem = new MenuItem(zoomInMenu, SWT.PUSH);
		menuItem.setText(Messages.ZOOMIN_X);
		menuItem.addListener(SWT.Selection, this);

		// zoom in Y axis
		menuItem = new MenuItem(zoomInMenu, SWT.PUSH);
		menuItem.setText(Messages.ZOOMIN_Y);
		menuItem.addListener(SWT.Selection, this);

		// zoom out menu group
		menuItem = new MenuItem(menu, SWT.CASCADE);
		menuItem.setText(Messages.ZOOMOUT_GROUP);
		Menu zoomOutMenu = new Menu(menuItem);
		menuItem.setMenu(zoomOutMenu);

		// zoom out both axes
		menuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
		menuItem.setText(Messages.ZOOMOUT);
		menuItem.addListener(SWT.Selection, this);

		// zoom out X axis
		menuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
		menuItem.setText(Messages.ZOOMOUT_X);
		menuItem.addListener(SWT.Selection, this);

		// zoom out Y axis
		menuItem = new MenuItem(zoomOutMenu, SWT.PUSH);
		menuItem.setText(Messages.ZOOMOUT_Y);
		menuItem.addListener(SWT.Selection, this);

		menuItem = new MenuItem(menu, SWT.SEPARATOR);

		// properties
		menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText(Messages.PROPERTIES);
		menuItem.addListener(SWT.Selection, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events
	 * .PaintEvent)
	 */
	public void paintControl(PaintEvent e) {
		selection.draw(e.gc);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	@Override
	public void handleEvent(Event event) {
		super.handleEvent(event);

		switch (event.type) {
		case SWT.MouseDown:
			handleMouseDownEvent(event);
			break;
		case SWT.MouseMove:
			handleMouseMoveEvent(event);
			break;
		case SWT.MouseUp:
			handleMouseUpEvent(event);
			break;
		case SWT.Selection:
			handleSelectionEvent(event);
			break;
		case SWT.KeyDown:
			handleKeyDownEvent(event);
			break;
		default:
			break;
		}
	}

	/**
	 * Handles mouse move event.
	 * 
	 * @param event
	 *            the mouse move event
	 */
	private void handleMouseMoveEvent(Event event) {
		if (!selection.isDisposed()) {
			selection.setEndPoint(event.x, event.y);
			redraw();
		}
	}

	/**
	 * Handles the mouse down event.
	 * 
	 * @param event
	 *            the mouse down event
	 */
	private void handleMouseDownEvent(Event event) {
		if (event.button == 1) {
			selection.setStartPoint(event.x, event.y);
			clickedTime = System.currentTimeMillis();
		}
	}

	/**
	 * Handles the mouse up event.
	 * 
	 * @param event
	 *            the mouse up event
	 */
	private void handleMouseUpEvent(Event event) {
		if (event.button == 1 && System.currentTimeMillis() - clickedTime > 100) {
			for (IAxis axis : getAxisSet().getAxes()) {
				Direction direction;
				if (getOrientation() == SWT.HORIZONTAL) {
					direction = axis.getDirection();
				} else {
					direction = (axis.getDirection() == Direction.X) ? Direction.Y
							: Direction.X;
				}
				Range range = selection.getRange(direction, getPlotArea()
						.getClientArea());
				if (range.lower != range.upper) {
					setRange(range, axis);
				}
			}
		}
		selection.dispose();
		redraw();
	}

	/**
	 * Handles the key down event.
	 * 
	 * @param event
	 *            the key down event
	 */
	private void handleKeyDownEvent(Event event) {
		if (event.keyCode == SWT.ARROW_DOWN) {
			if (event.stateMask == SWT.CTRL) {
				getAxisSet().zoomOut();
			} else {
				for (IAxis axis : getAxes(SWT.VERTICAL)) {
					axis.scrollDown();
				}
			}
			redraw();
		} else if (event.keyCode == SWT.ARROW_UP) {
			if (event.stateMask == SWT.CTRL) {
				getAxisSet().zoomIn();
			} else {
				for (IAxis axis : getAxes(SWT.VERTICAL)) {
					axis.scrollUp();
				}
			}
			redraw();
		} else if (event.keyCode == SWT.ARROW_LEFT) {
			for (IAxis axis : getAxes(SWT.HORIZONTAL)) {
				axis.scrollDown();
			}
			redraw();
		} else if (event.keyCode == SWT.ARROW_RIGHT) {
			for (IAxis axis : getAxes(SWT.HORIZONTAL)) {
				axis.scrollUp();
			}
			redraw();
		}
	}

	/**
	 * Gets the axes for given orientation.
	 * 
	 * @param orientation
	 *            the orientation
	 * @return the axes
	 */
	private IAxis[] getAxes(int orientation) {
		IAxis[] axes;
		if (getOrientation() == orientation) {
			axes = getAxisSet().getXAxes();
		} else {
			axes = getAxisSet().getYAxes();
		}
		return axes;
	}

	/**
	 * Handles the selection event.
	 * 
	 * @param event
	 *            the event
	 */
	private void handleSelectionEvent(Event event) {

		if (!(event.widget instanceof MenuItem)) {
			return;
		}
		MenuItem menuItem = (MenuItem) event.widget;

		if (menuItem.getText().equals(Messages.AUTOSCALE)) {
			getAxisSet().autoScale();
		} else if (menuItem.getText().equals(Messages.AUTOSCALE_X)) {
			for (IAxis axis : getAxisSet().getXAxes()) {
				axis.autoScale();
			}
		} else if (menuItem.getText().equals(Messages.AUTOSCALE_Y)) {
			for (IAxis axis : getAxisSet().getYAxes()) {
				axis.autoScale();
			}
		} else if (menuItem.getText().equals(Messages.ZOOMIN)) {
			getAxisSet().zoomIn();
		} else if (menuItem.getText().equals(Messages.ZOOMIN_X)) {
			for (IAxis axis : getAxisSet().getXAxes()) {
				axis.zoomIn();
			}
		} else if (menuItem.getText().equals(Messages.ZOOMIN_Y)) {
			for (IAxis axis : getAxisSet().getYAxes()) {
				axis.zoomIn();
			}
		} else if (menuItem.getText().equals(Messages.ZOOMOUT)) {
			getAxisSet().zoomOut();
		} else if (menuItem.getText().equals(Messages.ZOOMOUT_X)) {
			for (IAxis axis : getAxisSet().getXAxes()) {
				axis.zoomOut();
			}
		} else if (menuItem.getText().equals(Messages.ZOOMOUT_Y)) {
			for (IAxis axis : getAxisSet().getYAxes()) {
				axis.zoomOut();
			}
		} else if (menuItem.getText().equals(Messages.PROPERTIES)) {
			openPropertiesDialog();
		}
		redraw();
	}

	/**
	 * Opens the properties dialog.
	 */
	private void openPropertiesDialog() {
		PreferenceManager manager = new PreferenceManager();

		final String chartTitle = "Chart";
		PreferenceNode chartNode = new PreferenceNode(chartTitle);
		chartNode.setPage(new ChartPage(this, chartTitle));
		manager.addToRoot(chartNode);

		final String legendTitle = "Legend";
		PreferenceNode legendNode = new PreferenceNode(legendTitle);
		legendNode.setPage(new LegendPage(this, legendTitle));
		manager.addTo(chartTitle, legendNode);

		final String xAxisTitle = "X Axis";
		PreferenceNode xAxisNode = new PreferenceNode(xAxisTitle);
		xAxisNode.setPage(new AxisPage(this, Direction.X, xAxisTitle));
		manager.addTo(chartTitle, xAxisNode);

		final String gridTitle = "Grid";
		PreferenceNode xGridNode = new PreferenceNode(gridTitle);
		xGridNode.setPage(new GridPage(this, Direction.X, gridTitle));
		manager.addTo(chartTitle + "." + xAxisTitle, xGridNode);

		final String tickTitle = "Tick";
		PreferenceNode xTickNode = new PreferenceNode(tickTitle);
		xTickNode.setPage(new AxisTickPage(this, Direction.X, tickTitle));
		manager.addTo(chartTitle + "." + xAxisTitle, xTickNode);

		final String yAxisTitle = "Y Axis";
		PreferenceNode yAxisNode = new PreferenceNode(yAxisTitle);
		yAxisNode.setPage(new AxisPage(this, Direction.Y, yAxisTitle));
		manager.addTo(chartTitle, yAxisNode);

		PreferenceNode yGridNode = new PreferenceNode(gridTitle);
		yGridNode.setPage(new GridPage(this, Direction.Y, gridTitle));
		manager.addTo(chartTitle + "." + yAxisTitle, yGridNode);

		PreferenceNode yTickNode = new PreferenceNode(tickTitle);
		yTickNode.setPage(new AxisTickPage(this, Direction.Y, tickTitle));
		manager.addTo(chartTitle + "." + yAxisTitle, yTickNode);

		final String seriesTitle = "Series";
		PreferenceNode plotNode = new PreferenceNode(seriesTitle);
		plotNode.setPage(new SeriesPage(this, seriesTitle));
		manager.addTo(chartTitle, plotNode);

		final String labelTitle = "Label";
		PreferenceNode labelNode = new PreferenceNode(labelTitle);
		labelNode.setPage(new SeriesLabelPage(this, labelTitle));
		manager.addTo(chartTitle + "." + seriesTitle, labelNode);

		PreferenceDialog dialog = new PreferenceDialog(getShell(), manager);
		dialog.create();
		dialog.getShell().setText("Properties");
		dialog.open();
	}

	/**
	 * Sets the axis range in ratio
	 * 
	 * @param range
	 *            the axis range in ratio (0.0 to 1.0)
	 * @param axis
	 *            the axis to set range
	 */
	private void setRange(Range range, IAxis axis) {
		if (range == null) {
			return;
		}

		double lowerRatio = 1.0;
		double upperRatio = 1.0;
		Direction direction = axis.getDirection();
		if ((direction == Direction.X && getOrientation() == SWT.HORIZONTAL)
				|| (direction == Direction.Y && getOrientation() == SWT.VERTICAL)) {
			lowerRatio = range.lower;
			upperRatio = range.upper;
		} else {
			lowerRatio = 1.0 - range.upper;
			upperRatio = 1.0 - range.lower;
		}

		double newMin;
		double min = axis.getRange().lower;
		double newMax;
		double max = axis.getRange().upper;

		if (axis.isLogScaleEnabled()) {
			double digitMin = Math.log10(min);
			double digitMax = Math.log10(max);

			// log(newMin) - log(min) = ratio * (log(max) - log(min))
			newMin = Math
					.pow(10, digitMin + lowerRatio * (digitMax - digitMin));

			// log(max) - log(newMax) = ratio * (log(max) - log(min))
			newMax = Math
					.pow(10, digitMin + upperRatio * (digitMax - digitMin));
		} else if (axis.isCategoryEnabled()) {
			newMin = min + lowerRatio * ((int) max - (int) min + 1);
			newMax = min + upperRatio * ((int) max - (int) min + 1);
		} else {
			newMin = min + lowerRatio * (max - min);
			newMax = min + upperRatio * (max - min);
		}

		axis.setRange(new Range(newMin, newMax));
	}
}