package com.example.student_loan_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class LoanRepaymentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        double repaymentAmount = intent.getDoubleExtra("repaymentAmount", 0);
        String channelId = "loan_channel";
        SharedPreferences prefs = context.getSharedPreferences("loanRepayments", Context.MODE_PRIVATE);
        int paymentsMade = prefs.getInt("paymentsMade", 0) + 1;
        double totalRepaid = prefs.getFloat("totalRepaid", 0) + (float) repaymentAmount;
        prefs.edit().putInt("paymentsMade", paymentsMade).putFloat("totalRepaid", (float) totalRepaid).apply();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Loan Repayment Due")
                .setContentText("Repayment Amount: $" + String.format("%.2f", repaymentAmount))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1000, builder.build());
    }
}