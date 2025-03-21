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
        articleList.add(new Article("Understanding interest", "https://www.canada.ca/en/revenue-agency/services/tax/businesses/topics/corporations/corporation-payments/understanding-interest.html"));
        articleList.add(new Article("Loan Principal Vs Interest", "https://www.consumerfinance.gov/ask-cfpb/on-a-mortgage-whats-the-difference-between-my-principal-and-interest-payment-and-my-total-monthly-payment-en-1941/"));
        articleList.add(new Article("APR ( Annual Percentage Rate)", "https://www.investopedia.com/terms/a/apr.asp"));
        articleList.add(new Article("Grace periods", "https://www.investopedia.com/terms/g/grace_period.asp#:~:text=A%20grace%20period%20allows%20a,of%20the%20loan%20or%20contract."));
        articleList.add(new Article("Deferment vs Forbearance", "https://studentaid.gov/manage-loans/lower-payments/get-temporary-relief"));
        articleList.add(new Article("Minimum Payment Trap", "https://www.sands-trustee.com/blog/breakfast-television-tips-to-avoid-minimum-payment-trap/"));




        adapter = new ArticleAdapter(articleList);
        recyclerView.setAdapter(adapter);
    }
}
