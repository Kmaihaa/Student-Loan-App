package com.example.student_loan_app;

import android.content.BroadcastReceiver; import android.content.Context; import android.content.Intent; import androidx.core.app.NotificationCompat; import androidx.core.app.NotificationManagerCompat;

public class BillDueReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String expenseName = intent.getStringExtra("expenseName");
        double expenseAmount = intent.getDoubleExtra("expenseAmount", 0);
        String channelId = "expense_channel";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications)  // use your notification icon
                .setContentTitle("Bill Due: " + expenseName)
                .setContentText("Amount: $" + String.format("%.2f", expenseAmount))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // Use a unique id for this notification
        notificationManager.notify(expenseName.hashCode(), builder.build());
    }
}
