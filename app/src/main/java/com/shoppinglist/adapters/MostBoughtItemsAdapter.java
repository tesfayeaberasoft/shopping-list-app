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

public class MostBoughtItemsAdapter extends RecyclerView.Adapter<MostBoughtItemsAdapter.ViewHolder> {
    private List<ShoppingHistoryDao.MostBoughtItem> items = new ArrayList<>();

    public void setItems(List<ShoppingHistoryDao.MostBoughtItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_most_bought, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingHistoryDao.MostBoughtItem item = items.get(position);
        
        holder.tvItemName.setText(item.item_name);
        holder.tvCategory.setText(item.category);
        holder.tvTotalQuantity.setText(String.format("%.1f total", item.total_quantity));
        holder.tvPurchaseCount.setText(String.format("%d times", item.purchase_count));
        holder.tvAveragePrice.setText(String.format("$%.2f avg", item.avg_price));
        holder.tvRank.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvItemName, tvCategory, tvTotalQuantity, tvPurchaseCount, tvAveragePrice;

        ViewHolder(View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTotalQuantity = itemView.findViewById(R.id.tv_total_quantity);
            tvPurchaseCount = itemView.findViewById(R.id.tv_purchase_count);
            tvAveragePrice = itemView.findViewById(R.id.tv_average_price);
        }
    }
}