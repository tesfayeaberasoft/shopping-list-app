package com.shoppinglist.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.shoppinglist.fragments.HistoryListFragment;
import com.shoppinglist.fragments.HistoryAnalyticsFragment;
import com.shoppinglist.fragments.HistoryReportsFragment;

public class ShoppingHistoryPagerAdapter extends FragmentStateAdapter {

    public ShoppingHistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HistoryListFragment();
            case 1:
                return new HistoryAnalyticsFragment();
            case 2:
                return new HistoryReportsFragment();
            default:
                return new HistoryListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}