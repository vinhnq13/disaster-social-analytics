package com.dsa.ui;

import com.dsa.model.Post;

import java.util.List;
import java.util.Map;

public final class ResultFormatter {

    private ResultFormatter() {
    }

    public static String formatFullReport(
            List<Post> posts,
            Map<String, Integer> damageResults,
            Map<String, Integer> sentimentResults,
            Map<String, Map<String, Integer>> trendResults,
            Map<String, Integer> reliefResults) {

        StringBuilder report = new StringBuilder();
        report.append("=== Disaster Social Media Analytics Report ===\n\n");
        report.append("Total posts analyzed: ").append(posts.size()).append("\n\n");

        appendSection(report, "Damage Analysis", damageResults);
        appendSection(report, "Sentiment Analysis", sentimentResults);
        appendTrendSection(report, trendResults);
        appendSection(report, "Relief Satisfaction Analysis", reliefResults);

        return report.toString();
    }

    public static String formatReliefTable(Map<String, Integer> reliefResults) {
        if (reliefResults == null || reliefResults.isEmpty()) {
            return "No relief satisfaction results. Run analysis after loading data.";
        }

        StringBuilder text = new StringBuilder();
        text.append(String.format("%-22s %-12s %6s%n", "Relief Category", "Sentiment", "Count"));
        text.append("-".repeat(44)).append("\n");

        for (Map.Entry<String, Integer> entry : reliefResults.entrySet()) {
            String[] parts = splitReliefKey(entry.getKey());
            text.append(String.format("%-22s %-12s %6d%n",
                    formatLabel(parts[0]), parts[1], entry.getValue()));
        }
        return text.toString();
    }

    public static String findMostCommonCategory(Map<String, Integer> results) {
        if (results == null || results.isEmpty()) {
            return "N/A";
        }

        String bestKey = "N/A";
        int bestValue = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            if (entry.getValue() > bestValue) {
                bestValue = entry.getValue();
                bestKey = entry.getKey();
            }
        }
        return formatLabel(bestKey);
    }

    private static void appendSection(StringBuilder report, String title, Map<String, Integer> results) {
        report.append("--- ").append(title).append(" ---\n");
        if (results == null || results.isEmpty()) {
            report.append("  (no data)\n\n");
            return;
        }
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            report.append(String.format("  %-24s : %d%n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");
    }

    private static void appendTrendSection(StringBuilder report, Map<String, Map<String, Integer>> trendResults) {
        report.append("--- Sentiment Trend Analysis ---\n");
        if (trendResults == null || trendResults.isEmpty()) {
            report.append("  (no data)\n\n");
            return;
        }

        for (Map.Entry<String, Map<String, Integer>> dayEntry : trendResults.entrySet()) {
            report.append("  Date: ").append(dayEntry.getKey()).append("\n");
            for (Map.Entry<String, Integer> sentimentEntry : dayEntry.getValue().entrySet()) {
                report.append(String.format("    %-12s : %d%n",
                        sentimentEntry.getKey(), sentimentEntry.getValue()));
            }
            report.append("\n");
        }
    }

    private static String[] splitReliefKey(String key) {
        int index = key.lastIndexOf('_');
        if (index <= 0) {
            return new String[]{key, ""};
        }
        return new String[]{key.substring(0, index), key.substring(index + 1)};
    }

    static String formatLabel(String key) {
        return key.replace('_', ' ');
    }
}
