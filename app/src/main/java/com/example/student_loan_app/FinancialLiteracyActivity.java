package com.example.student_loan_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class FinancialLiteracyActivity extends AppCompatActivity {

    private Button btnEducationalContent;
    private Button btnInteractiveTools;
    private Button btnResourceHub; // NEW

    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_literacy);

        btnEducationalContent = findViewById(R.id.btnEducationalContent);
        btnInteractiveTools = findViewById(R.id.btnInteractiveTools);
        btnResourceHub = findViewById(R.id.btnResourceHub);
        Button btnBackToHome = findViewById(R.id.btnBackToHome);




        btnEducationalContent.setOnClickListener(v -> {
            Intent intent = new Intent(FinancialLiteracyActivity.this, EducationalContentActivity.class);
            startActivity(intent);
        });


        btnInteractiveTools.setOnClickListener(v -> {
            Intent intent = new Intent(FinancialLiteracyActivity.this, InteractiveToolsActivity.class);
            startActivity(intent);
        });


        btnResourceHub.setOnClickListener(v -> {
            Intent intent = new Intent(FinancialLiteracyActivity.this, ResourceHubActivity.class);
            startActivity(intent);
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(FinancialLiteracyActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
