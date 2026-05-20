package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SentimentAnalyzer implements Analyzer {

    private static final String DEFAULT_SENTIMENT = "neutral";

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> sentimentKeywords;

    public SentimentAnalyzer(TextPreprocessor textPreprocessor, Map<String, List<String>> sentimentKeywords) {
        this.textPreprocessor = textPreprocessor;
        this.sentimentKeywords = new LinkedHashMap<>(sentimentKeywords);
    }

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String sentiment : sentimentKeywords.keySet()) {
            counts.put(sentiment, 0);
        }

        for (Post post : posts) {
            String sentiment = classifySentiment(textPreprocessor.clean(post.getContent()));
            counts.merge(sentiment, 1, Integer::sum);
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
}
