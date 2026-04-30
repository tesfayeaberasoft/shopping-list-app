package com.shoppinglist.models;

import java.util.HashMap;
import java.util.Map;

public class ShoppingList {
    private String id;
    private String name;
    private long createdAt; // timestamp
    private String ownerId;
    private Map<String, Boolean> sharedWith; // userId -> true (active)

    public ShoppingList() {
        sharedWith = new HashMap<>();
    }

    public ShoppingList(String id, String name, long createdAt, String ownerId) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.sharedWith = new HashMap<>();
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
    public Map<String, Boolean> getSharedWith() { return sharedWith; }
    public void setSharedWith(Map<String, Boolean> sharedWith) { this.sharedWith = sharedWith; }
}