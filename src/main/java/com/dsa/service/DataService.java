package com.dsa.service;

import com.dsa.model.Post;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataService {

    private static final String POSTS_RESOURCE = "/data/posts.json";

    private final Gson gson;

    public DataService() {
        this.gson = new Gson();
    }

    public List<Post> loadPosts() {
        try (InputStream inputStream = getClass().getResourceAsStream(POSTS_RESOURCE)) {
            if (inputStream == null) {
                System.err.println("Error: Could not find posts file at " + POSTS_RESOURCE);
                return new ArrayList<>();
            }

            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                Type listType = new TypeToken<List<Post>>() {
                }.getType();
                List<Post> posts = gson.fromJson(reader, listType);

                if (posts == null) {
                    System.err.println("Error: posts.json is empty or invalid.");
                    return new ArrayList<>();
                }

                return posts;
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Error: Failed to parse posts.json. Check JSON format.");
            System.err.println("Details: " + e.getMessage());
            return new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error: Could not read posts.json.");
            System.err.println("Details: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
