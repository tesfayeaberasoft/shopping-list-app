package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.dao.ShoppingHistoryDao;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthlySpendingAdapter extends RecyclerView.Adapter<MonthlySpendingAdapter.ViewHolder> {
    private List<ShoppingHistoryDao.MonthlySpending> items = new ArrayList<>();

    public void setItems(List<ShoppingHistoryDao.MonthlySpending> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_monthly_spending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingHistoryDao.MonthlySpending item = items.get(position);
        
        // Format month (e.g., "2024-01" -> "January 2024")
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            String formattedMonth = outputFormat.format(inputFormat.parse(item.month));
            holder.tvMonth.setText(formattedMonth);
        } catch (Exception e) {
            holder.tvMonth.setText(item.month);
        }
        
        holder.tvTotalSpent.setText(String.format("$%.2f", item.total_spent));
        holder.tvItemCount.setText(String.format("%d items", item.item_count));
        
        // Calculate average per item
        double avgPerItem = item.item_count > 0 ? item.total_spent / item.item_count : 0;
        holder.tvAveragePerItem.setText(String.format("$%.2f avg/item", avgPerItem));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvTotalSpent, tvItemCount, tvAveragePerItem;

        ViewHolder(View itemView) {
            super(itemView);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvTotalSpent = itemView.findViewById(R.id.tv_total_spent);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvAveragePerItem = itemView.findViewById(R.id.tv_average_per_item);
        }
    }
}