package com.dsa.analyzer;

import com.dsa.model.Post;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SentimentAnalyzer implements Analyzer {

    private final Map<String, List<String>> sentimentKeywords;

    public SentimentAnalyzer() {
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

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("positive", 0);
        counts.put("negative", 0);
        counts.put("neutral", 0);

        for (Post post : posts) {
            String sentiment = classifySentiment(normalize(post.getContent()));
            counts.merge(sentiment, 1, Integer::sum);
        }

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
            if (text.contains(normalize(keyword))) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase().trim();
    }
}
