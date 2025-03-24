package com.example.student_loan_app;

public class Article {
    private String title;
    private String url;

    public Article(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}