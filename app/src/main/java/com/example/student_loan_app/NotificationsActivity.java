package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private TextView textViewLoanStats;
    private RecyclerView recyclerViewUpcomingExpenses;
    private BottomNavigationView bottomNavigationView;
    private Button buttonAddPayment;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private NotificationsAdapter notificationsAdapter;
    private List<ExpenseItem> upcomingExpenseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        textViewLoanStats = findViewById(R.id.textViewLoanStats);
        recyclerViewUpcomingExpenses = findViewById(R.id.recyclerViewUpcomingExpenses);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        buttonAddPayment = findViewById(R.id.buttonAddPayment);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up RecyclerView
        recyclerViewUpcomingExpenses.setLayoutManager(new LinearLayoutManager(this));
        notificationsAdapter = new NotificationsAdapter(upcomingExpenseList);
        recyclerViewUpcomingExpenses.setAdapter(notificationsAdapter);

        loadLoanStats();
        listenToUpcomingExpenses();
        setupBottomNavigation();
        setupAddPaymentButton();
    }

    // Load loan stats from the user's profile in Firestore.
    private void loadLoanStats() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference profileRef = db.collection("profile").document(currentUser.getUid());
        profileRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Assuming your Profile class has getters for monthlyIncome and interestRate.
                Profile profile = documentSnapshot.toObject(Profile.class);
                if (profile != null) {
                    double monthlyIncome = profile.getMonthlyIncome();
                    double interestRate = profile.getInterestRate();

                    // Get stored loan payment stats or use defaults.
                    int paymentsMade = documentSnapshot.contains("paymentsMade")
                            ? documentSnapshot.getLong("paymentsMade").intValue() : 0;
                    double totalRepaid = documentSnapshot.contains("totalRepaid")
                            ? documentSnapshot.getDouble("totalRepaid") : 0.0;

                    // Compute recommended repayment using your formula.
                    double recommendedRepayment = (interestRate > 5.0)
                            ? monthlyIncome * 0.15
                            : monthlyIncome * 0.10;

                    String statsText = "Payments Made: " + paymentsMade +
                            "\nTotal Repaid: $" + String.format("%.2f", totalRepaid) +
                            "\nRecommended Repayment: $" + String.format("%.2f", recommendedRepayment);
                    textViewLoanStats.setText(statsText);
                }
            }
        }).addOnFailureListener(e ->
                Toast.makeText(NotificationsActivity.this, "Failed to load loan stats", Toast.LENGTH_SHORT).show());
    }

    // Listen to upcoming expenses using a snapshot listener so changes update automatically.
    private void listenToUpcomingExpenses() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        CollectionReference expensesRef = db.collection("profile")
                .document(currentUser.getUid())
                .collection("expenses");

        expensesRef.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Toast.makeText(NotificationsActivity.this, "Error loading expenses", Toast.LENGTH_SHORT).show();
                return;
            }
            upcomingExpenseList.clear();
            long now = System.currentTimeMillis();
            if (snapshots != null && !snapshots.isEmpty()) {
                snapshots.getDocuments().forEach(doc -> {
                    Expense expense = doc.toObject(Expense.class);
                    if (expense != null && expense.getChargeDate() >= now) {
                        upcomingExpenseList.add(new ExpenseItem(expense, doc.getId()));
                    }
                });
            }
            notificationsAdapter.notifyDataSetChanged();
        });
    }

    // Set up bottom navigation to switch between activities.
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                startActivity(new Intent(NotificationsActivity.this, MainActivity.class));
                return true;
            } else if (id == R.id.navigation_repayment_planner) {
                startActivity(new Intent(NotificationsActivity.this, RepaymentMenu.class));
                return true;
            } else if (id == R.id.navigation_budget) {
                startActivity(new Intent(NotificationsActivity.this, BudgetExpenseActivity.class));
                return true;
            } else if (id == R.id.navigation_notifications) {
                // Already here.
                return true;
            } else if (id == R.id.navigation_profile) {
                startActivity(new Intent(NotificationsActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // Set up the "Add Payment" button so the user can record a loan payment.
    private void setupAddPaymentButton() {
        buttonAddPayment.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsActivity.this);
            builder.setTitle("Add Payment");

            final EditText input = new EditText(NotificationsActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setHint("Enter payment amount");
            builder.setView(input);

            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String amountStr = input.getText().toString().trim();
                    if (amountStr.isEmpty()) {
                        Toast.makeText(NotificationsActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        float paymentAmount = Float.parseFloat(amountStr);
                        recordLoanPayment(paymentAmount);
                    } catch (NumberFormatException ex) {
                        Toast.makeText(NotificationsActivity.this, "Invalid amount", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        });
    }

    // Update the user's loan stats in Firestore by incrementing paymentsMade and totalRepaid.
    private void recordLoanPayment(float paymentAmount) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference profileRef = db.collection("profile").document(currentUser.getUid());
        profileRef.update("paymentsMade", FieldValue.increment(1),
                        "totalRepaid", FieldValue.increment((double) paymentAmount))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(NotificationsActivity.this, "Payment recorded", Toast.LENGTH_SHORT).show();
                    // Refresh loan stats from Firestore.
                    loadLoanStats();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(NotificationsActivity.this, "Failed to record payment", Toast.LENGTH_SHORT).show());
    }
}
