package com.example.student_loan_app;

import android.app.AlarmManager; import android.app.PendingIntent; import android.content.Context; import android.content.Intent;

public class NotificationScheduler {
    public static void scheduleExpenseNotification(Context context, Expense expense, String expenseId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BillDueReceiver.class);
        intent.putExtra("expenseName", expense.getName());
        intent.putExtra("expenseId", expenseId);
        intent.putExtra("expenseAmount", expense.getAmount());
        long triggerAtMillis = expense.getChargeDate();

        int requestCode = expenseId.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    public static void scheduleLoanRepaymentNotification(Context context, double repaymentAmount) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, LoanRepaymentReceiver.class);
        intent.putExtra("repaymentAmount", repaymentAmount);
        long triggerAtMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000;

        int requestCode = 1000;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
    }

}