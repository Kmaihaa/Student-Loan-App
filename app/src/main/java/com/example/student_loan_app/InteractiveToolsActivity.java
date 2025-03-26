package com.example.student_loan_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class InteractiveToolsActivity extends AppCompatActivity {

    private Button btnLoanCalculator;
    private Button btnRepaymentEstimator;
    private Button btnBackToMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_tools);

        btnLoanCalculator = findViewById(R.id.btnLoanCalculator);
        btnRepaymentEstimator = findViewById(R.id.btnRepaymentEstimator);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        btnLoanCalculator.setOnClickListener(v -> {
            Intent intent = new Intent(InteractiveToolsActivity.this, LoanCalculatorActivity.class);
            startActivity(intent);
        });

        btnRepaymentEstimator.setOnClickListener(v -> {
            Intent intent = new Intent(InteractiveToolsActivity.this, DebtToIncomeCalculatorActivity.class);
            startActivity(intent);
        });


        btnBackToMenu.setOnClickListener(v -> finish());

    }
}
