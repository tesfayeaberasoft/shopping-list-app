package com.shoppinglist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.adapters.MostBoughtItemsAdapter;
import com.shoppinglist.adapters.CategorySpendingAdapter;
import com.shoppinglist.viewmodels.ShoppingHistoryViewModel;

public class HistoryAnalyticsFragment extends Fragment {
    private ShoppingHistoryViewModel viewModel;
    private RecyclerView recyclerMostBought;
    private RecyclerView recyclerCategorySpending;
    private MostBoughtItemsAdapter mostBoughtAdapter;
    private CategorySpendingAdapter categorySpendingAdapter;
    private TextView tvAveragePerTrip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingHistoryViewModel.class);
        
        initializeViews(view);
        setupRecyclerViews();
        observeData();
    }

    private void initializeViews(View view) {
        recyclerMostBought = view.findViewById(R.id.recycler_most_bought);
        recyclerCategorySpending = view.findViewById(R.id.recycler_category_spending);
        tvAveragePerTrip = view.findViewById(R.id.tv_average_per_trip);
    }

    private void setupRecyclerViews() {
        // Most bought items
        mostBoughtAdapter = new MostBoughtItemsAdapter();
        recyclerMostBought.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMostBought.setAdapter(mostBoughtAdapter);

        // Category spending
        categorySpendingAdapter = new CategorySpendingAdapter();
        recyclerCategorySpending.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCategorySpending.setAdapter(categorySpendingAdapter);
    }

    private void observeData() {
        // Most bought items
        viewModel.getMostBoughtItems().observe(getViewLifecycleOwner(), items -> {
            mostBoughtAdapter.setItems(items);
        });

        // Category spending
        viewModel.getCategorySpending().observe(getViewLifecycleOwner(), spending -> {
            categorySpendingAdapter.setItems(spending);
        });

        // Average spending per trip
        viewModel.getAverageSpendingPerTrip().observe(getViewLifecycleOwner(), average -> {
            if (average != null) {
                tvAveragePerTrip.setText(String.format("$%.2f", average));
            } else {
                tvAveragePerTrip.setText("$0.00");
            }
        });
    }
}