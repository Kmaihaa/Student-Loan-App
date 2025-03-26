package com.example.student_loan_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class RepaymentMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_menu);

        Button btnSimulation = findViewById(R.id.btnSimulation);
        Button btnExtraPayment = findViewById(R.id.btnExtraPayment);
        Button btnGoalSetting = findViewById(R.id.btnGoalSetting);
        Button btnBackToHome = findViewById(R.id.btnBackToHome);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);


        btnSimulation.setOnClickListener(v -> startActivity(new Intent(RepaymentMenu.this, RepaymentSimulationActivity.class)));
        btnExtraPayment.setOnClickListener(v -> startActivity(new Intent(RepaymentMenu.this, ExtraPaymentActivity.class)));
        btnGoalSetting.setOnClickListener(v -> startActivity(new Intent(RepaymentMenu.this, GoalSettingActivity.class)));

        btnBackToHome.setOnClickListener(v -> finish());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    Intent intent = new Intent(RepaymentMenu.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    Intent intent = new Intent(RepaymentMenu.this, RepaymentMenu.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.navigation_budget) {
                    Intent intent = new Intent(RepaymentMenu.this, BudgetExpenseActivity.class);
                    startActivity(intent);
                    return true;
                }   else if (id == R.id.navigation_notifications) {
                    Intent intent = new Intent(RepaymentMenu.this, NotificationsActivity.class);
                    return true;
                } else if (id == R.id.navigation_profile) {
                    Intent intent = new Intent(RepaymentMenu.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });

    }
}
