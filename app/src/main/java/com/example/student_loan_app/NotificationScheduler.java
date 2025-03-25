package com.example.student_loan_app;

import android.app.AlarmManager; import android.app.PendingIntent; import android.content.Context; import android.content.Intent;

public class NotificationScheduler {
    public static void scheduleExpenseNotification(Context context, Expense expense, String expenseId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BillDueReceiver.class);
        intent.putExtra("expenseName", expense.getName());
        intent.putExtra("expenseId", expenseId);
        intent.putExtra("expenseAmount", expense.getAmount());
        // Use the chargeDate as the trigger time
        long triggerAtMillis = expense.getChargeDate();

        // Use a unique request code based on the expenseId hash
        int requestCode = expenseId.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    public static void scheduleLoanRepaymentNotification(Context context, double repaymentAmount) {
        // For loan repayments, you might decide on a fixed schedule (e.g., monthly on a given day)
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LoanRepaymentReceiver.class);
        intent.putExtra("repaymentAmount", repaymentAmount);
        // Determine trigger time (for demo, set 24 hours later)
        long triggerAtMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        int requestCode = 1000; // Fixed request code for the monthly repayment notification

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
    }

}