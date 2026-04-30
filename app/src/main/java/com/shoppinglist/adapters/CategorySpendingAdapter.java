package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.dao.ShoppingHistoryDao;
import java.util.ArrayList;
import java.util.List;

public class CategorySpendingAdapter extends RecyclerView.Adapter<CategorySpendingAdapter.ViewHolder> {
    private List<ShoppingHistoryDao.CategorySpending> items = new ArrayList<>();
    private double maxSpending = 0;

    public void setItems(List<ShoppingHistoryDao.CategorySpending> items) {
        this.items = items != null ? items : new ArrayList<>();
        
        // Calculate max spending for progress bars
        maxSpending = 0;
        for (ShoppingHistoryDao.CategorySpending item : this.items) {
            if (item.total_spent > maxSpending) {
                maxSpending = item.total_spent;
            }
        }
        
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_spending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingHistoryDao.CategorySpending item = items.get(position);
        
        holder.tvCategory.setText(item.category);
        holder.tvTotalSpent.setText(String.format("$%.2f", item.total_spent));
        holder.tvItemCount.setText(String.format("%d items", item.item_count));
        
        // Set progress bar
        if (maxSpending > 0) {
            int progress = (int) ((item.total_spent / maxSpending) * 100);
            holder.progressBar.setProgress(progress);
        } else {
            holder.progressBar.setProgress(0);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTotalSpent, tvItemCount;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTotalSpent = itemView.findViewById(R.id.tv_total_spent);
            tvItemCount = itemView.findViewById(R.id.tv_item_count);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}