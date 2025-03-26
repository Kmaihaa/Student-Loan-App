package com.example.student_loan_app;

public class Expense {
    private String name;
    private double amount;
    private long chargeDate;
    private long timestamp;

    // Required empty constructor for Firestore
    public Expense() { }

    public Expense(String name, double amount, long chargeDate) {
        this.name = name;
        this.amount = amount;
        this.chargeDate = chargeDate;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public long getChargeDate() { return chargeDate; }
    public void setChargeDate(long chargeDate) { this.chargeDate = chargeDate; }

}
