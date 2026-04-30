package com.shoppinglist.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_history")
public class ShoppingHistoryEntity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name = "list_id")
    private String listId;

    @ColumnInfo(name = "list_name")
    private String listName;

    @ColumnInfo(name = "item_name")
    private String itemName;

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "quantity")
    private double quantity;

    @ColumnInfo(name = "unit")
    private String unit;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "total_cost")
    private double totalCost; // quantity * price

    @ColumnInfo(name = "purchase_date")
    private long purchaseDate; // When item was marked as purchased

    @ColumnInfo(name = "store_name")
    private String storeName; // Optional - where it was bought

    @ColumnInfo(name = "notes")
    private String notes;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public ShoppingHistoryEntity() {}

    @androidx.room.Ignore
    public ShoppingHistoryEntity(String id, String userId, String listId, String listName,
                                String itemName, String category, double quantity, String unit,
                                double price, double totalCost, long purchaseDate, String storeName,
                                String notes, long createdAt) {
        this.id = id;
        this.userId = userId;
        this.listId = listId;
        this.listName = listName;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.totalCost = totalCost;
        this.purchaseDate = purchaseDate;
        this.storeName = storeName;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    // Getters and setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getListId() { return listId; }
    public void setListId(String listId) { this.listId = listId; }

    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public long getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(long purchaseDate) { this.purchaseDate = purchaseDate; }

    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Helper methods
    public String getFormattedQuantity() {
        if (quantity == (int) quantity) {
            return String.valueOf((int) quantity);
        } else {
            return String.valueOf(quantity);
        }
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getFormattedTotalCost() {
        return String.format("$%.2f", totalCost);
    }
}