package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private TextView textViewBalanceAmount;
    private ImageView imageViewToggleBalance;
    private boolean isBalanceVisible = true;  // Track whether the balance is shown or hidden

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Find Views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        textViewBalanceAmount = findViewById(R.id.textViewBalanceAmount);
        imageViewToggleBalance = findViewById(R.id.imageViewToggleBalance);

        // 2. Set up the item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    // Navigate to Home
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    // Navigate to Repayment Planner
                    return true;
                } else if (id == R.id.navigation_budget) {
                    // Navigate to Budget & Expense Management
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    // Navigate to Notifications
                    return true;
                } else if (id == R.id.navigation_profile) {
                    // Navigate to Profile
                    return true;
                } else {
                    return false;
                }
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
