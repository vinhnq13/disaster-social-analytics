package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReliefSatisfactionAnalyzer implements Analyzer {

    private static final String DEFAULT_SENTIMENT = "neutral";

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> reliefKeywords;
    private final Map<String, List<String>> sentimentKeywords;

    public ReliefSatisfactionAnalyzer(
            TextPreprocessor textPreprocessor,
            Map<String, List<String>> reliefKeywords,
            Map<String, List<String>> sentimentKeywords) {
        this.textPreprocessor = textPreprocessor;
        this.reliefKeywords = new LinkedHashMap<>(reliefKeywords);
        this.sentimentKeywords = new LinkedHashMap<>(sentimentKeywords);
    }

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = createEmptyCounts();

        for (Post post : posts) {
            String text = textPreprocessor.clean(post.getContent());
            List<String> matchedCategories = findMatchedCategories(text);

            if (matchedCategories.isEmpty()) {
                continue;
            }

            String sentiment = classifySentiment(text);
            for (String category : matchedCategories) {
                String key = category + "_" + sentiment;
                counts.merge(key, 1, Integer::sum);
            }
        }

        return counts;
    }

    private Map<String, Integer> createEmptyCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String category : reliefKeywords.keySet()) {
            for (String sentiment : sentimentKeywords.keySet()) {
                counts.put(category + "_" + sentiment, 0);
            }
        }
        return counts;
    }

    private List<String> findMatchedCategories(String text) {
        List<String> matched = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : reliefKeywords.entrySet()) {
            if (containsAnyKeyword(text, entry.getValue())) {
                matched.add(entry.getKey());
            }
        }
        return matched;
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
}
