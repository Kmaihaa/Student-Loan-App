package com.example.student_loan_app;

public class Profile {
    private String name;
    private int age;
    private double loanAmount;
    private double monthlyIncome;
    private double interestRate;
    private double loanTerm;
    private String compoundFrequency;
    private String repaymentPlan;

    // Required empty constructor for Firebase
    public Profile() {
    }

    // Full constructor matching your submitProfile() parameter order
    public Profile(String name,
                   int age,
                   double loanAmount,
                   double monthlyIncome,
                   double interestRate,
                   double loanTerm,
                   String compoundFrequency,
                   String repaymentPlan) {
        this.name = name;
        this.age = age;
        this.loanAmount = loanAmount;
        this.monthlyIncome = monthlyIncome;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;
        this.compoundFrequency = compoundFrequency;
        this.repaymentPlan = repaymentPlan;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public double getLoanAmount() {
        return loanAmount;
    }
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }
    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public double getInterestRate() {
        return interestRate;
    }
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public double getLoanTerm() {
        return loanTerm;
    }
    public void setLoanTerm(double loanTerm) {
        this.loanTerm = loanTerm;
    }

    public String getCompoundFrequency() {
        return compoundFrequency;
    }
    public void setCompoundFrequency(String compoundFrequency) {
        this.compoundFrequency = compoundFrequency;
    }

    public String getRepaymentPlan() {
        return repaymentPlan;
    }
    public void setRepaymentPlan(String repaymentPlan) {
        this.repaymentPlan = repaymentPlan;
    }
}
