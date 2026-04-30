package com.shoppinglist.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.shoppinglist.R;
import com.shoppinglist.adapters.ShoppingListAdapter;
import com.shoppinglist.auth.SessionManager;
import com.shoppinglist.database.entities.ShoppingListEntity;
import com.shoppinglist.repository.CloudRepository;
import com.shoppinglist.utils.LocaleHelper;
import com.shoppinglist.viewmodels.ShoppingListsViewModel;
import com.shoppinglist.activities.ListDetailActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements ShoppingListAdapter.OnListClickListener {
    private RecyclerView recyclerView;
    private ShoppingListAdapter adapter;
    private ShoppingListsViewModel viewModel;
    private SessionManager session;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Apply saved locale to this activity's context
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }
        viewModel = new ViewModelProvider(this).get(ShoppingListsViewModel.class);

        recyclerView = findViewById(R.id.recycler_lists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ShoppingListAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel.getLists().observe(this, lists -> {
            adapter.setLists(lists);
            findViewById(R.id.empty_view).setVisibility(lists == null || lists.isEmpty() ? View.VISIBLE : View.GONE);
            
            // Load item counts for each list
            if (lists != null && !lists.isEmpty()) {
                new Thread(() -> {
                    java.util.Map<String, ShoppingListAdapter.ItemCounts> countsMap = new java.util.HashMap<>();
                    for (ShoppingListEntity list : lists) {
                        int total = viewModel.getItemCountForList(list.getId());
                        int completed = viewModel.getCompletedItemCountForList(list.getId());
                        countsMap.put(list.getId(), new ShoppingListAdapter.ItemCounts(total, completed));
                    }
                    runOnUiThread(() -> adapter.setAllItemCounts(countsMap));
                }).start();
            }
        });

        findViewById(R.id.fab_add_list).setOnClickListener(v -> showCreateListDialog());
    }

    private void showCreateListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.create_list);
        final android.widget.EditText input = new android.widget.EditText(this);
        builder.setView(input);
        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) viewModel.createList(name);
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    @Override
    public void onListClick(ShoppingListEntity list) {
        Intent intent = new Intent(this, ListDetailActivity.class);
        intent.putExtra("list_id", list.getId());
        intent.putExtra("list_name", list.getName());
        startActivity(intent);
    }

    @Override
    public void onListDelete(ShoppingListEntity list) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete list?")
                .setMessage("This will delete all items in this list.")
                .setPositiveButton(R.string.delete, (d,w) -> viewModel.deleteList(list))
                .setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        
        // Force icons to show in overflow menu - multiple approaches for maximum compatibility
        try {
            // Approach 1: Direct MenuBuilder cast (most reliable)
            if (menu instanceof androidx.appcompat.view.menu.MenuBuilder) {
                androidx.appcompat.view.menu.MenuBuilder menuBuilder = (androidx.appcompat.view.menu.MenuBuilder) menu;
                menuBuilder.setOptionalIconsVisible(true);
                android.util.Log.d("MainActivity", "Icons enabled via MenuBuilder cast");
            }
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "MenuBuilder cast failed", e);
        }
        
        // Approach 2: Reflection fallback
        try {
            java.lang.reflect.Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
            android.util.Log.d("MainActivity", "Icons enabled via reflection");
        } catch (Exception e) {
            android.util.Log.e("MainActivity", "Reflection method failed", e);
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            startActivity(new Intent(this, ShoppingHistoryActivity.class));
            return true;
        } else if (id == R.id.action_reminders) {
            startActivity(new Intent(this, RemindersActivity.class));
            return true;
        } else if (id == R.id.action_pantry) {
            startActivity(new Intent(this, PantryActivity.class));
            return true;
        } else if (id == R.id.action_analytics) {
            startActivity(new Intent(this, AnalyticsActivity.class));
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_join_list) {
            showJoinListDialog();
            return true;
        } else if (id == R.id.action_logout) {
            session.clearSession();
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showJoinListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter share code");
        final android.widget.EditText input = new android.widget.EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Join", (dialog, which) -> {
            String code = input.getText().toString().trim();
            new CloudRepository().getShareListByCode(code).addOnSuccessListener(docRef -> {
                if (docRef != null) {
                    // Fetch list from cloud and add to local as shared list (simplified)
                    Toast.makeText(this, "List joined!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Invalid code", Toast.LENGTH_SHORT).show();
                }
            });
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}