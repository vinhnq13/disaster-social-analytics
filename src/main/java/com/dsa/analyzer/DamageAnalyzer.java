package com.dsa.analyzer;

import com.dsa.model.Post;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DamageAnalyzer implements Analyzer {

    private final Map<String, List<String>> categoryKeywords;

    public DamageAnalyzer() {
        categoryKeywords = new LinkedHashMap<>();
        categoryKeywords.put("housing", List.of(
                "nhà", "mái", "tường", "ngập nhà", "sập nhà", "đổ nhà", "dân cư"
        ));
        categoryKeywords.put("transport", List.of(
                "đường", "cầu", "giao thông", "xe", "sạt lở", "tắc", "phương tiện"
        ));
        categoryKeywords.put("human", List.of(
                "người", "thương vong", "thiệt hại người", "mất tích", "cứu hộ", "dân"
        ));
        categoryKeywords.put("infrastructure", List.of(
                "điện", "nước", "trạm", "cống", "hạ tầng", "trường", "bệnh viện", "cột điện"
        ));
        categoryKeywords.put("other", List.of(
                "thiệt hại", "hư hỏng", "đổ", "gãy", "vỡ"
        ));
    }

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String category : categoryKeywords.keySet()) {
            counts.put(category, 0);
        }

        for (Post post : posts) {
            String text = normalize(post.getContent());
            String matchedCategory = findCategory(text);
            counts.merge(matchedCategory, 1, Integer::sum);
        }

        return counts;
    }

    private String findCategory(String text) {
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            if (containsAnyKeyword(text, entry.getValue())) {
                return entry.getKey();
            }
        }
        return "other";
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
