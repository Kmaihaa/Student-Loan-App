package com.example.student_loan_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<ExpenseItem> upcomingExpenses;
    private SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy", Locale.getDefault());

    public NotificationsAdapter(List<ExpenseItem> upcomingExpenses) {
        this.upcomingExpenses = upcomingExpenses;
    }

    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_expense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.ViewHolder holder, int position) {
        ExpenseItem item = upcomingExpenses.get(position);
        Expense expense = item.getExpense();

        holder.textViewName.setText(expense.getName());
        holder.textViewAmount.setText("$" + String.format("%.2f", expense.getAmount()));

        long chargeDate = expense.getChargeDate();
        if (chargeDate > 0) {
            holder.textViewDueDate.setText("Due: " + sdf.format(chargeDate));
        } else {
            holder.textViewDueDate.setText("No date");
        }
    }

    @Override
    public int getItemCount() {
        return upcomingExpenses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewAmount, textViewDueDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewExpenseName);
            textViewAmount = itemView.findViewById(R.id.textViewExpenseAmount);
            textViewDueDate = itemView.findViewById(R.id.textViewExpenseDueDate);
        }
    }
}
