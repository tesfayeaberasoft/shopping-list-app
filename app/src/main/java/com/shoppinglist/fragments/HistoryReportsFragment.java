package com.shoppinglist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.adapters.MonthlySpendingAdapter;
import com.shoppinglist.adapters.WeeklySpendingAdapter;
import com.shoppinglist.viewmodels.ShoppingHistoryViewModel;

public class HistoryReportsFragment extends Fragment {
    private ShoppingHistoryViewModel viewModel;
    private RecyclerView recyclerMonthly;
    private RecyclerView recyclerWeekly;
    private MonthlySpendingAdapter monthlyAdapter;
    private WeeklySpendingAdapter weeklyAdapter;
    private Button btnMonthlyReport;
    private Button btnWeeklyReport;
    private boolean showingMonthly = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingHistoryViewModel.class);
        
        initializeViews(view);
        setupRecyclerViews();
        setupButtons();
        observeData();
        showMonthlyReport();
    }

    private void initializeViews(View view) {
        recyclerMonthly = view.findViewById(R.id.recycler_monthly);
        recyclerWeekly = view.findViewById(R.id.recycler_weekly);
        btnMonthlyReport = view.findViewById(R.id.btn_monthly_report);
        btnWeeklyReport = view.findViewById(R.id.btn_weekly_report);
    }

    private void setupRecyclerViews() {
        // Monthly spending
        monthlyAdapter = new MonthlySpendingAdapter();
        recyclerMonthly.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerMonthly.setAdapter(monthlyAdapter);

        // Weekly spending
        weeklyAdapter = new WeeklySpendingAdapter();
        recyclerWeekly.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerWeekly.setAdapter(weeklyAdapter);
    }

    private void setupButtons() {
        btnMonthlyReport.setOnClickListener(v -> showMonthlyReport());
        btnWeeklyReport.setOnClickListener(v -> showWeeklyReport());
    }

    private void showMonthlyReport() {
        showingMonthly = true;
        recyclerMonthly.setVisibility(View.VISIBLE);
        recyclerWeekly.setVisibility(View.GONE);
        
        btnMonthlyReport.setSelected(true);
        btnWeeklyReport.setSelected(false);
    }

    private void showWeeklyReport() {
        showingMonthly = false;
        recyclerMonthly.setVisibility(View.GONE);
        recyclerWeekly.setVisibility(View.VISIBLE);
        
        btnMonthlyReport.setSelected(false);
        btnWeeklyReport.setSelected(true);
    }

    private void observeData() {
        // Monthly spending
        viewModel.getMonthlySpending().observe(getViewLifecycleOwner(), spending -> {
            monthlyAdapter.setItems(spending);
        });

        // Weekly spending
        viewModel.getWeeklySpending().observe(getViewLifecycleOwner(), spending -> {
            weeklyAdapter.setItems(spending);
        });
    }
}