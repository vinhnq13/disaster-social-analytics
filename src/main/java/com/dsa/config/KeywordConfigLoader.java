package com.dsa.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeywordConfigLoader {

    private static final String DAMAGE_KEYWORDS_RESOURCE = "/config/damage-keywords.json";
    private static final String SENTIMENT_KEYWORDS_RESOURCE = "/config/sentiment-keywords.json";
    private static final String RELIEF_KEYWORDS_RESOURCE = "/config/relief-keywords.json";

    private final Gson gson;

    public KeywordConfigLoader() {
        this.gson = new Gson();
    }

    public Map<String, List<String>> loadDamageKeywords() {
        return loadKeywords(DAMAGE_KEYWORDS_RESOURCE, "damage-keywords.json");
    }

    public Map<String, List<String>> loadSentimentKeywords() {
        return loadKeywords(SENTIMENT_KEYWORDS_RESOURCE, "sentiment-keywords.json");
    }

    public Map<String, List<String>> loadReliefKeywords() {
        return loadKeywords(RELIEF_KEYWORDS_RESOURCE, "relief-keywords.json");
    }

    private Map<String, List<String>> loadKeywords(String resourcePath, String fileName) {
        try (InputStream inputStream = KeywordConfigLoader.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                System.err.println("Error: Could not find keyword config at " + resourcePath);
                return Collections.emptyMap();
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Type mapType = new TypeToken<Map<String, List<String>>>() {
                }.getType();
                Map<String, List<String>> keywords = gson.fromJson(reader, mapType);

                if (keywords == null) {
                    System.err.println("Error: " + fileName + " is empty or invalid.");
                    return Collections.emptyMap();
                }

                return new LinkedHashMap<>(keywords);
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Error: Failed to parse " + fileName + ". Check JSON format.");
            System.err.println("Details: " + e.getMessage());
            return Collections.emptyMap();
        } catch (IOException e) {
            System.err.println("Error: Could not read " + fileName + ".");
            System.err.println("Details: " + e.getMessage());
            return Collections.emptyMap();
        }
    }
}
