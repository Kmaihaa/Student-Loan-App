package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.Nullable;

public class BudgetExpenseActivity extends AppCompatActivity {

    private static final String TAG = "BudgetExpenseActivity";

    // UI Views
    private TextView textViewMonthlyIncome, textViewLoanRepaymentSuggestion, textViewExpenseSuggestion;
    private PieChart pieChartBudget;
    private EditText editTextExpenseName, editTextExpenseAmount;
    private RecyclerView recyclerViewExpenses;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Adapter and expense list with document IDs
    private ExpenseAdapter expenseAdapter;
    private List<ExpenseItem> expenseItemList = new ArrayList<>();

    // User profile
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_expense);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Bind UI views
        textViewMonthlyIncome = findViewById(R.id.textViewMonthlyIncome);
        textViewLoanRepaymentSuggestion = findViewById(R.id.textViewLoanRepaymentSuggestion);
        textViewExpenseSuggestion = findViewById(R.id.textViewExpenseSuggestion);
        pieChartBudget = findViewById(R.id.pieChartBudget);
        editTextExpenseName = findViewById(R.id.editTextExpenseName);
        editTextExpenseAmount = findViewById(R.id.editTextExpenseAmount);
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);

        // Setup RecyclerView
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(expenseItemList, new ExpenseAdapter.OnExpenseItemClickListener() {
            @Override
            public void onEditExpense(ExpenseItem expenseItem) {
                showEditExpenseDialog(expenseItem);
            }

            @Override
            public void onDeleteExpense(ExpenseItem expenseItem) {
                showDeleteExpenseDialog(expenseItem);
            }
        });
        recyclerViewExpenses.setAdapter(expenseAdapter);

        // Setup Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(BudgetExpenseActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    // TODO: Start Repayment Planner Activity
                    return true;
                } else if (id == R.id.navigation_budget) {
                    // Already here
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    // TODO: Start Notifications Activity
                    return true;
                } else if (id == R.id.navigation_profile) {
                    startActivity(new Intent(BudgetExpenseActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        // Load user profile and expense data
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            loadProfileData(currentUser.getUid());
            listenToExpenses(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Set listener for adding a new expense
        findViewById(R.id.buttonAddExpense).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(BudgetExpenseActivity.this, "Please log in", Toast.LENGTH_SHORT).show();
                    return;
                }
                addExpense(user.getUid());
            }
        });
    }

    // Load profile data from Firestore once
    private void loadProfileData(String uid) {
        DocumentReference docRef = db.collection("profile").document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userProfile = documentSnapshot.toObject(Profile.class);
                if (userProfile != null) {
                    updateBudgetUI();
                }
            } else {
                Log.d(TAG, "No profile document found.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading profile data", e);
        });
    }

    // Listen to expenses collection in real-time
    private void listenToExpenses(String uid) {
        CollectionReference expensesRef = db.collection("profile").document(uid).collection("expenses");
        expensesRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e(TAG, "Listen failed.", e);
                    return;
                }
                expenseItemList.clear();
                if (snapshots != null) {
                    for (var doc : snapshots.getDocuments()) {
                        Expense expense = doc.toObject(Expense.class);
                        String docId = doc.getId();
                        expenseItemList.add(new ExpenseItem(expense, docId));
                    }
                    expenseAdapter.notifyDataSetChanged();
                    // Update chart based on expense changes
                    updateBudgetUI();
                }
            }
        });
    }

    // Update the UI (TextViews and PieChart) using profile data and expense totals
    private void updateBudgetUI() {
        if (userProfile == null) return;

        double monthlyIncome = userProfile.getMonthlyIncome();
        double interestRate = userProfile.getInterestRate();

        // Calculate loan repayment suggestion (real-world logic)
        double loanRepaymentSuggestion = (interestRate > 5.0) ? monthlyIncome * 0.15 : monthlyIncome * 0.10;
        // Sum actual expenses from the expense list
        double totalExpenses = 0.0;
        for (ExpenseItem item : expenseItemList) {
            totalExpenses += item.getExpense().getAmount();
        }
        // Calculate remaining savings
        double savings = monthlyIncome - (loanRepaymentSuggestion + totalExpenses);

        // Update TextViews
        textViewMonthlyIncome.setText("Monthly Income: $" + String.format("%.2f", monthlyIncome));
        textViewLoanRepaymentSuggestion.setText("Recommended Loan Payment: $" + String.format("%.2f", loanRepaymentSuggestion));
        textViewExpenseSuggestion.setText("Total Expenses: $" + String.format("%.2f", totalExpenses) +
                "\nRemaining Savings: $" + String.format("%.2f", savings));

        // Update PieChart entries
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) loanRepaymentSuggestion, "Loan Repayment"));
        entries.add(new PieEntry((float) totalExpenses, "Expenses"));
        entries.add(new PieEntry((float) savings, "Savings"));

        PieDataSet dataSet = new PieDataSet(entries, "Budget Allocation");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChartBudget.setData(data);
        pieChartBudget.getDescription().setEnabled(false);
        pieChartBudget.setCenterText("Monthly Budget");
        pieChartBudget.animateY(1000);
        pieChartBudget.invalidate(); // refresh chart
    }

    // Add an expense (creates a new expense document)
    private void addExpense(String uid) {
        String expenseName = editTextExpenseName.getText().toString().trim();
        String expenseAmountStr = editTextExpenseAmount.getText().toString().trim();

        if (expenseName.isEmpty() || expenseAmountStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all expense fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double expenseAmount = Double.parseDouble(expenseAmountStr);
        Expense expense = new Expense(expenseName, expenseAmount, System.currentTimeMillis());

        db.collection("profile").document(uid)
                .collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                    // The snapshot listener will update the list and chart
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Error adding expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Show a confirmation dialog and delete the expense if confirmed
    private void showDeleteExpenseDialog(ExpenseItem expenseItem) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Expense")
                .setMessage("Are you sure you want to delete this expense?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    deleteExpense(expenseItem);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Delete the expense from Firestore
    private void deleteExpense(ExpenseItem expenseItem) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        db.collection("profile").document(user.getUid())
                .collection("expenses").document(expenseItem.getDocId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Expense deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Error deleting expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Show an edit dialog to update the expense details
    private void showEditExpenseDialog(ExpenseItem expenseItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Expense");

        // Create input fields for expense name and amount
        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_edit_expense, null);
        final EditText inputName = viewInflated.findViewById(R.id.editTextDialogExpenseName);
        final EditText inputAmount = viewInflated.findViewById(R.id.editTextDialogExpenseAmount);

        // Pre-fill with current values
        inputName.setText(expenseItem.getExpense().getName());
        inputAmount.setText(String.valueOf(expenseItem.getExpense().getAmount()));

        builder.setView(viewInflated);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            String newAmountStr = inputAmount.getText().toString().trim();
            if (newName.isEmpty() || newAmountStr.isEmpty()) {
                Toast.makeText(BudgetExpenseActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            double newAmount = Double.parseDouble(newAmountStr);
            updateExpense(expenseItem, newName, newAmount);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Update an expense document with new values
    private void updateExpense(ExpenseItem expenseItem, String newName, double newAmount) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        DocumentReference expenseDoc = db.collection("profile").document(user.getUid())
                .collection("expenses").document(expenseItem.getDocId());
        expenseDoc.update("name", newName, "amount", newAmount)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Expense updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Error updating expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
