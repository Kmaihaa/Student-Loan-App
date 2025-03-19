package com.example.student_loan_app;

public class ExpenseItem {
    private Expense expense;
    private String docId;  // Firestore document id

    public ExpenseItem() { }

    public ExpenseItem(Expense expense, String docId) {
        this.expense = expense;
        this.docId = docId;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
