package com.example.student_loan_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ResourceHubActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResourceAdapter adapter;
    private List<Resource> resourceList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_hub);

        recyclerView = findViewById(R.id.recyclerViewResources);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        resourceList = new ArrayList<>();
        resourceList.add(new Resource("OSAP",
                "Ontario Student Assistance Program – Apply for aid",
                "https://www.ontario.ca/page/osap-ontario-student-assistance-program"));
        resourceList.add(new Resource("NSLSC",
                "Manage federal student loans in Canada",
                "https://www.csnpe-nslsc.cibletudes-canlearn.ca"));
        resourceList.add(new Resource(
                "CRA Interest Info",
                "Canada Revenue Agency – Learn how student loan interest is reported and claimed.",
                "https://www.canada.ca/en/revenue-agency/services/tax/individuals/topics/about-your-tax-return/tax-return/completing-a-tax-return/deductions-credits-expenses/line-31900-interest-paid-on-your-student-loans.html"));

        resourceList.add(new Resource(
                "Repayment Assistance Program",
                "Apply for reduced monthly payments based on your income through the RAP.",
                "https://www.csnpe-nslsc.canada.ca/en/repayment-assistance"));

        resourceList.add(new Resource(
                "Scholarships and Grant Search",
                "Find scholarships and grants through external databases.",
                "https://www.scholarshipscanada.com/"));

        resourceList.add(new Resource(
                "Resume Building Services",
                "Use these tools to build and improve your resume for job applications.",
                "https://www.canada.ca/en/services/jobs/education/resume.html"));

        resourceList.add(new Resource(
                "Financial Hardship Options",
                "Explore options if you're struggling to repay your loans.",
                "https://www.canada.ca/en/services/benefits/education/student-aid/repay-student-loan/repayment-assistance.html"));

        adapter = new ResourceAdapter(resourceList, this);
        recyclerView.setAdapter(adapter);

        Button btnBackToMenu = findViewById(R.id.btnBackToMenu);
        btnBackToMenu.setOnClickListener(v -> finish());
    }
}
