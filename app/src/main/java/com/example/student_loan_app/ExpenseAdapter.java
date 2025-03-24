package com.example.student_loan_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnExpenseItemClickListener {
        void onEditExpense(ExpenseItem expenseItem);
        void onDeleteExpense(ExpenseItem expenseItem);
    }

    private List<ExpenseItem> expenseItemList;
    private OnExpenseItemClickListener listener;

    public ExpenseAdapter(List<ExpenseItem> expenseItemList, OnExpenseItemClickListener listener) {
        this.expenseItemList = expenseItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        ExpenseItem expenseItem = expenseItemList.get(position);
        Expense expense = expenseItem.getExpense();
        holder.textViewName.setText(expense.getName());
        holder.textViewAmount.setText("$" + String.format("%.2f", expense.getAmount()));

        // On short click: edit
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditExpense(expenseItem);
            }
        });

        // On long click: delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteExpense(expenseItem);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenseItemList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewAmount;
        // Optionally, add a delete button here if desired

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewExpenseName);
            textViewAmount = itemView.findViewById(R.id.textViewExpenseAmount);
        }
    }
}
