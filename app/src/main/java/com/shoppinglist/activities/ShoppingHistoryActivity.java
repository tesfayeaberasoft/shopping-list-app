package com.shoppinglist.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.shoppinglist.R;
import com.shoppinglist.adapters.ShoppingHistoryPagerAdapter;
import com.shoppinglist.utils.LocaleHelper;
import com.shoppinglist.viewmodels.ShoppingHistoryViewModel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ShoppingHistoryActivity extends AppCompatActivity {
    private ShoppingHistoryViewModel viewModel;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextView tvTotalSpending;
    private TextView tvCurrentMonthSpending;
    private TextView tvCurrentWeekSpending;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.shopping_history);
        }

        viewModel = new ViewModelProvider(this).get(ShoppingHistoryViewModel.class);

        initializeViews();
        setupViewPager();
        observeData();
        loadAnalyticsData();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        tvTotalSpending = findViewById(R.id.tv_total_spending);
        tvCurrentMonthSpending = findViewById(R.id.tv_current_month_spending);
        tvCurrentWeekSpending = findViewById(R.id.tv_current_week_spending);
    }

    private void setupViewPager() {
        ShoppingHistoryPagerAdapter adapter = new ShoppingHistoryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.history);
                    tab.setIcon(R.drawable.ic_history);
                    break;
                case 1:
                    tab.setText(R.string.analytics);
                    tab.setIcon(R.drawable.ic_analytics);
                    break;
                case 2:
                    tab.setText(R.string.reports);
                    tab.setIcon(R.drawable.ic_report);
                    break;
            }
        }).attach();
    }

    private void observeData() {
        // Observe total spending
        viewModel.getTotalSpending().observe(this, totalSpending -> {
            if (totalSpending != null) {
                tvTotalSpending.setText(String.format("$%.2f", totalSpending));
            } else {
                tvTotalSpending.setText("$0.00");
            }
        });

        // Load current month and week spending
        viewModel.getCurrentMonthSpending(spending -> 
            runOnUiThread(() -> tvCurrentMonthSpending.setText(String.format("$%.2f", spending))));

        viewModel.getCurrentWeekSpending(spending -> 
            runOnUiThread(() -> tvCurrentWeekSpending.setText(String.format("$%.2f", spending))));
    }

    private void loadAnalyticsData() {
        // Load analytics data for the fragments
        viewModel.loadMostBoughtItems(10);
        viewModel.loadCategorySpending();
        viewModel.loadMonthlySpending(12);
        viewModel.loadWeeklySpending(8);
        viewModel.loadAverageSpendingPerTrip();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.action_export) {
            exportHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        // TODO: Implement filter dialog for date range, category, etc.
    }

    private void exportHistory() {
        // TODO: Implement export functionality (CSV, PDF)
    }
}