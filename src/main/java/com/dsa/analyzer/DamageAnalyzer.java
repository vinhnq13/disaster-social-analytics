package com.dsa.analyzer;

import com.dsa.model.Post;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DamageAnalyzer implements Analyzer {

    private static final String DEFAULT_CATEGORY = "other";

    private final Map<String, List<String>> categoryKeywords;

    public DamageAnalyzer() {
        categoryKeywords = new LinkedHashMap<>();
        categoryKeywords.put("affected_people", List.of(
                "người dân", "bị thương", "mắc kẹt", "sơ tán", "mất tích", "nạn nhân", "hộ dân"
        ));
        categoryKeywords.put("economic_disruption", List.of(
                "kinh doanh", "sản xuất", "chợ", "cửa hàng", "việc làm", "thu nhập", "gián đoạn"
        ));
        categoryKeywords.put("housing_damage", List.of(
                "nhà", "mái", "sập", "tốc mái", "tường", "ngập nhà", "hư hỏng nhà"
        ));
        categoryKeywords.put("personal_asset_loss", List.of(
                "tài sản", "xe máy", "đồ đạc", "vật dụng", "mất trắng", "gia súc", "hoa màu"
        ));
        categoryKeywords.put("infrastructure_damage", List.of(
                "đường", "cầu", "điện", "nước", "trường học", "bệnh viện", "viễn thông", "sạt lở"
        ));
        categoryKeywords.put(DEFAULT_CATEGORY, List.of());
    }

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String category : categoryKeywords.keySet()) {
            counts.put(category, 0);
        }

        for (Post post : posts) {
            String text = normalize(post.getContent());
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
