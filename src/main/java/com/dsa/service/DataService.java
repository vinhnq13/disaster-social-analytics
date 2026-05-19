package com.dsa.service;

import com.dsa.collector.DataCollector;
import com.dsa.collector.JsonDataCollector;
import com.dsa.model.Post;

import java.util.List;

public class DataService {

    private final DataCollector dataCollector;

    public DataService() {
        this(new JsonDataCollector());
    }

    public DataService(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
    }

    public List<Post> loadPosts() {
        return dataCollector.collect();
    }
}
