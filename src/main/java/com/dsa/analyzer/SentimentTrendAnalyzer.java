package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.sentiment.SentimentModel;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class SentimentTrendAnalyzer {

    private final SentimentModel sentimentModel;

    public SentimentTrendAnalyzer(SentimentModel sentimentModel) {
        this.sentimentModel = sentimentModel;
    }

    public Map<String, Map<String, Integer>> analyze(List<Post> posts) {
        Map<String, Map<String, Integer>> trendByDate = new TreeMap<>();

        for (Post post : posts) {
            String date = normalizeDate(post.getDate());
            Map<String, Integer> dayCounts = trendByDate.computeIfAbsent(date, d -> createEmptyDayCounts());

            String sentiment = sentimentModel.classify(post.getContent());
            dayCounts.merge(sentiment, 1, Integer::sum);
        }

        return trendByDate;
    }

    private Map<String, Integer> createEmptyDayCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String sentiment : sentimentModel.getSentiments()) {
            counts.put(sentiment, 0);
        }
        return counts;
    }

    private String normalizeDate(String date) {
        if (date == null || date.isBlank()) {
            return "unknown";
        }
        return date.trim();
    }
}
