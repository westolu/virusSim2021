import org.knowm.xchart.*;
import java.util.ArrayList;
public class Charter {
    private final ArrayList<Double> xData;
    private final ArrayList<Double> yData;
    private final XYChart chart;
    private final SwingWrapper<XYChart> wrapper;
    public Charter(String chartTitle, String xAxisTitle, String yAxisTitle, String seriesName, double dataX, double dataY) {
        xData = new ArrayList<>(); //arraylists because they are move flexible than arrays (no defined capacity)
        yData = new ArrayList<>();
        chart = QuickChart.getChart(chartTitle, xAxisTitle, yAxisTitle, seriesName, new double[]{dataX}, new double[]{dataY});      //use library to make our own little graph
        wrapper = new SwingWrapper<>(chart);    //put the graph into the swingwrapper 
        wrapper.displayChart();             //display the graph on the wrapper
    }
    public void addNewData(double newDataX, double newDataY) {  //this, when called, adds a new datapoint to the graph
        xData.add(newDataX);                                    
        yData.add(newDataY);
        chart.updateXYSeries("people infected", xData, yData, null);  //updates the new datapoints onto the graph
        wrapper.repaintChart();         //repaint the graph
    }
}
