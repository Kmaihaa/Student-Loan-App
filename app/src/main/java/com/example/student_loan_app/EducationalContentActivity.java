package com.example.student_loan_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EducationalContentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private List<Article> articleList;
    private Button btnBackToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_content);

        recyclerView = findViewById(R.id.recyclerViewArticles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToMenu = findViewById(R.id.btnBackToMenu);

        btnBackToMenu.setOnClickListener(v -> finish());


        articleList = new ArrayList<>();
        articleList.add(new Article("Understanding Student Loan Interest", "https://example.com/interest"));
        articleList.add(new Article("How to Manage Your Loans", "https://example.com/manage"));
        articleList.add(new Article("Budgeting for Repayments", "https://example.com/budgeting"));

        adapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(adapter);
    }
}
