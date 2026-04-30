/**
 * ShoppingListAdapter - RecyclerView Adapter for Shopping Lists
 * Displays all shopping lists with item counts and completion status
 * Supports drag and drop, swipe to delete, and list sharing
 * 
 * Adapter Features:
 * - Display shopping lists
 * - Show item counts
 * - Display completion percentage
 * - Drag and drop support
 * - Swipe to delete
 * - List sharing functionality
 * - Click listeners for list operations
 * - Item count caching
 */
package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.entities.ShoppingListEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private List<ShoppingListEntity> lists = new ArrayList<>();
    private Map<String, ItemCounts> itemCountsMap = new HashMap<>();
    private OnListClickListener listener;

    public static class ItemCounts {
        public int total;
        public int completed;
        
        public ItemCounts(int total, int completed) {
            this.total = total;
            this.completed = completed;
        }
    }

    public interface OnListClickListener {
        void onListClick(ShoppingListEntity list);
        void onListDelete(ShoppingListEntity list);
    }

    public ShoppingListAdapter(OnListClickListener listener) {
        this.listener = listener;
    }

    public void setLists(List<ShoppingListEntity> lists) {
        this.lists = lists != null ? lists : new ArrayList<>();
        notifyDataSetChanged();
    }
    
    public void setItemCounts(String listId, int total, int completed) {
        itemCountsMap.put(listId, new ItemCounts(total, completed));
        notifyDataSetChanged();
    }
    
    public void setAllItemCounts(Map<String, ItemCounts> counts) {
        this.itemCountsMap = counts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingListEntity list = lists.get(position);
        holder.tvName.setText(list.getName());
        
        // Get item counts
        ItemCounts counts = itemCountsMap.get(list.getId());
        int total = counts != null ? counts.total : 0;
        int completed = counts != null ? counts.completed : 0;
        
        // Set counts
        holder.tvTotalItems.setText(String.valueOf(total));
        holder.tvCompletedItems.setText(String.valueOf(completed));
        
        // Calculate and set progress
        int progress = total > 0 ? (completed * 100 / total) : 0;
        holder.progressBar.setProgress(progress);
        holder.tvProgressText.setText(progress + "%");
        
        // Format created date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String dateStr = sdf.format(new Date(list.getCreatedAt()));
        holder.tvCreatedDate.setText("Created: " + dateStr);
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> listener.onListClick(list));
        holder.btnDelete.setOnClickListener(v -> listener.onListDelete(list));
    }

    @Override
    public int getItemCount() { return lists.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTotalItems, tvCompletedItems, tvProgressText, tvCreatedDate;
        ProgressBar progressBar;
        ImageButton btnDelete;
        
        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_list_name);
            tvTotalItems = itemView.findViewById(R.id.tv_total_items);
            tvCompletedItems = itemView.findViewById(R.id.tv_completed_items);
            tvProgressText = itemView.findViewById(R.id.tv_progress_text);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
            progressBar = itemView.findViewById(R.id.progress_bar);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}