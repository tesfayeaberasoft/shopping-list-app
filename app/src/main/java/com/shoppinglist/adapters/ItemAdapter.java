package com.shoppinglist.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private List<Object> items; // either String (category header) or ShoppingItemEntity
    private OnItemActionListener listener;
    private boolean showHeaders = false;

    public interface OnItemActionListener {
        void onItemClick(ShoppingItemEntity item);
        void onDeleteClick(ShoppingItemEntity item);
        void onPurchasedToggle(ShoppingItemEntity item, boolean isPurchased);
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    public ItemAdapter(OnItemActionListener listener) {
        this.listener = listener;
        this.items = new ArrayList<>();
    }

    public void submitList(List<ShoppingItemEntity> itemList, boolean groupByCategory) {
        this.showHeaders = groupByCategory;
        items.clear();
        if (groupByCategory && itemList != null) {
            // Group by category
            String currentCategory = null;
            for (ShoppingItemEntity item : itemList) {
                if (!item.getCategory().equals(currentCategory)) {
                    currentCategory = item.getCategory();
                    items.add(currentCategory);
                }
                items.add(item);
            }
        } else if (itemList != null) {
            items.addAll(itemList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_header, parent, false);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_item, parent, false);
            return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String category = (String) items.get(position);
            ((HeaderViewHolder) holder).tvHeader.setText(category);
        } else if (holder instanceof ItemViewHolder) {
            ShoppingItemEntity item = (ShoppingItemEntity) items.get(position);
            ItemViewHolder vh = (ItemViewHolder) holder;
            
            // Name
            vh.tvName.setText(item.getName());
            
            // Quantity & Unit
            vh.tvQuantity.setText(String.format("%d %s", item.getQuantity(), item.getUnit()));
            
            // Category
            vh.tvCategory.setText(item.getCategory());
            
            // Priority
            String priorityText;
            String priorityIcon;
            switch (item.getPriority()) {
                case 1:
                    priorityText = vh.itemView.getContext().getString(R.string.high);
                    priorityIcon = "⭐⭐⭐";
                    break;
                case 2:
                    priorityText = vh.itemView.getContext().getString(R.string.medium);
                    priorityIcon = "⭐⭐";
                    break;
                default:
                    priorityText = vh.itemView.getContext().getString(R.string.low);
                    priorityIcon = "⭐";
                    break;
            }
            vh.tvPriority.setText(priorityText);
            vh.tvPriorityIcon.setText(priorityIcon);
            
            // Price
            if (item.getPrice() > 0) {
                vh.tvPrice.setText(String.format("$%.2f", item.getPrice()));
            } else {
                vh.tvPrice.setText("--");
            }
            
            // Purchased status
            vh.cbPurchased.setChecked(item.isPurchased());
            if (item.isPurchased()) {
                vh.tvName.setPaintFlags(vh.tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                vh.itemView.setAlpha(0.6f);
            } else {
                vh.tvName.setPaintFlags(vh.tvName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                vh.itemView.setAlpha(1.0f);
            }
            
            // Click listeners
            vh.itemView.setOnClickListener(v -> listener.onItemClick(item));
            vh.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
            vh.cbPurchased.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onPurchasedToggle(item, isChecked));
            vh.ivDragHandle.setOnTouchListener((v, event) -> {
                if (event.getActionMasked() == android.view.MotionEvent.ACTION_DOWN) {
                    listener.onDragStarted(holder);
                }
                return false;
            });
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // Drag and drop methods
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        // Return true if items are of TYPE_ITEM only
        return items.get(fromPosition) instanceof ShoppingItemEntity && items.get(toPosition) instanceof ShoppingItemEntity;
    }

    @Override
    public void onItemDismiss(int position) {
        // Not used
    }

    public List<ShoppingItemEntity> getCurrentItemList() {
        List<ShoppingItemEntity> result = new ArrayList<>();
        for (Object obj : items) {
            if (obj instanceof ShoppingItemEntity) {
                result.add((ShoppingItemEntity) obj);
            }
        }
        return result;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbPurchased;
        TextView tvName, tvQuantity, tvCategory, tvPriority, tvPriorityIcon, tvPrice;
        ImageButton btnDelete;
        View ivDragHandle;
        ItemViewHolder(View v) {
            super(v);
            cbPurchased = v.findViewById(R.id.cb_purchased);
            tvName = v.findViewById(R.id.tv_item_name);
            tvQuantity = v.findViewById(R.id.tv_quantity);
            tvCategory = v.findViewById(R.id.tv_category);
            tvPriority = v.findViewById(R.id.tv_priority);
            tvPriorityIcon = v.findViewById(R.id.tv_priority_icon);
            tvPrice = v.findViewById(R.id.tv_price);
            btnDelete = v.findViewById(R.id.btn_delete);
            ivDragHandle = v.findViewById(R.id.iv_drag_handle);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(View v) {
            super(v);
            tvHeader = v.findViewById(R.id.tv_category_header);
        }
    }
}