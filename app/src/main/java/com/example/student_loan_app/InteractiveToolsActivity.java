package com.example.student_loan_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class InteractiveToolsActivity extends AppCompatActivity {

    private Button btnLoanCalculator;
    private Button btnRepaymentEstimator;
    private Button btnBackToFinancialLiteracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_tools);

        btnLoanCalculator = findViewById(R.id.btnLoanCalculator);
        btnRepaymentEstimator = findViewById(R.id.btnRepaymentEstimator);
        btnBackToFinancialLiteracy = findViewById(R.id.btnBackToFinancialLiteracy);

        btnLoanCalculator.setOnClickListener(v -> {
            Intent intent = new Intent(InteractiveToolsActivity.this, LoanCalculatorActivity.class);
            startActivity(intent);
        });

        btnRepaymentEstimator.setOnClickListener(v -> {
            Intent intent = new Intent(InteractiveToolsActivity.this, RepaymentEstimatorActivity.class);
            startActivity(intent);
        });

        btnBackToFinancialLiteracy.setOnClickListener(v -> finish());
    }
}
