package com.shoppinglist.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_items")
public class ShoppingItemEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "list_id")
    private String listId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "priority")
    private int priority; // 1=High,2=Medium,3=Low

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "is_purchased")
    private boolean isPurchased;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "order_index")
    private int orderIndex;
    
    // NEW: Budget tracking fields
    @ColumnInfo(name = "price", defaultValue = "0.0")
    private double price;

    public ShoppingItemEntity() {}

    @androidx.room.Ignore
    public ShoppingItemEntity(String id, String listId, String name, int quantity, String unit, String category,
                              int priority, String note, boolean isPurchased, long createdAt, int orderIndex) {
        this.id = id;
        this.listId = listId;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.priority = priority;
        this.note = note;
        this.isPurchased = isPurchased;
        this.createdAt = createdAt;
        this.orderIndex = orderIndex;
        this.price = 0.0; // Default price
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getListId() { return listId; }
    public void setListId(String listId) { this.listId = listId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public boolean isPurchased() { return isPurchased; }
    public void setPurchased(boolean purchased) { isPurchased = purchased; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    
    // NEW: Price getter and setter
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    // NEW: Calculate total price for this item
    public double getTotalPrice() { return price * quantity; }
}