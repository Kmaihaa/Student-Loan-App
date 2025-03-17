package com.example.student_loan_app;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ExtraPaymentActivity extends AppCompatActivity {
    private EditText etLoanAmount, etInterestRate, etLoanTerm, etExtraPayment;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_payment);

        etLoanAmount = findViewById(R.id.etLoanAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        etLoanTerm = findViewById(R.id.etLoanTerm);
        etExtraPayment = findViewById(R.id.etExtraPayment);
        tvResult = findViewById(R.id.tvResult);
        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(v -> calculateExtraPaymentImpact());
    }

    private void calculateExtraPaymentImpact() {
        double loanAmount = Double.parseDouble(etLoanAmount.getText().toString());
        double interestRate = Double.parseDouble(etInterestRate.getText().toString()) / 100 / 12;
        int loanTermMonths = Integer.parseInt(etLoanTerm.getText().toString()) * 12;
        double extraPayment = Double.parseDouble(etExtraPayment.getText().toString());

        double monthlyPayment = (loanAmount * interestRate) / (1 - Math.pow(1 + interestRate, -loanTermMonths));
        double newMonthlyPayment = monthlyPayment + extraPayment;

        int newTermMonths = 0;
        double remainingBalance = loanAmount;

        while (remainingBalance > 0) {
            double interest = remainingBalance * interestRate;
            double principal = newMonthlyPayment - interest;
            remainingBalance -= principal;
            newTermMonths++;
        }

        tvResult.setText(String.format("New Loan Term: %d months\nOld Loan Term: %d months", newTermMonths, loanTermMonths));
    }
}