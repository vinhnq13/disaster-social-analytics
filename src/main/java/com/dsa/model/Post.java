package com.dsa.model;

public class Post {

    private String content;
    private String date;
    private String source;

    public Post() {
    }

    public Post(String content, String date, String source) {
        this.content = content;
        this.date = date;
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "Post{"
                + "date='" + date + '\''
                + ", source='" + source + '\''
                + ", content='" + content + '\''
                + '}';
    }
}
