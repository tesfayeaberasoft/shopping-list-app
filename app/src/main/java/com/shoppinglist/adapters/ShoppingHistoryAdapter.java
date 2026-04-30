package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.entities.ShoppingHistoryEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShoppingHistoryAdapter extends RecyclerView.Adapter<ShoppingHistoryAdapter.ViewHolder> {
    private List<ShoppingHistoryEntity> historyItems = new ArrayList<>();
    private OnHistoryItemListener listener;

    public interface OnHistoryItemListener {
        void onHistoryItemClick(ShoppingHistoryEntity item);
    }

    public ShoppingHistoryAdapter(OnHistoryItemListener listener) {
        this.listener = listener;
    }

    public void setHistoryItems(List<ShoppingHistoryEntity> items) {
        this.historyItems = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingHistoryEntity item = historyItems.get(position);
        
        holder.tvItemName.setText(item.getItemName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvQuantity.setText(item.getFormattedQuantity() + " " + (item.getUnit() != null ? item.getUnit() : ""));
        holder.tvPrice.setText(item.getFormattedPrice());
        holder.tvTotalCost.setText(item.getFormattedTotalCost());
        
        // Format purchase date
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        holder.tvPurchaseDate.setText(sdf.format(new Date(item.getPurchaseDate())));
        
        // Set list name
        holder.tvListName.setText(item.getListName());
        
        // Set store name if available
        if (item.getStoreName() != null && !item.getStoreName().isEmpty()) {
            holder.tvStoreName.setText(item.getStoreName());
            holder.tvStoreName.setVisibility(View.VISIBLE);
        } else {
            holder.tvStoreName.setVisibility(View.GONE);
        }
        
        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHistoryItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvCategory, tvQuantity, tvPrice, tvTotalCost;
        TextView tvPurchaseDate, tvListName, tvStoreName;

        ViewHolder(View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTotalCost = itemView.findViewById(R.id.tv_total_cost);
            tvPurchaseDate = itemView.findViewById(R.id.tv_purchase_date);
            tvListName = itemView.findViewById(R.id.tv_list_name);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
        }
    }
}