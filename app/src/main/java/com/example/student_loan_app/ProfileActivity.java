package com.example.student_loan_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    // Fields
    private EditText editTextName, editTextAge, editTextLoanAmount, editTextMonthlyIncome;
    private EditText editTextInterestRate, editTextLoanTerm;
    private Spinner spinnerCompoundFrequency, spinnerRepaymentPlan;
    private Button buttonSubmit;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Constants
    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Bind Views
        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextLoanAmount = findViewById(R.id.editTextLoanAmount);
        editTextMonthlyIncome = findViewById(R.id.editTextMonthlyIncome);
        editTextInterestRate = findViewById(R.id.editTextInterestRate);
        editTextLoanTerm = findViewById(R.id.editTextLoanTerm);
        spinnerCompoundFrequency = findViewById(R.id.spinnerCompoundFrequency);
        spinnerRepaymentPlan = findViewById(R.id.spinnerRepaymentPlan);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Set up bottom navigation on this Profile page
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationViewProfile);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                    return true;
                } else if (id == R.id.navigation_repayment_planner) {
                    startActivity(new Intent(ProfileActivity.this, RepaymentMenu.class));
                    return true;
                } else if (id == R.id.navigation_budget) {
                    startActivity(new Intent(ProfileActivity.this, BudgetExpenseActivity.class));
                    return true;
                } else if (id == R.id.navigation_notifications) {
                    startActivity(new Intent(ProfileActivity.this, NotificationsActivity.class));
                    return true;
                } else if (id == R.id.navigation_profile) {
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            }
        });

        // If a user is logged in, load their profile data
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            loadProfileData(currentUser.getUid());
        } else {
            // If no user is logged in, you might prompt them to log in
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
        }

        // Submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = auth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(ProfileActivity.this, "Please log in first.", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitProfile(user.getUid());
            }
        });
    }

    // Load existing profile data if it exists
    private void loadProfileData(String userId) {
        DocumentReference docRef = db.collection("profile").document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Convert document to Profile object
                Profile profile = documentSnapshot.toObject(Profile.class);
                if (profile != null) {
                    // Populate the fields
                    editTextName.setText(profile.getName());
                    editTextAge.setText(String.valueOf(profile.getAge()));
                    editTextLoanAmount.setText(String.valueOf(profile.getLoanAmount()));
                    editTextMonthlyIncome.setText(String.valueOf(profile.getMonthlyIncome()));
                    editTextInterestRate.setText(String.valueOf(profile.getInterestRate()));
                    editTextLoanTerm.setText(String.valueOf(profile.getLoanTerm()));

                    // Set the spinner selections
                    // For compoundFrequency and repaymentPlan, you'll need to match the array index
                    // if (profile.getCompoundFrequency() != null) { ... }
                    // if (profile.getRepaymentPlan() != null) { ... }
                    // For simplicity, we won't show the full spinner selection code here
                }
            } else {
                Log.d(TAG, "No profile document found for user: " + userId);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching profile", e);
        });
    }

    // Create or update the user's profile
    private void submitProfile(String userId) {
        // Retrieve text inputs
        String name = editTextName.getText().toString().trim();
        String ageStr = editTextAge.getText().toString().trim();
        String loanAmountStr = editTextLoanAmount.getText().toString().trim();
        String monthlyIncomeStr = editTextMonthlyIncome.getText().toString().trim();
        String interestRateStr = editTextInterestRate.getText().toString().trim();
        String loanTermStr = editTextLoanTerm.getText().toString().trim();

        // Retrieve spinner selections
        String compoundFrequency = spinnerCompoundFrequency.getSelectedItem().toString();
        String repaymentPlan = spinnerRepaymentPlan.getSelectedItem().toString();

        // Validate fields
        if (name.isEmpty() || ageStr.isEmpty() || loanAmountStr.isEmpty() ||
                monthlyIncomeStr.isEmpty() || interestRateStr.isEmpty() || loanTermStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parse numeric values
        int age = Integer.parseInt(ageStr);
        double loanAmount = Double.parseDouble(loanAmountStr);
        double monthlyIncome = Double.parseDouble(monthlyIncomeStr);
        double interestRate = Double.parseDouble(interestRateStr);
        double loanTerm = Double.parseDouble(loanTermStr);

        // Build Profile object
        Profile profile = new Profile(
                name,
                age,
                loanAmount,
                monthlyIncome,
                interestRate,
                loanTerm,
                compoundFrequency,
                repaymentPlan
        );

        // Use set() with the userId as the document ID
        DocumentReference docRef = db.collection("profile").document(userId);
        docRef.set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
