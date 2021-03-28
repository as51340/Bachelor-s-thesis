package hr.fer.zemris.bachelor.thesis.ai.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

	private Map<Integer, Double> genToBest;

	private Map<Integer, Double> genToAvg;

	private int width = 1280;

	private int height = 720;

	private String title = "FPGA generation to fitness";

	private String categoryAxisLabel = "Generation";

	private String valueAxisLabel = "Fitness";

	public EvolutionaryFPGAGUIMaker(Map<Integer, Double> genToBest, Map<Integer, Double> genToAvg) {
		super("FPGA fitness function");
		this.genToBest = genToBest;
		this.genToAvg = genToAvg;
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

	private void drawXYChart() throws IOException {
		JFreeChart xylineChart = ChartFactory.createXYLineChart(title, "Category", "Score", createXYDataset(),
				PlotOrientation.VERTICAL, true, true, false);
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
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy:MM:dd_HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		File out = new File("imgs/" + dtf.format(now) + ".jpeg");
		ChartUtilities.saveChartAsPNG(out, xylineChart, width, height);

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

	private void drawLineChart() throws IOException {
		JPanel chartPanel = createChartPanel();
		add(chartPanel, BorderLayout.CENTER);
	}

	private JPanel createChartPanel() throws IOException {
		String chartTitle = title;

		CategoryDataset dataset = createDataset();

		JFreeChart chart = ChartFactory.createLineChart(chartTitle, categoryAxisLabel, valueAxisLabel, dataset,
				PlotOrientation.VERTICAL, true, true, false);

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		File out = new File("/imgs/" + dtf.format(now) + ".jpeg");

		ChartUtilities.saveChartAsJPEG(out, chart, width, height);
		return new ChartPanel(chart);
	}

	private CategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String best = "genToBest";
		String avg = "genToAvg";
		for (Integer gen : genToBest.keySet()) {
//			System.out.println(gen + " " + genToBest.get(gen));
			dataset.addValue(gen, best, genToBest.get(gen));
		}
		for (Integer gen : genToAvg.keySet()) {
//			System.out.println(gen + " " + genToAvg.get(gen));
			dataset.addValue(gen, avg, genToAvg.get(gen));
		}
		return dataset;
	}

}