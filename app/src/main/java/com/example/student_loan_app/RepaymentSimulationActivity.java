package com.example.student_loan_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RepaymentSimulationActivity extends AppCompatActivity {

    private EditText loanAmount, interestRate, loanTerm;
    private Spinner planType;
    private TextView resultView;
    private LineChart chart;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_simulation);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loanAmount = findViewById(R.id.etLoanAmount);
        interestRate = findViewById(R.id.etInterestRate);
        loanTerm = findViewById(R.id.etLoanTerm);
        planType = findViewById(R.id.spinnerPlanType);
        resultView = findViewById(R.id.tvResult);
        chart = findViewById(R.id.chart);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.repayment_plans,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planType.setAdapter(adapter);

        loadUserProfile();

        btnCalculate.setOnClickListener(v -> calculateRepayment());

        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserProfile() {
        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                loanAmount.setText(String.valueOf(profile.getLoanAmount()));
                                interestRate.setText(String.valueOf(profile.getInterestRate()));
                                loanTerm.setText(String.valueOf(profile.getLoanTerm()));
                                String repaymentPlan = profile.getRepaymentPlan();
                                ArrayAdapter adapter = (ArrayAdapter) planType.getAdapter();
                                int position = adapter.getPosition(repaymentPlan);
                                if (position >= 0) {
                                    planType.setSelection(position);
                                }
                            }
                        } else {
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void calculateRepayment() {
        if (loanAmount.getText().toString().isEmpty() ||
                interestRate.getText().toString().isEmpty() ||
                loanTerm.getText().toString().isEmpty()) {
            resultView.setText("Please enter all loan details.");
            return;
        }

        double amount = Double.parseDouble(loanAmount.getText().toString());
        double rate = Double.parseDouble(interestRate.getText().toString()) / 100 / 12;
        int months = (int)(Double.parseDouble(loanTerm.getText().toString()) * 12);

        double monthlyPayment = (amount * rate) / (1 - Math.pow(1 + rate, -months));
        resultView.setText(String.format("Estimated Monthly Payment: $%.2f", monthlyPayment));

        updateGraph(amount, rate, months, monthlyPayment);
    }

    private void updateGraph(double amount, double rate, int months, double monthlyPayment) {
        List<Entry> entries = new ArrayList<>();
        double remainingBalance = amount;

        for (int i = 1; i <= months; i++) {
            remainingBalance = (remainingBalance * (1 + rate)) - monthlyPayment;
            if (remainingBalance < 0) {
                remainingBalance = 0;
            }
            entries.add(new Entry(i, (float) remainingBalance));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Loan Balance Over Time");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }
}
