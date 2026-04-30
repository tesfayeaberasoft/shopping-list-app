package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.repository.LocalRepository;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShoppingItemsViewModel extends AndroidViewModel {
    private LocalRepository localRepository;
    private String listId;
    private LiveData<List<ShoppingItemEntity>> items;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ShoppingItemsViewModel(Application application, String listId) {
        super(application);
        localRepository = new LocalRepository(application);
        this.listId = listId;
        items = localRepository.getItemsByListId(listId);
    }

    public LiveData<List<ShoppingItemEntity>> getItems() { return items; }

    public LiveData<Double> getBudget() {
        return localRepository.getListBudget(listId);
    }

    public void setBudget(double budget) {
        executor.execute(() -> localRepository.updateListBudget(listId, budget));
    }

    public void addItem(String name, int quantity, String unit, String category, int priority, String note, double price) {
        executor.execute(() -> {
            String id = UUID.randomUUID().toString();
            long now = System.currentTimeMillis();
            int maxOrder = 0;
            List<ShoppingItemEntity> current = localRepository.getItemsByListIdSync(listId);
            if (current != null) {
                for (ShoppingItemEntity item : current) {
                    if (item.getOrderIndex() > maxOrder) maxOrder = item.getOrderIndex();
                }
            }
            ShoppingItemEntity item = new ShoppingItemEntity(
                    id, listId, name, quantity, unit, category, priority, note, false, now, maxOrder + 1);
            item.setPrice(price);
            localRepository.insertItem(item);
        });
    }

    public void updateItem(ShoppingItemEntity item) {
        executor.execute(() -> localRepository.updateItem(item));
    }

    public void deleteItem(ShoppingItemEntity item) {
        executor.execute(() -> localRepository.deleteItem(item));
    }

    public void togglePurchased(ShoppingItemEntity item) {
        executor.execute(() -> {
            boolean newStatus = !item.isPurchased();
            item.setPurchased(newStatus);
            localRepository.updateItem(item);
        });
    }

    public void reorderItems(List<ShoppingItemEntity> newOrder) {
        executor.execute(() -> {
            for (int i = 0; i < newOrder.size(); i++) {
                localRepository.updateItemOrderIndex(newOrder.get(i).getId(), i);
            }
        });
    }

    public void clearCompleted() {
        executor.execute(() -> {
            List<ShoppingItemEntity> current = localRepository.getItemsByListIdSync(listId);
            if (current != null) {
                for (ShoppingItemEntity item : current) {
                    if (item.isPurchased()) {
                        localRepository.deleteItem(item);
                    }
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
