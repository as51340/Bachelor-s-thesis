package hr.fer.zemris.bachelor.thesis.ai.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * TODO Add if we want to get relative to zero fitness function graph.
 * 
 * @author andi
 *
 */
public class EvolutionaryFPGAGUIMaker extends JFrame {

	private static final long serialVersionUID = 8164916626673884334L;

	private Map<Integer, Double> genToBest = null;

	private Map<Integer, Double> genToAvg = null;

	private List<Double> intensities = null;
	
	private int width = 1280;

	private int height = 720;

	private String title = "FPGA generation to fitness";

	private String categoryAxisLabel = "Generation";

	private String valueAxisLabel = "Fitness";
	
	private String algShort = "D";
	

	public EvolutionaryFPGAGUIMaker(String algShort, String title, Map<Integer, Double> genToBest, Map<Integer, Double> genToAvg) {
		super("FPGA fitness function");
		this.genToBest = genToBest;
		this.genToAvg = genToAvg;
		this.title = title;
		this.algShort = algShort;
		// drawLineChart();
		setSize(width, height);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		try {
			drawXYChart();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public EvolutionaryFPGAGUIMaker(String title, List<Double> intensities) {
		super("FPGA intensities");
		this.intensities = intensities;
		this.title = title;
		setSize(width, height);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		try {
			drawXYChart();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void drawXYChart() throws IOException {
		JFreeChart xylineChart = null;
		
		if(this.intensities == null) {
			xylineChart = ChartFactory.createXYLineChart(algShort + " " + title, this.categoryAxisLabel, this.valueAxisLabel, createXYDataset(),
					PlotOrientation.VERTICAL, true, true, false);
		} else {
			xylineChart = ChartFactory.createXYLineChart(title,"Generation", "Intensity", createXYDatasetIntensity(), PlotOrientation.VERTICAL, true,
					true, false);
		}
		
		ChartPanel chartPanel = new ChartPanel(xylineChart);
		final XYPlot plot = xylineChart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.GREEN);
		renderer.setSeriesPaint(2, Color.YELLOW);
		renderer.setSeriesStroke(0, new BasicStroke(4.0f));
		renderer.setSeriesStroke(1, new BasicStroke(3.0f));
		renderer.setSeriesStroke(2, new BasicStroke(2.0f));
		plot.setRenderer(renderer);
		setContentPane(chartPanel);
		if(this.intensities == null) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			File out = new File("imgs/" + algShort + dtf.format(now) + ".png");
			ChartUtilities.saveChartAsPNG(out, xylineChart, width, height);
		} else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			File out = new File("intensities/" + algShort + dtf.format(now) + ".png");
			ChartUtilities.saveChartAsPNG(out, xylineChart, width, height);
		}
	}
	
	private XYDataset createXYDatasetIntensity() {
		final XYSeries series = new XYSeries("Intensities");
		for(int i = 0; i < intensities.size(); i++) {
			series.add((i+1) / 2, intensities.get(i));
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		return dataset;
	}
	
	

	private XYDataset createXYDataset() {
		final XYSeries best = new XYSeries("Best");
		for (Integer gen : genToBest.keySet()) {
//			System.out.println(gen + " " + genToBest.get(gen));
			best.add(gen, genToBest.get(gen));
		}
		final XYSeries avg = new XYSeries("Avg");
		for (Integer gen : genToAvg.keySet()) {
//			System.out.println(gen + " " + genToAvg.get(gen));
			avg.add(gen, genToAvg.get(gen));
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(best);
		dataset.addSeries(avg);
		return dataset;
	}
}