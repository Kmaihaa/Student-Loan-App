package com.example.student_loan_app;

public class Expense {
    private String name;
    private double amount;
    private long timestamp;

    // Required empty constructor for Firestore
    public Expense() {}

    public Expense(String name, double amount, long timestamp) {
        this.name = name;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
