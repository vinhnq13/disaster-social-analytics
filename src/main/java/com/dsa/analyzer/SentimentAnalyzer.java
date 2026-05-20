package com.dsa.analyzer;

import com.dsa.model.Post;
import com.dsa.sentiment.SentimentModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SentimentAnalyzer implements Analyzer {

    private final SentimentModel sentimentModel;

    public SentimentAnalyzer(SentimentModel sentimentModel) {
        this.sentimentModel = sentimentModel;
    }

    @Override
    public Map<String, Integer> analyze(List<Post> posts) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String sentiment : sentimentModel.getSentiments()) {
            counts.put(sentiment, 0);
        }

        for (Post post : posts) {
            String sentiment = sentimentModel.classify(post.getContent());
            counts.merge(sentiment, 1, Integer::sum);
        }

        return counts;
    }
}
