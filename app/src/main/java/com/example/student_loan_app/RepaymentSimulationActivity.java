package com.example.student_loan_app;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import java.util.ArrayList;
import java.util.List;

public class RepaymentSimulationActivity extends AppCompatActivity {
    private EditText loanAmount, interestRate, loanTerm;
    private Spinner planType;
    private TextView resultView;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_simulation);

        loanAmount = findViewById(R.id.etLoanAmount);
        interestRate = findViewById(R.id.etInterestRate);
        loanTerm = findViewById(R.id.etLoanTerm);
        planType = findViewById(R.id.spinnerPlanType);
        resultView = findViewById(R.id.tvResult);
        chart = findViewById(R.id.chart);
        Button btnCalculate = findViewById(R.id.btnCalculate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.repayment_plans, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planType.setAdapter(adapter);

        btnCalculate.setOnClickListener(v -> calculateRepayment());
    }

    private void calculateRepayment() {
        double amount = Double.parseDouble(loanAmount.getText().toString());
        double rate = Double.parseDouble(interestRate.getText().toString()) / 100 / 12;
        int months = Integer.parseInt(loanTerm.getText().toString()) * 12;

        double monthlyPayment = (amount * rate) / (1 - Math.pow(1 + rate, -months));
        resultView.setText(String.format("Estimated Monthly Payment: $%.2f", monthlyPayment));

        updateGraph(amount, rate, months, monthlyPayment);
    }

    private void updateGraph(double amount, double rate, int months, double monthlyPayment) {
        List<Entry> entries = new ArrayList<>();
        double remainingBalance = amount;

        for (int i = 1; i <= months; i++) {
            remainingBalance = (remainingBalance * (1 + rate)) - monthlyPayment;
            if (remainingBalance < 0) remainingBalance = 0;
            entries.add(new Entry(i, (float) remainingBalance));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Loan Balance Over Time");
        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);
        chart.invalidate();
    }
}
