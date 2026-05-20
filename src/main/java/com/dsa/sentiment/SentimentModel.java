package com.dsa.sentiment;

import java.util.List;

public interface SentimentModel {

    /**
     * Classifies the given text. Implementations may clean the text internally.
     */
    String classify(String text);

    /**
     * Returns sentiment labels used for counting (e.g. positive, negative, neutral).
     */
    List<String> getSentiments();
}
