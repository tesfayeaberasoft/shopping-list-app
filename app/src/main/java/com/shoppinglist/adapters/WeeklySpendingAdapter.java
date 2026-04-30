package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.dao.ShoppingHistoryDao;
import java.util.ArrayList;
import java.util.List;

public class WeeklySpendingAdapter extends RecyclerView.Adapter<WeeklySpendingAdapter.ViewHolder> {
    private List<ShoppingHistoryDao.WeeklySpending> items = new ArrayList<>();

    public void setItems(List<ShoppingHistoryDao.WeeklySpending> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weekly_spending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingHistoryDao.WeeklySpending item = items.get(position);
        
        // Format week (e.g., "2024-W01" -> "Week 1, 2024")
        String formattedWeek = formatWeek(item.week);
        holder.tvWeek.setText(formattedWeek);
        
        holder.tvTotalSpent.setText(String.format("$%.2f", item.total_spent));
        holder.tvItemCount.setText(String.format("%d items", item.item_count));
        
        // Calculate average per item
        double avgPerItem = item.item_count > 0 ? item.total_spent / item.item_count : 0;
        holder.tvAveragePerItem.setText(String.format("$%.2f avg/item", avgPerItem));
    }

    private String formatWeek(String week) {
        try {
            // Parse "2024-W01" format
            String[] parts = week.split("-W");
            if (parts.length == 2) {
                String year = parts[0];
                String weekNum = parts[1];
                return String.format("Week %s, %s", weekNum, year);
            }
        } catch (Exception e) {
            // Fall back to original format
        }
        return week;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvWeek, tvTotalSpent, tvItemCount, tvAveragePerItem;

        ViewHolder(View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvTotalSpent = itemView.findViewById(R.id.tv_total_spent);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            tvAveragePerItem = itemView.findViewById(R.id.tv_average_per_item);
        }
    }
}