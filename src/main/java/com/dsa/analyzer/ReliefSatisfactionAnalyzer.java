package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;
import com.dsa.sentiment.SentimentModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReliefSatisfactionAnalyzer implements Analyzer {

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> reliefKeywords;
    private final SentimentModel sentimentModel;

    public ReliefSatisfactionAnalyzer(
            TextPreprocessor textPreprocessor,
            Map<String, List<String>> reliefKeywords,
            SentimentModel sentimentModel) {
        this.textPreprocessor = textPreprocessor;
        this.reliefKeywords = new LinkedHashMap<>(reliefKeywords);
        this.sentimentModel = sentimentModel;
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

            String sentiment = sentimentModel.classify(post.getContent());
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
            for (String sentiment : sentimentModel.getSentiments()) {
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
