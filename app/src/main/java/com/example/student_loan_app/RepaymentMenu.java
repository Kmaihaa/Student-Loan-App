package com.example.student_loan_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class RepaymentMenu extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repayment_menu);

        Button btnSimulation = findViewById(R.id.btnSimulation);
        Button btnExtraPayment = findViewById(R.id.btnExtraPayment);
        Button btnGoalSetting = findViewById(R.id.btnGoalSetting);

        btnSimulation.setOnClickListener(v -> startActivity(new Intent(RepaymentMenu.this, RepaymentSimulationActivity.class)));
        btnExtraPayment.setOnClickListener(v -> startActivity(new Intent(RepaymentMenu.this, ExtraPaymentActivity.class)));
        btnGoalSetting.setOnClickListener(v -> startActivity(new Intent(RepaymentMenu.this, GoalSettingActivity.class)));
    }
}
