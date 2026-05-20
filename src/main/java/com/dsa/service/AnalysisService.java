package com.dsa.service;

import com.dsa.analyzer.DamageAnalyzer;
import com.dsa.analyzer.ReliefSatisfactionAnalyzer;
import com.dsa.analyzer.SentimentAnalyzer;
import com.dsa.analyzer.SentimentTrendAnalyzer;
import com.dsa.config.KeywordConfigLoader;
import com.dsa.model.Post;
import com.dsa.preprocess.BasicTextPreprocessor;
import com.dsa.preprocess.TextPreprocessor;
import com.dsa.sentiment.KeywordSentimentModel;
import com.dsa.sentiment.SentimentModel;

import java.util.List;
import java.util.Map;

public class AnalysisService {

    private final DamageAnalyzer damageAnalyzer;
    private final SentimentAnalyzer sentimentAnalyzer;
    private final SentimentTrendAnalyzer sentimentTrendAnalyzer;
    private final ReliefSatisfactionAnalyzer reliefSatisfactionAnalyzer;

    public AnalysisService(
            DamageAnalyzer damageAnalyzer,
            SentimentAnalyzer sentimentAnalyzer,
            SentimentTrendAnalyzer sentimentTrendAnalyzer,
            ReliefSatisfactionAnalyzer reliefSatisfactionAnalyzer) {
        this.damageAnalyzer = damageAnalyzer;
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.sentimentTrendAnalyzer = sentimentTrendAnalyzer;
        this.reliefSatisfactionAnalyzer = reliefSatisfactionAnalyzer;
    }

    public static AnalysisService createDefault() {
        TextPreprocessor textPreprocessor = new BasicTextPreprocessor();
        KeywordConfigLoader keywordConfigLoader = new KeywordConfigLoader();

        Map<String, List<String>> damageKeywords = keywordConfigLoader.loadDamageKeywords();
        Map<String, List<String>> sentimentKeywords = keywordConfigLoader.loadSentimentKeywords();
        Map<String, List<String>> reliefKeywords = keywordConfigLoader.loadReliefKeywords();

        SentimentModel sentimentModel = new KeywordSentimentModel(textPreprocessor, sentimentKeywords);

        DamageAnalyzer damageAnalyzer = new DamageAnalyzer(textPreprocessor, damageKeywords);
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sentimentModel);
        SentimentTrendAnalyzer sentimentTrendAnalyzer = new SentimentTrendAnalyzer(sentimentModel);
        ReliefSatisfactionAnalyzer reliefSatisfactionAnalyzer = new ReliefSatisfactionAnalyzer(
                textPreprocessor, reliefKeywords, sentimentModel);

        return new AnalysisService(
                damageAnalyzer, sentimentAnalyzer, sentimentTrendAnalyzer, reliefSatisfactionAnalyzer);
    }

    public Map<String, Integer> analyzeDamage(List<Post> posts) {
        return damageAnalyzer.analyze(posts);
    }

    public Map<String, Integer> analyzeSentiment(List<Post> posts) {
        return sentimentAnalyzer.analyze(posts);
    }

    public Map<String, Map<String, Integer>> analyzeSentimentTrend(List<Post> posts) {
        return sentimentTrendAnalyzer.analyze(posts);
    }

    public Map<String, Integer> analyzeReliefSatisfaction(List<Post> posts) {
        return reliefSatisfactionAnalyzer.analyze(posts);
    }
}
