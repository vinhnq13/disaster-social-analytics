package com.dsa.collector;

import com.dsa.model.Post;

import java.util.List;

public interface DataCollector {

    List<Post> collect();
}
