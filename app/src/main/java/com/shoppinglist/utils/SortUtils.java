package com.shoppinglist.utils;

import com.shoppinglist.database.entities.ShoppingItemEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtils {
    public static final int SORT_CATEGORY = 0;
    public static final int SORT_ALPHA = 1;
    public static final int SORT_PRIORITY = 2;
    public static final int SORT_DATE = 3;

    public static List<ShoppingItemEntity> sort(List<ShoppingItemEntity> items, int sortType) {
        if (items == null) return null;
        // Work on a copy so we don't mutate the LiveData-backed list
        List<ShoppingItemEntity> sorted = new ArrayList<>(items);
        switch (sortType) {
            case SORT_CATEGORY:
                Collections.sort(sorted, Comparator.comparing(ShoppingItemEntity::getCategory).thenComparing(ShoppingItemEntity::getOrderIndex));
                break;
            case SORT_ALPHA:
                Collections.sort(sorted, Comparator.comparing(ShoppingItemEntity::getName));
                break;
            case SORT_PRIORITY:
                Collections.sort(sorted, Comparator.comparingInt(ShoppingItemEntity::getPriority));
                break;
            case SORT_DATE:
            default:
                Collections.sort(sorted, Comparator.comparingLong(ShoppingItemEntity::getCreatedAt));
                break;
        }
        return sorted;
    }
}