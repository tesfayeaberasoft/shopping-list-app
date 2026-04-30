package com.shoppinglist.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Map;

@Entity(tableName = "shopping_lists")
public class ShoppingListEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "owner_id")
    private String ownerId;

    @ColumnInfo(name = "shared_with")
    private String sharedWith; // JSON string of Map<String, Boolean>
    
    // NEW: Budget tracking field
    @ColumnInfo(name = "budget", defaultValue = "0.0")
    private double budget;

    // Constructors
    public ShoppingListEntity() {}

    @androidx.room.Ignore
    public ShoppingListEntity(String id, String name, long createdAt, String ownerId, String sharedWith) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.sharedWith = sharedWith;
        this.budget = 0.0; // Default no budget limit
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    public String getSharedWith() { return sharedWith; }
    public void setSharedWith(String sharedWith) { this.sharedWith = sharedWith; }
    
    // NEW: Budget getter and setter
    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }
}