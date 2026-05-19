package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SentimentTrendAnalyzer {

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> sentimentKeywords;

    public SentimentTrendAnalyzer(TextPreprocessor textPreprocessor) {
        this.textPreprocessor = textPreprocessor;
        sentimentKeywords = new LinkedHashMap<>();
        sentimentKeywords.put("positive", List.of(
                "cứu trợ", "an toàn", "đoàn kết", "hỗ trợ", "may mắn", "cảm ơn", "hy vọng", "ổn"
        ));
        sentimentKeywords.put("negative", List.of(
                "sợ", "lo lắng", "đau khổ", "mất mát", "tuyệt vọng", "hoảng loạn", "khó khăn", "thiệt hại"
        ));
        sentimentKeywords.put("neutral", List.of(
                "báo cáo", "thông tin", "cập nhật", "theo dõi", "dự báo"
        ));
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
        counts.put("positive", 0);
        counts.put("negative", 0);
        counts.put("neutral", 0);
        return counts;
    }

    private String classifySentiment(String text) {
        if (containsAnyKeyword(text, sentimentKeywords.get("positive"))) {
            return "positive";
        }
        if (containsAnyKeyword(text, sentimentKeywords.get("negative"))) {
            return "negative";
        }
        if (containsAnyKeyword(text, sentimentKeywords.get("neutral"))) {
            return "neutral";
        }
        return "neutral";
    }

    private boolean containsAnyKeyword(String text, List<String> keywords) {
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
