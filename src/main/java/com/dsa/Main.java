package com.dsa;

import com.dsa.analyzer.Analyzer;
import com.dsa.analyzer.DamageAnalyzer;
import com.dsa.analyzer.ReliefSatisfactionAnalyzer;
import com.dsa.analyzer.SentimentAnalyzer;
import com.dsa.analyzer.SentimentTrendAnalyzer;
import com.dsa.model.Post;
import com.dsa.service.DataService;

import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== Disaster Social Media Analytics ===\n");

        DataService dataService = new DataService();
        List<Post> posts = dataService.loadPosts();

        if (posts.isEmpty()) {
            System.out.println("No posts loaded. Exiting.");
            return;
        }

        System.out.println("Loaded " + posts.size() + " posts.\n");

        Analyzer damageAnalyzer = new DamageAnalyzer();
        Analyzer sentimentAnalyzer = new SentimentAnalyzer();
        Analyzer reliefSatisfactionAnalyzer = new ReliefSatisfactionAnalyzer();

        printResults("Damage Analysis", damageAnalyzer.analyze(posts));
        printResults("Sentiment Analysis", sentimentAnalyzer.analyze(posts));

        SentimentTrendAnalyzer sentimentTrendAnalyzer = new SentimentTrendAnalyzer();
        printSentimentTrendResults(sentimentTrendAnalyzer.analyze(posts));

        printResults("Relief Satisfaction Analysis", reliefSatisfactionAnalyzer.analyze(posts));
    }

    private static void printSentimentTrendResults(Map<String, Map<String, Integer>> trendByDate) {
        System.out.println("--- Sentiment Trend Analysis ---");
        if (trendByDate.isEmpty()) {
            System.out.println("  No dated posts to analyze.");
            System.out.println();
            return;
        }

        for (Map.Entry<String, Map<String, Integer>> dayEntry : trendByDate.entrySet()) {
            System.out.println("  Date: " + dayEntry.getKey());
            for (Map.Entry<String, Integer> sentimentEntry : dayEntry.getValue().entrySet()) {
                System.out.printf("    %-12s : %d%n", sentimentEntry.getKey(), sentimentEntry.getValue());
            }
            System.out.println();
        }
    }

    private static void printResults(String title, Map<String, Integer> results) {
        System.out.println("--- " + title + " ---");
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            System.out.printf("  %-24s : %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }
}
