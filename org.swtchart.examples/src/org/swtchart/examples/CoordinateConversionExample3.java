package org.swtchart.examples;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IAxis;
import org.swtchart.ILineSeries;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example to convert series data coordinate into pixel coordinate.
 */
public class CoordinateConversionExample3 {

    private static final double[] ySeries1 = { 3.0, 2.1, 1.9, 2.3, 3.2 };
    private static final double[] ySeries2 = { 2.0, 3.1, 0.9, 1.3, 2.2 };

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Tooltip Example");
        shell.setSize(500, 400);
        shell.setLayout(new FillLayout());

        // create a chart
        final Chart chart = new Chart(shell, SWT.NONE);

        // set titles
        chart.getTitle().setText("Tooltip Example");
        final IAxis xAxis = chart.getAxisSet().getXAxis(0);
        final IAxis yAxis = chart.getAxisSet().getYAxis(0);
        xAxis.getTitle().setText("Data Points");
        yAxis.getTitle().setText("Amplitude");

        // create line series
        ILineSeries series1 = (ILineSeries) chart.getSeriesSet()
                .createSeries(SeriesType.LINE, "series 1");
        series1.setYSeries(ySeries1);

        ILineSeries series2 = (ILineSeries) chart.getSeriesSet().createSeries(
                SeriesType.LINE, "series 2");
        series2.setYSeries(ySeries2);
        series2.setLineColor(new Color(Display.getDefault(), new RGB(80, 240,
                180)));
        
        // adjust the axis range
        chart.getAxisSet().adjustRange();

        // add mouse move listener to open tooltip on data point
        chart.getPlotArea().addMouseMoveListener(new MouseMoveListener() {
            @Override
            public void mouseMove(MouseEvent e) {
                for (ISeries series : chart.getSeriesSet().getSeries()) {
                    for (int i = 0; i < series.getYSeries().length; i++) {
                        Point p = series.getPixelCoordinates(i);
                        double distance = Math.sqrt(Math.pow(e.x - p.x, 2)
                                + Math.pow(e.y - p.y, 2));

                        if (distance < ((ILineSeries) series).getSymbolSize()) {
                            setToolTipText(series, i);
                            return;
                        }
                    }
                }
                chart.getPlotArea().setToolTipText(null);
            }

            private void setToolTipText(ISeries series, int index) {
                chart.getPlotArea().setToolTipText(
                        "Series: " + series.getId() + "\nValue: "
                                + series.getYSeries()[index]);
            }
        });

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}