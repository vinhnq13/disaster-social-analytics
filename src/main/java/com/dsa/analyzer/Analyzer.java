package com.dsa.analyzer;

import com.dsa.model.Post;

import java.util.List;
import java.util.Map;

public interface Analyzer {

    Map<String, Integer> analyze(List<Post> posts);
}
