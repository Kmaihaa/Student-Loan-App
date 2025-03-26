package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class BudgetExpenseActivity extends AppCompatActivity {

    private static final String TAG = "BudgetExpenseActivity";

    // UI Views
    private TextView textViewMonthlyIncome, textViewLoanRepaymentSuggestion, textViewExpenseSuggestion;
    private PieChart pieChartBudget;
    private EditText editTextExpenseName, editTextExpenseAmount;
    private EditText editTextChargeDate;
    private RecyclerView recyclerViewExpenses;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Adapter and expense list with document IDs
    private ExpenseAdapter expenseAdapter;
    private List<ExpenseItem> expenseItemList = new ArrayList<>();

    private Profile userProfile;

    private long selectedChargeDate = 0;

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
        editTextChargeDate = findViewById(R.id.editTextChargeDate);
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);

        // Set up RecyclerView
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

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(BudgetExpenseActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    startActivity(new Intent(BudgetExpenseActivity.this, RepaymentMenu.class));
                    return true;
                } else if (id == R.id.navigation_budget) {
                    // We are already here
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    startActivity(new Intent(BudgetExpenseActivity.this, NotificationsActivity.class));
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

        editTextChargeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BudgetExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year1, int monthOfYear, int dayOfMonth) {
                                calendar.set(year1, monthOfYear, dayOfMonth);
                                selectedChargeDate = calendar.getTimeInMillis();
                                editTextChargeDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        findViewById(R.id.buttonAddExpense).setOnClickListener(new View.OnClickListener() {
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

    private void listenToExpenses(String uid) {
        CollectionReference expensesRef = db.collection("profile").document(uid).collection("expenses");
        expensesRef.orderBy("chargeDate").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                    updateBudgetUI();
                }
            }
        });
    }

    // Update the UI using profile data and expense totals
    private void updateBudgetUI() {

        FirebaseUser currentUser = auth.getCurrentUser();

        if (userProfile == null) return;

        double monthlyIncome = userProfile.getMonthlyIncome();
        double interestRate = userProfile.getInterestRate();

        // Calculate loan repayment suggestion
        double loanRepaymentSuggestion = (interestRate > 5.0) ? monthlyIncome * 0.15 : monthlyIncome * 0.10;

        db.collection("profile").document(currentUser.getUid())
                .update("recommendedRepayment", loanRepaymentSuggestion);
        double totalExpenses = 0.0;
        for (ExpenseItem item : expenseItemList) {
            totalExpenses += item.getExpense().getAmount();
        }

        // Calculate remaining savings
        double savings = monthlyIncome - (loanRepaymentSuggestion + totalExpenses);

        textViewMonthlyIncome.setText("Monthly Income: $" + String.format("%.2f", monthlyIncome));
        textViewLoanRepaymentSuggestion.setText("Recommended Loan Payment: $" + String.format("%.2f", loanRepaymentSuggestion));
        textViewExpenseSuggestion.setText("Total Expenses: $" + String.format("%.2f", totalExpenses) +
                "\nRemaining Savings: $" + String.format("%.2f", savings));

        getSharedPreferences("loanRepayment", MODE_PRIVATE)
                .edit()
                .putFloat("recommendedRepayment", (float) loanRepaymentSuggestion)
                .apply();

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

    private void addExpense(String uid) {
        String expenseName = editTextExpenseName.getText().toString().trim();
        String expenseAmountStr = editTextExpenseAmount.getText().toString().trim();

        if (expenseName.isEmpty() || expenseAmountStr.isEmpty() || selectedChargeDate == 0) {
            Toast.makeText(this, "Please fill in all expense fields including charge date", Toast.LENGTH_SHORT).show();
            return;
        }

        double expenseAmount = Double.parseDouble(expenseAmountStr);
        Expense expense = new Expense(expenseName, expenseAmount, selectedChargeDate);

        db.collection("profile").document(uid)
                .collection("expenses")
                .add(expense)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                    // Schedule expense notification using AlarmManager
                    NotificationScheduler.scheduleExpenseNotification(
                            BudgetExpenseActivity.this,
                            expense,
                            documentReference.getId()
                    );
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Error adding expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

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

    private void showEditExpenseDialog(ExpenseItem expenseItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Expense");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_edit_expense, null);
        final EditText inputName = viewInflated.findViewById(R.id.editTextDialogExpenseName);
        final EditText inputAmount = viewInflated.findViewById(R.id.editTextDialogExpenseAmount);
        final EditText inputChargeDate = viewInflated.findViewById(R.id.editTextDialogChargeDate);

        inputName.setText(expenseItem.getExpense().getName());
        inputAmount.setText(String.valueOf(expenseItem.getExpense().getAmount()));

        long currentChargeDate = expenseItem.getExpense().getChargeDate();
        if (currentChargeDate != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
            inputChargeDate.setText(sdf.format(currentChargeDate));
        }

        inputChargeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int year, month, day;
                SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                if (!inputChargeDate.getText().toString().isEmpty()) {
                    try {
                        calendar.setTime(sdf.parse(inputChargeDate.getText().toString()));
                    } catch (Exception e) {

                    }
                }
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        BudgetExpenseActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year1, int monthOfYear, int dayOfMonth) {
                                calendar.set(year1, monthOfYear, dayOfMonth);
                                inputChargeDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1);
                            }
                        },
                        year,
                        month,
                        day
                );
                datePickerDialog.show();
            }
        });

        builder.setView(viewInflated);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = inputName.getText().toString().trim();
            String newAmountStr = inputAmount.getText().toString().trim();
            String newChargeDateStr = inputChargeDate.getText().toString().trim();

            if (newName.isEmpty() || newAmountStr.isEmpty() || newChargeDateStr.isEmpty()) {
                Toast.makeText(BudgetExpenseActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            double newAmount = Double.parseDouble(newAmountStr);
            long newChargeDate = 0;
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());
                newChargeDate = sdf.parse(newChargeDateStr).getTime();
            } catch (Exception e) {
                Toast.makeText(BudgetExpenseActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
                return;
            }
            updateExpense(expenseItem, newName, newAmount, newChargeDate);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateExpense(ExpenseItem expenseItem, String newName, double newAmount, long newChargeDate) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        DocumentReference expenseDoc = db.collection("profile").document(user.getUid())
                .collection("expenses").document(expenseItem.getDocId());
        expenseDoc.update("name", newName, "amount", newAmount, "chargeDate", newChargeDate)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Expense updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Error updating expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
