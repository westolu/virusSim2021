import org.knowm.xchart.*;
import java.util.ArrayList;
public class Charter {
    private final ArrayList<Double> xData;
    private final ArrayList<Double> yData;
    private final XYChart chart;
    private final SwingWrapper<XYChart> wrapper;
    public Charter(String chartTitle, String xAxisTitle, String yAxisTitle, String seriesName, double dataX, double dataY) {
        xData = new ArrayList<>();
        yData = new ArrayList<>();
        chart = QuickChart.getChart(chartTitle, xAxisTitle, yAxisTitle, seriesName, new double[] {dataX}, new double[] {dataY});
        wrapper = new SwingWrapper<>(chart);
        wrapper.displayChart();
    }
    //CALL THIS WITH YOUR OTHER THINGS TO ADD DATA TO THE GRAPH
    public void addNewData(double newDataX, double newDataY) {
        xData.add(newDataX);
        yData.add(newDataY);
        chart.updateXYSeries("people infected", xData, yData, null);
        wrapper.repaintChart();
    }
}
