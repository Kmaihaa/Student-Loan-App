package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class BudgetExpenseActivity extends AppCompatActivity {

    private static final String TAG = "BudgetExpenseActivity";

    // UI Views
    private TextView textViewMonthlyIncome, textViewLoanRepaymentSuggestion, textViewExpenseSuggestion;
    private PieChart pieChartBudget;
    private EditText editTextExpenseName, editTextExpenseAmount;
    private Button buttonAddExpense;
    private RecyclerView recyclerViewExpenses;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // RecyclerView Adapter
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList = new ArrayList<>();

    // User Profile
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_expense);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Bind UI Views
        textViewMonthlyIncome = findViewById(R.id.textViewMonthlyIncome);
        textViewLoanRepaymentSuggestion = findViewById(R.id.textViewLoanRepaymentSuggestion);
        textViewExpenseSuggestion = findViewById(R.id.textViewExpenseSuggestion);
        pieChartBudget = findViewById(R.id.pieChartBudget);
        editTextExpenseName = findViewById(R.id.editTextExpenseName);
        editTextExpenseAmount = findViewById(R.id.editTextExpenseAmount);
        buttonAddExpense = findViewById(R.id.buttonAddExpense);
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);

        // Set up RecyclerView
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        expenseAdapter = new ExpenseAdapter(expenseList);
        recyclerViewExpenses.setAdapter(expenseAdapter);

        // Set up Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(BudgetExpenseActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    // TODO: start Repayment Planner Activity
                    return true;
                } else if (id == R.id.navigation_budget) {
                    // Already on Budget page
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    // TODO: start Notifications Activity
                    return true;
                } else if (id == R.id.navigation_profile) {
                    startActivity(new Intent(BudgetExpenseActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        // Load user profile and expenses
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            loadProfileData(currentUser.getUid());
            loadExpenses(currentUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Set listener for adding an expense
        buttonAddExpense.setOnClickListener(new View.OnClickListener(){
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

    // Load profile data from Firestore
    private void loadProfileData(String uid) {
        DocumentReference docRef = db.collection("profile").document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userProfile = documentSnapshot.toObject(Profile.class);
                if (userProfile != null) {
                    updateBudgetUI(userProfile);
                }
            } else {
                Log.d(TAG, "No profile document found.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error loading profile data", e);
        });
    }

    // Update UI elements and PieChart based on profile data
    private void updateBudgetUI(Profile profile) {
        double monthlyIncome = profile.getMonthlyIncome();
        double interestRate = profile.getInterestRate();

        // Real-world logic:
        // If interest rate > 5%, allocate 15% of income for loan repayment; otherwise, 10%.
        double loanRepaymentSuggestion = (interestRate > 5.0) ? monthlyIncome * 0.15 : monthlyIncome * 0.10;
        double expenseSuggestion = monthlyIncome * 0.50;
        double savings = monthlyIncome - (loanRepaymentSuggestion + expenseSuggestion);

        // Update TextViews with formatted values
        textViewMonthlyIncome.setText("Monthly Income: $" + String.format("%.2f", monthlyIncome));
        textViewLoanRepaymentSuggestion.setText("Recommended Loan Payment: $" + String.format("%.2f", loanRepaymentSuggestion));
        textViewExpenseSuggestion.setText("Suggested Expense Budget: $" + String.format("%.2f", expenseSuggestion));

        // Set up entries for the PieChart
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) loanRepaymentSuggestion, "Loan Repayment"));
        entries.add(new PieEntry((float) expenseSuggestion, "Expenses"));
        entries.add(new PieEntry((float) savings, "Savings/Discretionary"));

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

    // Add an expense to Firestore
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
                    loadExpenses(uid);  // Refresh the list after adding
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BudgetExpenseActivity.this, "Error adding expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Load all expenses from Firestore for the current user
    private void loadExpenses(String uid) {
        CollectionReference expensesRef = db.collection("profile").document(uid).collection("expenses");
        expensesRef.orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    expenseList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Expense expense = doc.toObject(Expense.class);
                        expenseList.add(expense);
                    }
                    expenseAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading expenses", e);
                });
    }
}
