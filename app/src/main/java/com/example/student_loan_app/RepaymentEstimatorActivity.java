package com.example.student_loan_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

public class RepaymentEstimatorActivity extends AppCompatActivity {

    private EditText loanAmountInput, interestRateInput, monthlyPaymentInput;
    private Button calculateButton, btnBackToInteractiveTools;
    private TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_estimator);

        loanAmountInput = findViewById(R.id.loanAmountInput);
        interestRateInput = findViewById(R.id.interestRateInput);
        monthlyPaymentInput = findViewById(R.id.monthlyPaymentInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);
        btnBackToInteractiveTools = findViewById(R.id.btnBackToInteractiveTools);

        calculateButton.setOnClickListener(v -> estimateRepaymentTime());
        btnBackToInteractiveTools.setOnClickListener(v -> finish()); // Return to Interactive Tools
    }

    private void estimateRepaymentTime() {
        String loanAmountStr = loanAmountInput.getText().toString();
        String interestRateStr = interestRateInput.getText().toString();
        String monthlyPaymentStr = monthlyPaymentInput.getText().toString();

        if (loanAmountStr.isEmpty() || interestRateStr.isEmpty() || monthlyPaymentStr.isEmpty()) {
            resultText.setText("Please fill in all fields");
            return;
        }

        double loanAmount = Double.parseDouble(loanAmountStr);
        double annualInterestRate = Double.parseDouble(interestRateStr) / 100;
        double monthlyPayment = Double.parseDouble(monthlyPaymentStr);

        double monthlyInterestRate = annualInterestRate / 12;

        if (monthlyPayment <= loanAmount * monthlyInterestRate) {
            resultText.setText("Monthly payment is too low to cover interest. Increase the amount.");
            return;
        }

        // Calculate the number of months required to pay off the loan
        double numPayments = Math.log(monthlyPayment / (monthlyPayment - (loanAmount * monthlyInterestRate)))
                / Math.log(1 + monthlyInterestRate);

        int months = (int) Math.ceil(numPayments);
        int years = months / 12;
        int remainingMonths = months % 12;

        DecimalFormat df = new DecimalFormat("#.##");
        double totalInterest = (monthlyPayment * months) - loanAmount;

        resultText.setText("Estimated Repayment Time: " + years + " years and " + remainingMonths + " months\n"
                + "Total Interest Paid: $" + df.format(totalInterest));
    }
}
