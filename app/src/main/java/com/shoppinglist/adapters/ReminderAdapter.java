package com.shoppinglist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.database.entities.ReminderEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<ReminderEntity> reminders = new ArrayList<>();
    private OnReminderListener listener;

    public interface OnReminderListener {
        void onReminderClick(ReminderEntity reminder);
        void onEditClick(ReminderEntity reminder);
        void onDeleteClick(ReminderEntity reminder);
        void onToggleActive(ReminderEntity reminder, boolean isActive);
    }

    public ReminderAdapter(OnReminderListener listener) {
        this.listener = listener;
    }

    public void setReminders(List<ReminderEntity> reminders) {
        this.reminders = reminders != null ? reminders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reminder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReminderEntity reminder = reminders.get(position);
        
        // Set icon based on reminder type
        String icon = getIconForType(reminder.getReminderType());
        holder.tvIcon.setText(icon);
        
        holder.tvTitle.setText(reminder.getTitle());
        holder.tvMessage.setText(reminder.getMessage());
        
        // Format scheduled time
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        holder.tvScheduledTime.setText(sdf.format(new Date(reminder.getScheduledTime())));
        
        // Format repeat interval
        holder.tvRepeatInterval.setText(formatRepeatInterval(reminder.getRepeatInterval()));
        
        // Set active switch
        holder.switchActive.setChecked(reminder.isActive());
        holder.switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggleActive(reminder, isChecked);
            }
        });
        
        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReminderClick(reminder);
        });
        
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(reminder);
        });
        
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(reminder);
        });
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    private String getIconForType(String type) {
        switch (type) {
            case "TIME_BASED":
                return "⏰";
            case "LOW_STOCK":
                return "⚠️";
            case "SHOPPING_DAY":
                return "🛒";
            case "EXPIRY":
                return "📆";
            default:
                return "🔔";
        }
    }

    private String formatRepeatInterval(String interval) {
        switch (interval) {
            case "DAILY":
                return "Daily";
            case "WEEKLY":
                return "Weekly";
            case "MONTHLY":
                return "Monthly";
            case "NONE":
            default:
                return "No Repeat";
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIcon, tvTitle, tvMessage, tvScheduledTime, tvRepeatInterval;
        SwitchCompat switchActive;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_reminder_icon);
            tvTitle = itemView.findViewById(R.id.tv_reminder_title);
            tvMessage = itemView.findViewById(R.id.tv_reminder_message);
            tvScheduledTime = itemView.findViewById(R.id.tv_scheduled_time);
            tvRepeatInterval = itemView.findViewById(R.id.tv_repeat_interval);
            switchActive = itemView.findViewById(R.id.switch_active);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
