package com.shoppinglist.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pantry_items")
public class PantryItemEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "current_quantity")
    private double currentQuantity;

    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "minimum_quantity")
    private double minimumQuantity; // Threshold for low stock alert

    @ColumnInfo(name = "location")
    private String location; // e.g., "Fridge", "Pantry", "Freezer"

    @ColumnInfo(name = "expiry_date")
    private long expiryDate; // Optional expiry tracking

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public PantryItemEntity() {}

    @androidx.room.Ignore
    public PantryItemEntity(String id, String userId, String name, double currentQuantity, 
                           String unit, String category, double minimumQuantity, 
                           String location, long expiryDate, String notes, 
                           long createdAt, long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.currentQuantity = currentQuantity;
        this.unit = unit;
        this.category = category;
        this.minimumQuantity = minimumQuantity;
        this.location = location;
        this.expiryDate = expiryDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(double currentQuantity) { this.currentQuantity = currentQuantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public double getMinimumQuantity() { return minimumQuantity; }
    public void setMinimumQuantity(double minimumQuantity) { this.minimumQuantity = minimumQuantity; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public long getExpiryDate() { return expiryDate; }
    public void setExpiryDate(long expiryDate) { this.expiryDate = expiryDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isLowStock() {
        return currentQuantity <= minimumQuantity;
    }
    
    public boolean isExpired() {
        return expiryDate > 0 && System.currentTimeMillis() > expiryDate;
    }
    
    public boolean isExpiringSoon() {
        if (expiryDate <= 0) return false;
        long threeDaysInMillis = 3 * 24 * 60 * 60 * 1000L;
        return System.currentTimeMillis() + threeDaysInMillis > expiryDate;
    }
}
