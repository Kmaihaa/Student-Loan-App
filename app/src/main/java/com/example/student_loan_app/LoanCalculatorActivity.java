package com.example.student_loan_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class LoanCalculatorActivity extends AppCompatActivity {

    private EditText loanAmountInput, interestRateInput, loanTermInput;
    private Button calculateButton;
    private TextView resultText;
    private Button btnBackToMenu;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_calculator);

        loanAmountInput = findViewById(R.id.loanAmountInput);
        interestRateInput = findViewById(R.id.interestRateInput);
        loanTermInput = findViewById(R.id.loanTermInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);
        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        calculateButton.setOnClickListener(v -> calculateLoanPayment());

        btnBackToMenu.setOnClickListener(v -> finish());
    }

    private void calculateLoanPayment() {
        String loanAmountStr = loanAmountInput.getText().toString();
        String interestRateStr = interestRateInput.getText().toString();
        String loanTermStr = loanTermInput.getText().toString();


        if (loanAmountStr.isEmpty() || interestRateStr.isEmpty() || loanTermStr.isEmpty()) {
            resultText.setText("Please fill in all fields");
            return;
        }

        double loanAmount = Double.parseDouble(loanAmountStr);
        double annualInterestRate = Double.parseDouble(interestRateStr) / 100;
        int loanTerm = Integer.parseInt(loanTermStr);

        double monthlyInterestRate = annualInterestRate / 12;
        int totalPayments = loanTerm * 12;
        double monthlyPayment;

        if (monthlyInterestRate == 0) {
            monthlyPayment = loanAmount / totalPayments;
        } else {
            monthlyPayment = (loanAmount * monthlyInterestRate) /
                    (1 - Math.pow(1 + monthlyInterestRate, -totalPayments));
        }

        DecimalFormat df = new DecimalFormat("#.##");
        resultText.setText("Estimated Monthly Payment: $" + df.format(monthlyPayment));
    }



}