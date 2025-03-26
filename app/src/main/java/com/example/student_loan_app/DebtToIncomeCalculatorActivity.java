package com.example.student_loan_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DebtToIncomeCalculatorActivity extends AppCompatActivity {

    private EditText incomeInput, debtInput;
    private Button calculateButton, btnBackToMenu;
    private TextView resultText, adviceText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_to_income_calculator);

        incomeInput = findViewById(R.id.incomeInput);
        debtInput = findViewById(R.id.debtInput);
        calculateButton = findViewById(R.id.calculateButton);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);
        resultText = findViewById(R.id.resultText);
        adviceText = findViewById(R.id.adviceText);

        calculateButton.setOnClickListener(view -> calculateDTI());
        btnBackToMenu.setOnClickListener(view -> finish());
    }

    private void calculateDTI() {
        String incomeStr = incomeInput.getText().toString();
        String debtStr = debtInput.getText().toString();

        if (incomeStr.isEmpty() || debtStr.isEmpty()) {
            resultText.setText("Please fill in both fields.");
            adviceText.setText("");
            return;
        }

        double income = Double.parseDouble(incomeStr);
        double debt = Double.parseDouble(debtStr);

        if (income <= 0) {
            resultText.setText("Monthly income must be greater than zero.");
            adviceText.setText("");
            return;
        }

        double dti = (debt / income) * 100;
        String dtiFormatted = String.format("%.2f", dti);
        resultText.setText("Your Debt-to-Income Ratio: " + dtiFormatted + "%");

        if (dti < 20) {
            adviceText.setText("✅ Excellent – Your finances are in good shape.");
        } else if (dti < 35) {
            adviceText.setText("🟡 Fair – You're managing debt, but be cautious.");
        } else {
            adviceText.setText("🔴 High – Consider reducing debt for better financial health.");
        }
    }
}
