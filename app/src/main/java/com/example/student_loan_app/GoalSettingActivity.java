package com.example.student_loan_app;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GoalSettingActivity extends AppCompatActivity {
    private EditText etLoanAmount, etInterestRate, etTargetMonths;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_setting);

        etLoanAmount = findViewById(R.id.etLoanAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        etTargetMonths = findViewById(R.id.etTargetMonths);
        tvResult = findViewById(R.id.tvResult);
        Button btnCalculate = findViewById(R.id.btnCalculate);

        btnCalculate.setOnClickListener(v -> calculateGoalPayment());
    }

    private void calculateGoalPayment() {
        double loanAmount = Double.parseDouble(etLoanAmount.getText().toString());
        double interestRate = Double.parseDouble(etInterestRate.getText().toString()) / 100 / 12;
        int targetMonths = Integer.parseInt(etTargetMonths.getText().toString());

        double goalPayment = (loanAmount * interestRate) / (1 - Math.pow(1 + interestRate, -targetMonths));

        tvResult.setText(String.format("Required Monthly Payment: $%.2f", goalPayment));
    }
}
