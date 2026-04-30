package com.shoppinglist.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.shoppinglist.R;
import com.shoppinglist.adapters.ShoppingHistoryAdapter;
import com.shoppinglist.database.entities.ShoppingHistoryEntity;
import com.shoppinglist.viewmodels.ShoppingHistoryViewModel;

public class HistoryListFragment extends Fragment implements ShoppingHistoryAdapter.OnHistoryItemListener {
    private ShoppingHistoryViewModel viewModel;
    private ShoppingHistoryAdapter adapter;
    private RecyclerView recyclerView;
    private View emptyView;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(requireActivity()).get(ShoppingHistoryViewModel.class);
        
        initializeViews(view);
        setupRecyclerView();
        setupSearch();
        observeData();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_history);
        emptyView = view.findViewById(R.id.empty_view);
        searchView = view.findViewById(R.id.search_view);
    }

    private void setupRecyclerView() {
        adapter = new ShoppingHistoryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.trim().isEmpty()) {
                    observeAllHistory();
                } else {
                    observeSearchResults(newText.trim());
                }
                return true;
            }
        });
    }

    private void observeData() {
        observeAllHistory();
    }

    private void observeAllHistory() {
        viewModel.getAllHistory().observe(getViewLifecycleOwner(), historyItems -> {
            adapter.setHistoryItems(historyItems);
            updateEmptyView(historyItems);
        });
    }

    private void observeSearchResults(String query) {
        viewModel.searchHistory(query).observe(getViewLifecycleOwner(), historyItems -> {
            adapter.setHistoryItems(historyItems);
            updateEmptyView(historyItems);
        });
    }

    private void updateEmptyView(java.util.List<ShoppingHistoryEntity> items) {
        if (items == null || items.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onHistoryItemClick(ShoppingHistoryEntity item) {
        // TODO: Show item details dialog or navigate to details
    }
}