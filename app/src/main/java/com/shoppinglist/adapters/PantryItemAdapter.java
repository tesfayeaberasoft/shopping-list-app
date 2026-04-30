package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.entities.PantryItemEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantryItemAdapter extends RecyclerView.Adapter<PantryItemAdapter.ViewHolder> {
    private List<PantryItemEntity> items = new ArrayList<>();
    private OnPantryItemListener listener;

    public interface OnPantryItemListener {
        void onItemClick(PantryItemEntity item);
        void onEditClick(PantryItemEntity item);
        void onDeleteClick(PantryItemEntity item);
        void onIncreaseQuantity(PantryItemEntity item);
        void onDecreaseQuantity(PantryItemEntity item);
    }

    public PantryItemAdapter(OnPantryItemListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItemEntity> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItemEntity item = items.get(position);
        
        holder.tvName.setText(item.getName());
        holder.tvCurrentQuantity.setText(String.format(Locale.getDefault(), "%.1f", item.getCurrentQuantity()));
        holder.tvUnit.setText(item.getUnit());
        holder.tvCategory.setText(item.getCategory());
        holder.tvLocation.setText(item.getLocation());

        // Show status indicator
        if (item.isExpired()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(R.string.expired);
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else if (item.isExpiringSoon()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(R.string.expires_soon);
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if (item.isLowStock()) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setText(R.string.low_stock);
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatus.setVisibility(View.GONE);
        }

        // Show expiry date if set
        if (item.getExpiryDate() > 0) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            holder.tvExpiry.setText("Expires: " + sdf.format(new Date(item.getExpiryDate())));
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(item);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(item);
        });

        holder.btnIncrease.setOnClickListener(v -> {
            if (listener != null) listener.onIncreaseQuantity(item);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (listener != null) listener.onDecreaseQuantity(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCurrentQuantity, tvUnit, tvCategory, tvLocation, tvStatus, tvExpiry;
        Button btnIncrease, btnDecrease;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvCurrentQuantity = itemView.findViewById(R.id.tv_current_quantity);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvLocation = itemView.findViewById(R.id.tv_location);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvExpiry = itemView.findViewById(R.id.tv_expiry);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
