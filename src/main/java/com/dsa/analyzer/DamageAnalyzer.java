package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DamageAnalyzer implements Analyzer {

    private static final String DEFAULT_CATEGORY = "other";

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> categoryKeywords;

    public DamageAnalyzer(TextPreprocessor textPreprocessor, Map<String, List<String>> damageKeywords) {
        this.textPreprocessor = textPreprocessor;
        this.categoryKeywords = new LinkedHashMap<>(damageKeywords);
    }

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String category : categoryKeywords.keySet()) {
            counts.put(category, 0);
        }

        for (Post post : posts) {
            String text = textPreprocessor.clean(post.getContent());
            String matchedCategory = findBestCategory(text);
            counts.merge(matchedCategory, 1, Integer::sum);
        }

        return counts;
    }

    private String findBestCategory(String text) {
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            if (DEFAULT_CATEGORY.equals(entry.getKey())) {
                continue;
            }
            if (containsAnyKeyword(text, entry.getValue())) {
                return entry.getKey();
            }
        }
        return DEFAULT_CATEGORY;
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
