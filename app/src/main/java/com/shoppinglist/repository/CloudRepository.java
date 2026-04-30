package com.shoppinglist.repository;

import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.database.entities.ShoppingListEntity;
import com.shoppinglist.database.entities.UserEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudRepository {
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public CloudRepository() {
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Sync list up
    public void syncListToCloud(ShoppingListEntity list) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", list.getId());
        data.put("name", list.getName());
        data.put("createdAt", list.getCreatedAt());
        data.put("ownerId", list.getOwnerId());
        data.put("sharedWith", list.getSharedWith());
        firestore.collection("lists").document(list.getId()).set(data, SetOptions.merge());
    }

    // Sync items for a list
    public void syncItemsToCloud(String listId, List<ShoppingItemEntity> items) {
        for (ShoppingItemEntity item : items) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", item.getId());
            data.put("listId", item.getListId());
            data.put("name", item.getName());
            data.put("quantity", item.getQuantity());
            data.put("unit", item.getUnit());
            data.put("category", item.getCategory());
            data.put("priority", item.getPriority());
            data.put("note", item.getNote());
            data.put("isPurchased", item.isPurchased());
            data.put("createdAt", item.getCreatedAt());
            data.put("orderIndex", item.getOrderIndex());
            firestore.collection("items").document(item.getId()).set(data, SetOptions.merge());
        }
    }

    // Upload user profile photo
    public UploadTask uploadProfilePhoto(String userId, Uri imageUri) {
        StorageReference ref = storage.getReference().child("profile_photos/" + userId + ".jpg");
        return ref.putFile(imageUri);
    }

    // Get download URL
    public Task<Uri> getProfilePhotoDownloadUrl(String userId) {
        return storage.getReference().child("profile_photos/" + userId + ".jpg").getDownloadUrl();
    }

    // Update user profile in Firestore
    public void updateUserProfile(UserEntity user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("email", user.getEmail());
        data.put("displayName", user.getDisplayName());
        data.put("photoUrl", user.getPhotoUrl());
        firestore.collection("users").document(user.getId()).set(data, SetOptions.merge());
    }

    // Share list: store share code mapping
    public void createShareLink(String shareCode, String listId, String ownerId) {
        Map<String, Object> data = new HashMap<>();
        data.put("shareCode", shareCode);
        data.put("listId", listId);
        data.put("ownerId", ownerId);
        firestore.collection("shares").document(shareCode).set(data);
    }

    public Task<DocumentReference> getShareListByCode(String shareCode) {
        return firestore.collection("shares").document(shareCode).get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        return task.getResult().getReference();
                    }
                    return null;
                });
    }
}