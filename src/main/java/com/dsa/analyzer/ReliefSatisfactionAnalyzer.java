package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.preprocess.TextPreprocessor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReliefSatisfactionAnalyzer implements Analyzer {

    private static final List<String> RELIEF_CATEGORIES = List.of(
            "food", "medical", "cash", "housing", "transport"
    );

    private static final List<String> SENTIMENTS = List.of(
            "positive", "negative", "neutral"
    );

    private final TextPreprocessor textPreprocessor;
    private final Map<String, List<String>> reliefKeywords;
    private final Map<String, List<String>> sentimentKeywords;

    public ReliefSatisfactionAnalyzer(TextPreprocessor textPreprocessor) {
        this.textPreprocessor = textPreprocessor;
        reliefKeywords = new LinkedHashMap<>();
        reliefKeywords.put("food", List.of(
                "lương thực", "gạo", "mì", "thực phẩm", "suất ăn", "đồ ăn", "nước uống", "nước sạch"
        ));
        reliefKeywords.put("medical", List.of(
                "y tế", "bệnh viện", "thuốc", "khám", "sơ cứu", "tiêm", "bác sĩ", "y tá"
        ));
        reliefKeywords.put("cash", List.of(
                "tiền mặt", "tiền", "chi hỗ trợ", "tài khoản", "quyên góp tiền", "nhận tiền"
        ));
        reliefKeywords.put("housing", List.of(
                "nhà", "mái nhà", "ngói", "tôn", "vá nhà", "sửa nhà", "chỗ ở", "tái thiết"
        ));
        reliefKeywords.put("transport", List.of(
                "xe cứu trợ", "xe chở", "phương tiện", "đường vào", "cầu", "giao thông cứu trợ"
        ));

        sentimentKeywords = new LinkedHashMap<>();
        sentimentKeywords.put("positive", List.of(
                "cảm ơn", "hài lòng", "kịp thời", "đủ", "tốt", "hỗ trợ", "may mắn", "ổn", "đoàn kết"
        ));
        sentimentKeywords.put("negative", List.of(
                "thiếu", "chậm", "khó khăn", "không đủ", "phàn nàn", "tuyệt vọng", "lo lắng", "sợ"
        ));
        sentimentKeywords.put("neutral", List.of(
                "báo cáo", "thông tin", "cập nhật", "phát", "nhận", "danh sách", "theo dõi"
        ));
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
        for (String category : RELIEF_CATEGORIES) {
            for (String sentiment : SENTIMENTS) {
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
}
