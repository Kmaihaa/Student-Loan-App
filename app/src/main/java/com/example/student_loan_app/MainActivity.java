package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private PieChart pieChart;
    private LineChart lineChart;
    private TextView textViewRemainingLoan;
    private TextView textViewWelcome;
    private Button btnFinancialLiteracy;

    private boolean isRemainingLoanVisible = true; // toggles text visibility
    private double fetchedFullLoanAmount = 0.0;      // full loan amount from server
    private double fetchedTotalRepaid = 0.0;         // total repaid from server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        textViewRemainingLoan = findViewById(R.id.textViewRemainingLoan);
        textViewWelcome = findViewById(R.id.textViewWelcome);
        btnFinancialLiteracy = findViewById(R.id.btnFinancialLiteracy);

        // Fetch user profile data from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("profile")
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);

                        String firstName = doc.getString("name");
                        textViewWelcome.setText((firstName != null && !firstName.isEmpty())
                                ? "Welcome, " + firstName
                                : "Welcome, User");

                        // Retrieve loan details: full loan amount and total repaid
                        Double fullLoanAmount = doc.getDouble("loanAmount");
                        Double totalRepaid = doc.getDouble("totalRepaid");
                        if (fullLoanAmount != null && totalRepaid != null) {
                            fetchedFullLoanAmount = fullLoanAmount;
                            fetchedTotalRepaid = totalRepaid;
                            double remainingAmount = fetchedFullLoanAmount - fetchedTotalRepaid;
                            textViewRemainingLoan.setText("$" + String.format("%.2f", remainingAmount));
                            updateLoanPieChartData((float) fetchedTotalRepaid, (float) remainingAmount);
                            loadDetailedLineChartData();
                        }
                    } else {
                        Log.d("FirestoreTest", "Connected, but no data found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreTest", "Connection failed", e));

        // Bottom Navigation Listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    return true;
                } else if (id == R.id.navigation_budget) {
                    startActivity(new Intent(MainActivity.this, BudgetExpenseActivity.class));
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    return true;
                } else if (id == R.id.navigation_profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                } else {
                    return false;
                }
            }
        });

        setupPieChart();
        setupLineChart();

        // Toggle Remaining Loan Visibility on click
        textViewRemainingLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRemainingLoanVisible = !isRemainingLoanVisible;
                updateRemainingLoanVisibility();
            }
        });

        btnFinancialLiteracy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FinancialLiteracyActivity.class));
            }
        });
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setHoleRadius(40f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);
    }

    private void updateLoanPieChartData(float totalRepaid, float remaining) {
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalRepaid, "Repaid"));
        entries.add(new PieEntry(remaining, "Remaining"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                Color.parseColor("#4CAF50"), // green for repaid
                Color.parseColor("#F44336")  // red for remaining
        });
        dataSet.setSliceSpace(3f);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);

        // Set up a custom marker to display pinpoint details
        MyMarkerView markerView = new MyMarkerView(this, R.layout.marker_view);
        markerView.setChartView(lineChart);
        lineChart.setMarker(markerView);
    }

    private void loadDetailedLineChartData() {
        int months = 12;
        float monthlyIncrement = (float) ((fetchedFullLoanAmount - fetchedTotalRepaid) / months);
        float cumulativeRepaid = (float) fetchedTotalRepaid;

        List<Entry> scheduleEntries = new ArrayList<>();
        for (int i = 0; i <= months; i++) {
            scheduleEntries.add(new Entry(i, cumulativeRepaid));
            cumulativeRepaid += monthlyIncrement; // Increase monthly
        }

        LineDataSet scheduleDataSet = new LineDataSet(scheduleEntries, "Projected Repayment");
        scheduleDataSet.setColor(Color.parseColor("#3F51B5"));
        scheduleDataSet.setLineWidth(2f);
        scheduleDataSet.setCircleColor(Color.parseColor("#FF9800"));
        scheduleDataSet.setCircleRadius(4f);
        scheduleDataSet.setDrawValues(false);

        List<Entry> repaidEntries = new ArrayList<>();
        repaidEntries.add(new Entry(0, (float) fetchedTotalRepaid));

        LineDataSet repaidDataSet = new LineDataSet(repaidEntries, "User's Total Repaid");
        repaidDataSet.setColor(Color.RED);
        repaidDataSet.setCircleColor(Color.RED);
        repaidDataSet.setCircleRadius(6f);
        repaidDataSet.setDrawValues(true);
        repaidDataSet.setLineWidth(0f);

        LineData lineData = new LineData(scheduleDataSet, repaidDataSet);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    private void updateRemainingLoanVisibility() {
        if (isRemainingLoanVisible) {
            double remainingAmount = fetchedFullLoanAmount - fetchedTotalRepaid;
            textViewRemainingLoan.setText("$" + String.format("%.2f", remainingAmount));
        } else {
            textViewRemainingLoan.setText("******");
        }
    }
}
