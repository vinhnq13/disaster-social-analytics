package com.dsa.ui;

import com.dsa.analyzer.Analyzer;
import com.dsa.analyzer.DamageAnalyzer;
import com.dsa.analyzer.ReliefSatisfactionAnalyzer;
import com.dsa.analyzer.SentimentAnalyzer;
import com.dsa.analyzer.SentimentTrendAnalyzer;
import com.dsa.config.KeywordConfigLoader;
import com.dsa.model.Post;
import com.dsa.preprocess.BasicTextPreprocessor;
import com.dsa.preprocess.TextPreprocessor;
import com.dsa.sentiment.KeywordSentimentModel;
import com.dsa.sentiment.SentimentModel;
import com.dsa.service.DataService;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardController {

    private final BorderPane root;
    private final DataService dataService;
    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> damageKeywords;
    private final Map<String, List<String>> reliefKeywords;
    private final SentimentModel sentimentModel;

    private final ObservableList<Post> allPosts = FXCollections.observableArrayList();
    private final FilteredList<Post> filteredPosts;

    private List<Post> loadedPosts = List.of();
    private Map<String, Integer> damageResults;
    private Map<String, Integer> sentimentResults;
    private Map<String, Map<String, Integer>> trendResults;
    private Map<String, Integer> reliefResults;
    private String lastReport = "";

    private Label totalPostsValue;
    private Label positivePostsValue;
    private Label negativePostsValue;
    private Label mostCommonDamageValue;
    private Label postsCountLabel;
    private Label statusLabel;

    private TableView<Post> postsTable;
    private TextField searchField;
    private ComboBox<String> sourceComboBox;

    private PieChart damagePieChart;
    private BarChart<String, Number> sentimentBarChart;
    private LineChart<String, Number> sentimentTrendLineChart;

    private TextArea reportTextArea;
    private TableView<ReliefRow> reliefTable;

    public DashboardController() {
        dataService = new DataService();
        textPreprocessor = new BasicTextPreprocessor();

        KeywordConfigLoader keywordConfigLoader = new KeywordConfigLoader();
        damageKeywords = keywordConfigLoader.loadDamageKeywords();
        Map<String, List<String>> sentimentKeywords = keywordConfigLoader.loadSentimentKeywords();
        reliefKeywords = keywordConfigLoader.loadReliefKeywords();
        sentimentModel = new KeywordSentimentModel(textPreprocessor, sentimentKeywords);

        filteredPosts = new FilteredList<>(allPosts, post -> true);

        root = new BorderPane();
        root.setLeft(buildSidebar());
        root.setTop(buildHeader());
        root.setCenter(buildCenter());
    }

    public BorderPane getRoot() {
        return root;
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");

        Label title = new Label("DSA System");
        title.getStyleClass().add("sidebar-title");

        Button loadButton = createSidebarButton("Load Data", this::handleLoadData);
        Button analyzeButton = createSidebarButton("Run Analysis", this::handleRunAnalysis);
        Button exportButton = createSidebarButton("Export Report", this::handleExportReport);
        Button clearButton = createSidebarButton("Clear", this::handleClear);

        statusLabel = new Label("Ready.");
        statusLabel.getStyleClass().add("status-label");
        statusLabel.setWrapText(true);

        sidebar.getChildren().addAll(title, loadButton, analyzeButton, exportButton, clearButton, statusLabel);
        VBox.setVgrow(statusLabel, Priority.ALWAYS);
        return sidebar;
    }

    private Button createSidebarButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("sidebar-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        return button;
    }

    private VBox buildHeader() {
        VBox header = new VBox();
        header.getStyleClass().add("header");

        Label title = new Label("Disaster Social Media Analytics System");
        title.getStyleClass().add("header-title");

        Label subtitle = new Label("Case Study: Typhoon Yagi | 2024-09-06 to 2024-09-20");
        subtitle.getStyleClass().add("header-subtitle");

        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private BorderPane buildCenter() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("content-area");

        Tab datasetTab = new Tab("Dataset", buildDatasetTab());
        datasetTab.setClosable(false);

        Tab overviewTab = new Tab("Overview", buildOverviewTab());
        overviewTab.setClosable(false);

        Tab trendTab = new Tab("Sentiment Trend", buildTrendTab());
        trendTab.setClosable(false);

        Tab reliefTab = new Tab("Relief Satisfaction", buildReliefTab());
        reliefTab.setClosable(false);

        tabPane.getTabs().addAll(datasetTab, overviewTab, trendTab, reliefTab);

        BorderPane center = new BorderPane();
        center.setCenter(tabPane);
        BorderPane.setMargin(tabPane, new Insets(0, 16, 16, 16));
        return center;
    }

    private VBox buildDatasetTab() {
        searchField = new TextField();
        searchField.setPromptText("Search by content keyword...");
        searchField.setPrefWidth(280);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        sourceComboBox = new ComboBox<>(FXCollections.observableArrayList(
                "All", "Facebook", "TikTok", "YouTube", "X"));
        sourceComboBox.setValue("All");
        sourceComboBox.setOnAction(event -> applyFilters());

        postsCountLabel = new Label();
        postsCountLabel.getStyleClass().add("posts-count-label");
        postsCountLabel.textProperty().bind(Bindings.concat(
                "Displayed posts: ", Bindings.size(filteredPosts)));

        HBox filterBar = new HBox(12);
        filterBar.getStyleClass().add("filter-bar");
        filterBar.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("Search:");
        searchLabel.getStyleClass().add("filter-label");
        Label sourceLabel = new Label("Source:");
        sourceLabel.getStyleClass().add("filter-label");

        filterBar.getChildren().addAll(searchLabel, searchField, sourceLabel, sourceComboBox, postsCountLabel);

        postsTable = new TableView<>(filteredPosts);
        postsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Post, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.setPrefWidth(100);

        TableColumn<Post, String> sourceColumn = new TableColumn<>("Source");
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        sourceColumn.setPrefWidth(100);

        TableColumn<Post, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));

        postsTable.getColumns().addAll(dateColumn, sourceColumn, contentColumn);
        VBox.setVgrow(postsTable, Priority.ALWAYS);

        VBox tabContent = new VBox(12, filterBar, postsTable);
        tabContent.setPadding(new Insets(8));
        return tabContent;
    }

    private VBox buildOverviewTab() {
        VBox totalPostsCard = createSummaryCard("Total Posts", "0");
        totalPostsValue = (Label) totalPostsCard.getChildren().get(1);

        VBox positivePostsCard = createSummaryCard("Positive Posts", "0");
        positivePostsValue = (Label) positivePostsCard.getChildren().get(1);

        VBox negativePostsCard = createSummaryCard("Negative Posts", "0");
        negativePostsValue = (Label) negativePostsCard.getChildren().get(1);

        VBox mostCommonDamageCard = createSummaryCard("Most Common Damage", "N/A");
        mostCommonDamageValue = (Label) mostCommonDamageCard.getChildren().get(1);

        HBox cardsRow = new HBox(16,
                totalPostsCard, positivePostsCard, negativePostsCard, mostCommonDamageCard);
        cardsRow.setAlignment(Pos.CENTER_LEFT);

        damagePieChart = ChartBuilder.createDamagePieChart();
        sentimentBarChart = ChartBuilder.createSentimentBarChart();

        VBox piePanel = wrapChartPanel(damagePieChart);
        VBox barPanel = wrapChartPanel(sentimentBarChart);
        HBox.setHgrow(piePanel, Priority.ALWAYS);
        HBox.setHgrow(barPanel, Priority.ALWAYS);

        HBox chartsRow = new HBox(16, piePanel, barPanel);
        chartsRow.setAlignment(Pos.CENTER);
        VBox.setVgrow(chartsRow, Priority.ALWAYS);

        reportTextArea = new TextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setWrapText(true);
        reportTextArea.setPrefRowCount(8);
        reportTextArea.getStyleClass().add("result-text");
        reportTextArea.setPromptText("Run analysis to see the full text report here.");

        VBox tabContent = new VBox(16, cardsRow, chartsRow, new Label("Analysis Report"), reportTextArea);
        VBox.setVgrow(chartsRow, Priority.ALWAYS);
        VBox.setVgrow(reportTextArea, Priority.ALWAYS);
        tabContent.setPadding(new Insets(8));
        return tabContent;
    }

    private VBox buildTrendTab() {
        sentimentTrendLineChart = ChartBuilder.createSentimentTrendLineChart();
        VBox chartPanel = wrapChartPanel(sentimentTrendLineChart);
        VBox.setVgrow(chartPanel, Priority.ALWAYS);

        VBox tabContent = new VBox(chartPanel);
        tabContent.setPadding(new Insets(8));
        VBox.setVgrow(tabContent, Priority.ALWAYS);
        return tabContent;
    }

    private VBox buildReliefTab() {
        reliefTable = new TableView<>();
        reliefTable.getStyleClass().add("relief-table");
        reliefTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<ReliefRow, String> categoryColumn = new TableColumn<>("Relief Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<ReliefRow, String> sentimentColumn = new TableColumn<>("Sentiment");
        sentimentColumn.setCellValueFactory(new PropertyValueFactory<>("sentiment"));

        TableColumn<ReliefRow, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        reliefTable.getColumns().addAll(categoryColumn, sentimentColumn, countColumn);
        VBox.setVgrow(reliefTable, Priority.ALWAYS);

        VBox tabContent = new VBox(12, reliefTable);
        tabContent.setPadding(new Insets(8));
        return tabContent;
    }

    private VBox createSummaryCard(String title, String value) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("summary-card-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("summary-card-value");

        VBox card = new VBox(titleLabel, valueLabel);
        card.getStyleClass().add("summary-card");
        card.setAlignment(Pos.TOP_LEFT);
        return card;
    }

    private VBox wrapChartPanel(javafx.scene.chart.Chart chart) {
        VBox panel = new VBox(chart);
        panel.getStyleClass().add("chart-panel");
        VBox.setVgrow(chart, Priority.ALWAYS);
        chart.setMinHeight(280);
        return panel;
    }

    private void handleLoadData() {
        loadedPosts = new ArrayList<>(dataService.loadPosts());
        allPosts.setAll(loadedPosts);
        applyFilters();

        totalPostsValue.setText(String.valueOf(loadedPosts.size()));
        updateSummaryFromSentiment(null);
        mostCommonDamageValue.setText("N/A");

        clearAnalysisViews();
        statusLabel.setText("Loaded " + loadedPosts.size() + " posts from JSON dataset.");

        if (loadedPosts.isEmpty()) {
            showWarning("No posts were loaded. Check posts.json and console error messages.");
        }
    }

    private void handleRunAnalysis() {
        if (loadedPosts.isEmpty()) {
            showWarning("Please load data before running analysis.");
            return;
        }

        Analyzer damageAnalyzer = new DamageAnalyzer(textPreprocessor, damageKeywords);
        Analyzer sentimentAnalyzer = new SentimentAnalyzer(sentimentModel);
        SentimentTrendAnalyzer trendAnalyzer = new SentimentTrendAnalyzer(sentimentModel);
        Analyzer reliefAnalyzer = new ReliefSatisfactionAnalyzer(
                textPreprocessor, reliefKeywords, sentimentModel);

        damageResults = damageAnalyzer.analyze(loadedPosts);
        sentimentResults = sentimentAnalyzer.analyze(loadedPosts);
        trendResults = trendAnalyzer.analyze(loadedPosts);
        reliefResults = reliefAnalyzer.analyze(loadedPosts);

        lastReport = ResultFormatter.formatFullReport(
                loadedPosts, damageResults, sentimentResults, trendResults, reliefResults);

        updateSummaryCards();
        ChartBuilder.updateDamagePieChart(damagePieChart, damageResults);
        ChartBuilder.updateSentimentBarChart(sentimentBarChart, sentimentResults);
        ChartBuilder.updateSentimentTrendLineChart(sentimentTrendLineChart, trendResults);

        reportTextArea.setText(lastReport);
        updateReliefTable();

        statusLabel.setText("Analysis completed for " + loadedPosts.size() + " posts.");
    }

    private void handleExportReport() {
        if (lastReport == null || lastReport.isBlank()) {
            showWarning("No analysis report to export. Run analysis first.");
            return;
        }

        Path reportPath = Path.of("analysis-report.txt");
        try {
            Files.writeString(reportPath, lastReport, StandardCharsets.UTF_8);
            statusLabel.setText("Report exported to " + reportPath.toAbsolutePath());
            showInfo("Report saved to:\n" + reportPath.toAbsolutePath());
        } catch (IOException e) {
            showError("Could not export report: " + e.getMessage());
        }
    }

    private void handleClear() {
        loadedPosts = List.of();
        allPosts.clear();
        damageResults = null;
        sentimentResults = null;
        trendResults = null;
        reliefResults = null;
        lastReport = "";

        clearAnalysisViews();
        totalPostsValue.setText("0");
        statusLabel.setText("Dashboard cleared.");
    }

    private void clearAnalysisViews() {
        positivePostsValue.setText("0");
        negativePostsValue.setText("0");
        mostCommonDamageValue.setText("N/A");
        reportTextArea.clear();
        reliefTable.getItems().clear();
        ChartBuilder.updateDamagePieChart(damagePieChart, null);
        ChartBuilder.updateSentimentBarChart(sentimentBarChart, null);
        ChartBuilder.updateSentimentTrendLineChart(sentimentTrendLineChart, null);
    }

    private void updateSummaryCards() {
        totalPostsValue.setText(String.valueOf(loadedPosts.size()));
        updateSummaryFromSentiment(sentimentResults);
        mostCommonDamageValue.setText(ResultFormatter.findMostCommonCategory(damageResults));
    }

    private void updateSummaryFromSentiment(Map<String, Integer> sentiment) {
        if (sentiment == null) {
            positivePostsValue.setText("0");
            negativePostsValue.setText("0");
            return;
        }
        positivePostsValue.setText(String.valueOf(sentiment.getOrDefault("positive", 0)));
        negativePostsValue.setText(String.valueOf(sentiment.getOrDefault("negative", 0)));
    }

    private void updateReliefTable() {
        ObservableList<ReliefRow> rows = FXCollections.observableArrayList();
        if (reliefResults != null) {
            for (Map.Entry<String, Integer> entry : reliefResults.entrySet()) {
                rows.add(ReliefRow.fromKey(entry.getKey(), entry.getValue()));
            }
        }
        reliefTable.setItems(rows);
    }

    private void applyFilters() {
        String keyword = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();
        String source = sourceComboBox.getValue();

        filteredPosts.setPredicate(post -> {
            boolean matchesKeyword = keyword.isEmpty()
                    || (post.getContent() != null
                    && post.getContent().toLowerCase().contains(keyword));
            boolean matchesSource = source == null
                    || "All".equals(source)
                    || source.equals(post.getSource());
            return matchesKeyword && matchesSource;
        });
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static class ReliefRow {
        private final String category;
        private final String sentiment;
        private final int count;

        public ReliefRow(String category, String sentiment, int count) {
            this.category = category;
            this.sentiment = sentiment;
            this.count = count;
        }

        public String getCategory() {
            return category;
        }

        public String getSentiment() {
            return sentiment;
        }

        public int getCount() {
            return count;
        }

        static ReliefRow fromKey(String key, int count) {
            int index = key.lastIndexOf('_');
            if (index <= 0) {
                return new ReliefRow(key, "", count);
            }
            String category = ResultFormatter.formatLabel(key.substring(0, index));
            String sentiment = key.substring(index + 1);
            return new ReliefRow(category, sentiment, count);
        }
    }
}
