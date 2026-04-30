package com.shoppinglist.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shoppinglist.R;
import com.shoppinglist.adapters.ItemAdapter;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.fragments.AddEditItemFragment;
import com.shoppinglist.fragments.ShareListFragment;
import com.shoppinglist.utils.LocaleHelper;
import com.shoppinglist.utils.SortUtils;
import com.shoppinglist.viewmodels.ShoppingItemsViewModel;
import com.shoppinglist.viewmodels.ShoppingItemsViewModelFactory;
import com.shoppinglist.viewmodels.ShoppingHistoryViewModel;
import java.util.List;

public class ListDetailActivity extends AppCompatActivity implements ItemAdapter.OnItemActionListener {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ShoppingItemsViewModel viewModel;
    private ShoppingHistoryViewModel historyViewModel;
    private String listId;
    private String listName;
    private int currentSort = SortUtils.SORT_DATE;
    private ItemTouchHelper itemTouchHelper;
    private MaterialCardView budgetCard;
    private TextView tvBudget, tvSpent, tvRemaining, tvBudgetStatus;
    private double currentBudget = 0.0;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply saved locale to this activity's context
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_detail);
        listId = getIntent().getStringExtra("list_id");
        listName = getIntent().getStringExtra("list_name");
        setTitle(listName);

        viewModel = new ViewModelProvider(this, new ShoppingItemsViewModelFactory(getApplication(), listId))
                .get(ShoppingItemsViewModel.class);
        historyViewModel = new ViewModelProvider(this).get(ShoppingHistoryViewModel.class);

        recyclerView = findViewById(R.id.recycler_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemAdapter(this);
        recyclerView.setAdapter(adapter);

        budgetCard = findViewById(R.id.budget_card);
        tvBudget = findViewById(R.id.tv_budget);
        tvSpent = findViewById(R.id.tv_spent);
        tvRemaining = findViewById(R.id.tv_remaining);
        tvBudgetStatus = findViewById(R.id.tv_budget_status);
        findViewById(R.id.btn_set_budget).setOnClickListener(v -> showSetBudgetDialog());

        viewModel.getItems().observe(this, items -> {
            if (items != null) {
                List<ShoppingItemEntity> sorted = SortUtils.sort(items, currentSort);
                adapter.submitList(sorted, currentSort == SortUtils.SORT_CATEGORY);
                updateBudgetSummary(items);
            }
            findViewById(R.id.empty_view).setVisibility(items == null || items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getBudget().observe(this, budget -> {
            if (budget != null) {
                currentBudget = budget;
                updateBudgetDisplay();
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                if (adapter.onItemMove(from, to)) {
                    List<ShoppingItemEntity> ordered = adapter.getCurrentItemList();
                    viewModel.reorderItems(ordered);
                    return true;
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public boolean isLongPressDragEnabled() { return false; }
        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        findViewById(R.id.fab_add_item).setOnClickListener(v -> showAddItemDialog(null));
    }

    private void showAddItemDialog(ShoppingItemEntity existing) {
        AddEditItemFragment fragment = new AddEditItemFragment();
        if (existing != null) fragment.setExistingItem(existing);
        fragment.setListener((name, qty, unit, category, priority, note, price, existingItem) -> {
            if (existingItem == null) {
                viewModel.addItem(name, qty, unit, category, priority, note, price);
            } else {
                existingItem.setName(name);
                existingItem.setQuantity(qty);
                existingItem.setUnit(unit);
                existingItem.setCategory(category);
                existingItem.setPriority(priority);
                existingItem.setNote(note);
                existingItem.setPrice(price);
                viewModel.updateItem(existingItem);
            }
        });
        fragment.show(getSupportFragmentManager(), "add_edit_item");
    }

    @Override
    public void onItemClick(ShoppingItemEntity item) {
        showAddItemDialog(item);
    }

    @Override
    public void onDeleteClick(ShoppingItemEntity item) {
        viewModel.deleteItem(item);
    }

    @Override
    public void onPurchasedToggle(ShoppingItemEntity item, boolean isPurchased) {
        viewModel.togglePurchased(item);
        
        // Record purchase in history if item is being marked as purchased
        if (isPurchased && item.getPrice() > 0) {
            historyViewModel.recordPurchase(
                listId,
                listName != null ? listName : "Shopping List",
                item.getName(),
                item.getCategory(),
                item.getQuantity(),
                item.getUnit(),
                item.getPrice(),
                null, // store name - could be added later
                item.getNote()
            );
        }
    }

    @Override
    public void onDragStarted(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_detail_menu, menu);
        
        // Force icons to show in overflow menu
        if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
            try {
                java.lang.reflect.Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            } catch (Exception e) {
                android.util.Log.e("ListDetailActivity", "Could not show menu icons", e);
            }
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_category) { currentSort = SortUtils.SORT_CATEGORY; refreshSort(); }
        else if (id == R.id.action_sort_alpha) { currentSort = SortUtils.SORT_ALPHA; refreshSort(); }
        else if (id == R.id.action_sort_priority) { currentSort = SortUtils.SORT_PRIORITY; refreshSort(); }
        else if (id == R.id.action_sort_date) { currentSort = SortUtils.SORT_DATE; refreshSort(); }
        else if (id == R.id.action_clear_completed) viewModel.clearCompleted();
        else if (id == R.id.action_share) shareList();
        return super.onOptionsItemSelected(item);
    }

    private void refreshSort() {
        viewModel.getItems().observe(this, items -> {
            if (items != null) adapter.submitList(SortUtils.sort(items, currentSort), currentSort == SortUtils.SORT_CATEGORY);
        });
    }

    private void shareList() {
        String shareCode = java.util.UUID.randomUUID().toString().substring(0, 6);
        new com.shoppinglist.repository.CloudRepository().createShareLink(shareCode, listId, new com.shoppinglist.auth.SessionManager(this).getUser().getId());
        ShareListFragment frag = new ShareListFragment();
        frag.setShareCode(shareCode);
        frag.show(getSupportFragmentManager(), "share");
    }

    private void showSetBudgetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_set_budget, null);
        EditText etBudget = dialogView.findViewById(R.id.et_budget);
        if (currentBudget > 0) {
            etBudget.setText(String.valueOf(currentBudget));
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.set_budget)
                .setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String budgetText = etBudget.getText().toString().trim();
                    if (!budgetText.isEmpty()) {
                        try {
                            double budget = Double.parseDouble(budgetText);
                            viewModel.setBudget(budget);
                            currentBudget = budget;
                            updateBudgetDisplay();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateBudgetSummary(List<ShoppingItemEntity> items) {
        double totalCost = 0.0;
        for (ShoppingItemEntity item : items) {
            totalCost += item.getPrice();
        }

        tvSpent.setText(String.format("$%.2f", totalCost));

        if (currentBudget > 0) {
            double remaining = currentBudget - totalCost;
            tvRemaining.setText(String.format("$%.2f", remaining));

            if (remaining < 0) {
                tvBudgetStatus.setText(R.string.over_budget);
                tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvBudgetStatus.setText(R.string.within_budget);
                tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        } else {
            tvRemaining.setText("--");
            tvBudgetStatus.setText(R.string.no_budget_set);
            tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void updateBudgetDisplay() {
        if (currentBudget > 0) {
            tvBudget.setText(String.format("$%.2f", currentBudget));
        } else {
            tvBudget.setText("--");
        }
    }
}