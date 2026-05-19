package com.dsa.ui;

import javafx.collections.FXCollections;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.util.Map;

public final class ChartBuilder {

    private ChartBuilder() {
    }

    public static PieChart createDamagePieChart() {
        PieChart chart = new PieChart();
        chart.setTitle("Damage Categories");
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);
        chart.getStyleClass().add("chart");
        return chart;
    }

    public static BarChart<String, Number> createSentimentBarChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Sentiment");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Posts");
        yAxis.setMinorTickVisible(false);

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Sentiment Distribution");
        chart.setLegendVisible(false);
        chart.setCategoryGap(20);
        chart.getStyleClass().add("chart");
        return chart;
    }

    public static LineChart<String, Number> createSentimentTrendLineChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Posts");
        yAxis.setMinorTickVisible(false);

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Sentiment Trend Over Time");
        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("chart");
        return chart;
    }

    public static void updateDamagePieChart(PieChart chart, Map<String, Integer> damageResults) {
        chart.getData().clear();
        if (damageResults == null) {
            return;
        }

        for (Map.Entry<String, Integer> entry : damageResults.entrySet()) {
            String label = ResultFormatter.formatLabel(entry.getKey())
                    + " (" + entry.getValue() + ")";
            chart.getData().add(new PieChart.Data(label, entry.getValue()));
        }
    }

    public static void updateSentimentBarChart(BarChart<String, Number> chart, Map<String, Integer> sentimentResults) {
        chart.getData().clear();
        if (sentimentResults == null) {
            return;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Posts");

        for (Map.Entry<String, Integer> entry : sentimentResults.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
    }

    public static void updateSentimentTrendLineChart(
            LineChart<String, Number> chart,
            Map<String, Map<String, Integer>> trendResults) {

        chart.getData().clear();
        if (trendResults == null || trendResults.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> positive = new XYChart.Series<>();
        positive.setName("Positive");

        XYChart.Series<String, Number> negative = new XYChart.Series<>();
        negative.setName("Negative");

        XYChart.Series<String, Number> neutral = new XYChart.Series<>();
        neutral.setName("Neutral");

        for (Map.Entry<String, Map<String, Integer>> dayEntry : trendResults.entrySet()) {
            String date = dayEntry.getKey();
            Map<String, Integer> counts = dayEntry.getValue();

            positive.getData().add(new XYChart.Data<>(date, counts.getOrDefault("positive", 0)));
            negative.getData().add(new XYChart.Data<>(date, counts.getOrDefault("negative", 0)));
            neutral.getData().add(new XYChart.Data<>(date, counts.getOrDefault("neutral", 0)));
        }

        chart.setData(FXCollections.observableArrayList(positive, negative, neutral));
    }
}
