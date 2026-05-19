package com.dsa.preprocess;

import java.util.Locale;
import java.util.regex.Pattern;

public class BasicTextPreprocessor implements TextPreprocessor {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "(https?://\\S+|www\\.\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern HASHTAG_SYMBOL_PATTERN = Pattern.compile("#");
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern EXTRA_SPACES_PATTERN = Pattern.compile("\\s+");

    @Override
    public String clean(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }

        String cleaned = text.toLowerCase(Locale.ROOT);
        cleaned = URL_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = HASHTAG_SYMBOL_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = PUNCTUATION_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = EXTRA_SPACES_PATTERN.matcher(cleaned).replaceAll(" ");
        return cleaned.trim();
    }
}
