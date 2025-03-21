package com.example.student_loan_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class FinancialLiteracyActivity extends AppCompatActivity {

    private Button btnEducationalContent;
    private Button btnInteractiveTools; // Added for Interactive Tools

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_literacy);

        btnEducationalContent = findViewById(R.id.btnEducationalContent);
        btnInteractiveTools = findViewById(R.id.btnInteractiveTools); // Connect to XML

        // Open the Educational Content screen
        btnEducationalContent.setOnClickListener(v -> {
            Intent intent = new Intent(FinancialLiteracyActivity.this, EducationalContentActivity.class);
            startActivity(intent);
        });

        // Open the Interactive Tools screen
        btnInteractiveTools.setOnClickListener(v -> {
            Intent intent = new Intent(FinancialLiteracyActivity.this, InteractiveToolsActivity.class);
            startActivity(intent);
        });
    }
}
