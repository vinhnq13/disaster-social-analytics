package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SentimentTrendAnalyzer {

    private static final String DEFAULT_SENTIMENT = "neutral";

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> sentimentKeywords;

    public SentimentTrendAnalyzer(TextPreprocessor textPreprocessor, Map<String, List<String>> sentimentKeywords) {
        this.textPreprocessor = textPreprocessor;
        this.sentimentKeywords = new LinkedHashMap<>(sentimentKeywords);
    }

    public Map<String, Map<String, Integer>> analyze(List<Post> posts) {
        Map<String, Map<String, Integer>> trendByDate = new TreeMap<>();

        for (Post post : posts) {
            String date = normalizeDate(post.getDate());
            Map<String, Integer> dayCounts = trendByDate.computeIfAbsent(date, d -> createEmptyDayCounts());

            String sentiment = classifySentiment(textPreprocessor.clean(post.getContent()));
            dayCounts.merge(sentiment, 1, Integer::sum);
        }

        return trendByDate;
    }

    private Map<String, Integer> createEmptyDayCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String sentiment : sentimentKeywords.keySet()) {
            counts.put(sentiment, 0);
        }
        return counts;
    }

    private String classifySentiment(String text) {
        for (Map.Entry<String, List<String>> entry : sentimentKeywords.entrySet()) {
            if (containsAnyKeyword(text, entry.getValue())) {
                return entry.getKey();
            }
        }
        return DEFAULT_SENTIMENT;
    }

    private boolean containsAnyKeyword(String text, List<String> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.contains(textPreprocessor.clean(keyword))) {
                return true;
            }
        }
        return false;
    }

    private String normalizeDate(String date) {
        if (date == null || date.isBlank()) {
            return "unknown";
        }
        return date.trim();
    }
}
