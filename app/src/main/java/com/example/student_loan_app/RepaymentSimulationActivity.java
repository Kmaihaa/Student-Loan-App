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

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find Views
        loanAmount = findViewById(R.id.etLoanAmount);
        interestRate = findViewById(R.id.etInterestRate);
        loanTerm = findViewById(R.id.etLoanTerm);
        planType = findViewById(R.id.spinnerPlanType);
        resultView = findViewById(R.id.tvResult);
        chart = findViewById(R.id.chart);
        Button btnCalculate = findViewById(R.id.btnCalculate);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Set up spinner adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.repayment_plans,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planType.setAdapter(adapter);

        // Load and autofill profile data when the user enters the simulation screen
        loadUserProfile();

        // Let the user trigger the repayment calculation manually after reviewing details
        btnCalculate.setOnClickListener(v -> calculateRepayment());

        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserProfile() {
        // Ensure the user is signed in
        if (auth.getCurrentUser() == null) {
            // Handle unauthenticated state, for example, by redirecting to a sign-in screen
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        // Adjust the collection/document path according to your Firestore structure
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Convert document to Profile object using the helper class
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                // Autofill form fields using Profile getters
                                loanAmount.setText(String.valueOf(profile.getLoanAmount()));
                                interestRate.setText(String.valueOf(profile.getInterestRate()));
                                loanTerm.setText(String.valueOf(profile.getLoanTerm()));

                                // Set the spinner selection based on the repayment plan from profile
                                String repaymentPlan = profile.getRepaymentPlan();
                                ArrayAdapter adapter = (ArrayAdapter) planType.getAdapter();
                                int position = adapter.getPosition(repaymentPlan);
                                if (position >= 0) {
                                    planType.setSelection(position);
                                }
                            }
                        } else {
                            // Optionally handle the case where the user has no saved profile data
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
        // Validate input fields to avoid parsing errors
        if (loanAmount.getText().toString().isEmpty() ||
                interestRate.getText().toString().isEmpty() ||
                loanTerm.getText().toString().isEmpty()) {
            resultView.setText("Please enter all loan details.");
            return;
        }

        // Retrieve values from the fields
        double amount = Double.parseDouble(loanAmount.getText().toString());
        double rate = Double.parseDouble(interestRate.getText().toString()) / 100 / 12;
        int months = (int)(Double.parseDouble(loanTerm.getText().toString()) * 12);

        // Calculate monthly payment using the standard amortization formula
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
        chart.invalidate();  // Refresh the chart to display updated data
    }
}
