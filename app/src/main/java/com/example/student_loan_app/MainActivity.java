package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView textViewBalanceAmount;
    private ImageView imageViewToggleBalance;
    private boolean isBalanceVisible = true;  // Track whether the balance is shown or hidden

    private Button btnFinancialLiteracy;
    private Button interactiveToolsButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profile").limit(1).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Log.d("FirestoreTest", "Connection Successful! Data exists.");
                    } else {
                        Log.d("FirestoreTest", "Connected, but no data found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreTest", "Connection failed", e);
                });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Find Views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textViewBalanceAmount = findViewById(R.id.textViewBalanceAmount);
        imageViewToggleBalance = findViewById(R.id.imageViewToggleBalance);

        btnFinancialLiteracy = findViewById(R.id.btnFinancialLiteracy);



        // 2. Set up the item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    // Navigate to Home
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    Intent intent = new Intent(MainActivity.this, RepaymentMenu.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_budget) {
                    // Navigate to Budget & Expense Management
                    Intent intent = new Intent(MainActivity.this, BudgetExpenseActivity.class);
                    startActivity(intent);
                    return true;
                }   else if (id == R.id.navigation_notifications) {
                    // Navigate to Notifications
                    return true;
                } else if (id == R.id.navigation_profile) {
                    // Navigate to Profile
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        btnFinancialLiteracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FinancialLiteracyActivity.class);
                startActivity(intent);
            }
        });



        // 3. Toggle Balance Visibility
        imageViewToggleBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isBalanceVisible = !isBalanceVisible;  // Flip the visibility state
                updateBalanceVisibility();
            }
        });
    }

    // Helper method to show or hide the balance text
    private void updateBalanceVisibility() {
        if (isBalanceVisible) {
            // Replace with real balance from your database or logic
            textViewBalanceAmount.setText("$12,345.67");
            imageViewToggleBalance.setImageResource(R.drawable.ic_eye_open);
        } else {
            textViewBalanceAmount.setText("******");
            imageViewToggleBalance.setImageResource(R.drawable.ic_eye_closed);
        }
    }
}
