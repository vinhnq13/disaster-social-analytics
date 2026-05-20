package com.dsa.sentiment;

import com.dsa.preprocess.TextPreprocessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeywordSentimentModel implements SentimentModel {

    private static final String DEFAULT_SENTIMENT = "neutral";

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> sentimentKeywords;

    public KeywordSentimentModel(TextPreprocessor textPreprocessor, Map<String, List<String>> sentimentKeywords) {
        this.textPreprocessor = textPreprocessor;
        this.sentimentKeywords = new LinkedHashMap<>(sentimentKeywords);
    }

    @Override
    public String classify(String text) {
        String cleanedText = textPreprocessor.clean(text);

        for (Map.Entry<String, List<String>> entry : sentimentKeywords.entrySet()) {
            if (containsAnyKeyword(cleanedText, entry.getValue())) {
                return entry.getKey();
            }
        }
        return DEFAULT_SENTIMENT;
    }

    @Override
    public List<String> getSentiments() {
        return new ArrayList<>(sentimentKeywords.keySet());
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
