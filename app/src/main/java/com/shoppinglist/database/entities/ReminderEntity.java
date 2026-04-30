package com.shoppinglist.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class ReminderEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "list_id")
    private String listId; // Optional - for list-specific reminders

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "message")
    private String message;

    @ColumnInfo(name = "reminder_type")
    private String reminderType; // "TIME_BASED", "LOW_STOCK", "SHOPPING_DAY", "EXPIRY"

    @ColumnInfo(name = "scheduled_time")
    private long scheduledTime; // When to trigger the reminder

    @ColumnInfo(name = "repeat_interval")
    private String repeatInterval; // "NONE", "DAILY", "WEEKLY", "MONTHLY"

    @ColumnInfo(name = "is_active")
    private boolean isActive;

    @ColumnInfo(name = "is_triggered")
    private boolean isTriggered;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public ReminderEntity() {}

    @androidx.room.Ignore
    public ReminderEntity(String id, String userId, String listId, String title, String message,
                         String reminderType, long scheduledTime, String repeatInterval,
                         boolean isActive, boolean isTriggered, long createdAt, long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.listId = listId;
        this.title = title;
        this.message = message;
        this.reminderType = reminderType;
        this.scheduledTime = scheduledTime;
        this.repeatInterval = repeatInterval;
        this.isActive = isActive;
        this.isTriggered = isTriggered;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getListId() { return listId; }
    public void setListId(String listId) { this.listId = listId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReminderType() { return reminderType; }
    public void setReminderType(String reminderType) { this.reminderType = reminderType; }

    public long getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(long scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getRepeatInterval() { return repeatInterval; }
    public void setRepeatInterval(String repeatInterval) { this.repeatInterval = repeatInterval; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isTriggered() { return isTriggered; }
    public void setTriggered(boolean triggered) { isTriggered = triggered; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    // Helper methods
    public boolean shouldTrigger() {
        return isActive && !isTriggered && System.currentTimeMillis() >= scheduledTime;
    }

    public boolean isRepeating() {
        return !repeatInterval.equals("NONE");
    }
}
