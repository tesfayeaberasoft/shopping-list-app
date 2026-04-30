package com.shoppinglist.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.adapters.ExpensiveItemsAdapter;
import com.shoppinglist.utils.LocaleHelper;
import com.shoppinglist.viewmodels.AnalyticsViewModel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AnalyticsActivity extends AppCompatActivity {
    private AnalyticsViewModel viewModel;
    private TextView tvTotalSpent, tvAveragePerList, tvTotalLists, tvTotalItems;
    private TextView tvMostFrequentCategory, tvBudgetStatus, tvBudgetTotal, tvActualTotal;
    private TextView tvShoppingFrequency, tvLastShoppingDate;
    private RecyclerView recyclerExpensiveItems;
    private ExpensiveItemsAdapter expensiveItemsAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        
        // Setup toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.analytics_dashboard);
        }

        viewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);
        
        initializeViews();
        setupRecyclerView();
        observeData();
    }

    private void initializeViews() {
        // Spending Analytics
        tvTotalSpent = findViewById(R.id.tv_total_spent);
        tvAveragePerList = findViewById(R.id.tv_average_per_list);
        tvTotalLists = findViewById(R.id.tv_total_lists);
        tvTotalItems = findViewById(R.id.tv_total_items);
        
        // Budget vs Actual
        tvBudgetTotal = findViewById(R.id.tv_budget_total);
        tvActualTotal = findViewById(R.id.tv_actual_total);
        tvBudgetStatus = findViewById(R.id.tv_budget_status);
        
        // Shopping Frequency
        tvShoppingFrequency = findViewById(R.id.tv_shopping_frequency);
        tvLastShoppingDate = findViewById(R.id.tv_last_shopping_date);
        tvMostFrequentCategory = findViewById(R.id.tv_most_frequent_category);
        
        // Expensive Items RecyclerView
        recyclerExpensiveItems = findViewById(R.id.recycler_expensive_items);
    }

    private void setupRecyclerView() {
        expensiveItemsAdapter = new ExpensiveItemsAdapter();
        recyclerExpensiveItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerExpensiveItems.setAdapter(expensiveItemsAdapter);
    }

    private void observeData() {
        // Spending Analytics
        viewModel.getTotalSpent().observe(this, total -> {
            tvTotalSpent.setText(String.format("$%.2f", total != null ? total : 0.0));
        });

        viewModel.getAveragePerList().observe(this, avg -> {
            tvAveragePerList.setText(String.format("$%.2f", avg != null ? avg : 0.0));
        });

        viewModel.getTotalLists().observe(this, count -> {
            tvTotalLists.setText(String.valueOf(count != null ? count : 0));
        });

        viewModel.getTotalItems().observe(this, count -> {
            tvTotalItems.setText(String.valueOf(count != null ? count : 0));
        });

        // Budget vs Actual
        viewModel.getTotalBudget().observe(this, budget -> {
            tvBudgetTotal.setText(String.format("$%.2f", budget != null ? budget : 0.0));
            updateBudgetStatus();
        });

        viewModel.getTotalActual().observe(this, actual -> {
            tvActualTotal.setText(String.format("$%.2f", actual != null ? actual : 0.0));
            updateBudgetStatus();
        });

        // Shopping Frequency
        viewModel.getShoppingFrequency().observe(this, frequency -> {
            tvShoppingFrequency.setText(frequency != null ? frequency : "N/A");
        });

        viewModel.getLastShoppingDate().observe(this, date -> {
            if (date != null && date > 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                tvLastShoppingDate.setText(sdf.format(new Date(date)));
            } else {
                tvLastShoppingDate.setText("N/A");
            }
        });

        viewModel.getMostFrequentCategory().observe(this, category -> {
            tvMostFrequentCategory.setText(category != null ? category : "N/A");
        });

        // Most Expensive Items
        viewModel.getMostExpensiveItems().observe(this, items -> {
            if (items != null) {
                expensiveItemsAdapter.setItems(items);
            }
        });
    }

    private void updateBudgetStatus() {
        Double budget = viewModel.getTotalBudget().getValue();
        Double actual = viewModel.getTotalActual().getValue();
        
        if (budget != null && actual != null && budget > 0) {
            double difference = budget - actual;
            if (difference >= 0) {
                tvBudgetStatus.setText(String.format("Under Budget: $%.2f", difference));
                tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvBudgetStatus.setText(String.format("Over Budget: $%.2f", Math.abs(difference)));
                tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        } else {
            tvBudgetStatus.setText("No budget data");
            tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
