package com.dsa;

import com.dsa.analyzer.Analyzer;
import com.dsa.analyzer.DamageAnalyzer;
import com.dsa.analyzer.SentimentAnalyzer;
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

        printResults("Damage Analysis", damageAnalyzer.analyze(posts));
        printResults("Sentiment Analysis", sentimentAnalyzer.analyze(posts));
    }

    private static void printResults(String title, Map<String, Integer> results) {
        System.out.println("--- " + title + " ---");
        for (Map.Entry<String, Integer> entry : results.entrySet()) {
            System.out.printf("  %-16s : %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println();
    }
}
