package com.shoppinglist.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.entities.ShoppingListEntity;
import com.shoppinglist.repository.LocalRepository;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShoppingListsViewModel extends AndroidViewModel {
    private LocalRepository localRepository;
    private SessionManager sessionManager;
    private LiveData<List<ShoppingListEntity>> lists;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ShoppingListsViewModel(Application application) {
        super(application);
        localRepository = new LocalRepository(application);
        sessionManager = new SessionManager(application);
        if (sessionManager.isLoggedIn()) {
            lists = localRepository.getListsByOwner(sessionManager.getUser().getId());
        }
    }

    public LiveData<List<ShoppingListEntity>> getLists() { return lists; }

    public void createList(String name) {
        executor.execute(() -> {
            String id = UUID.randomUUID().toString();
            long now = System.currentTimeMillis();
            String ownerId = sessionManager.getUser().getId();
            ShoppingListEntity list = new ShoppingListEntity(id, name, now, ownerId, "{}");
            localRepository.insertList(list);
        });
    }

    public void deleteList(ShoppingListEntity list) {
        executor.execute(() -> {
            localRepository.deleteList(list);
            localRepository.deleteAllItemsByListId(list.getId());
        });
    }
    
    public int getItemCountForList(String listId) {
        return localRepository.getItemCountForList(listId);
    }
    
    public int getCompletedItemCountForList(String listId) {
        return localRepository.getCompletedItemCountForList(listId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
