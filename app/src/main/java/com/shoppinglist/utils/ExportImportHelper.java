package com.shoppinglist.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppinglist.database.AppDatabase;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.database.entities.ShoppingListEntity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExportImportHelper {
    private Context context;

    public ExportImportHelper(Context context) {
        this.context = context;
    }

    public void exportData() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            List<ShoppingListEntity> lists = db.shoppingListDao().getAllListsSync();
            if (lists == null) lists = new ArrayList<>();

            Map<String, Object> export = new HashMap<>();
            export.put("lists", lists);
            for (ShoppingListEntity list : lists) {
                List<ShoppingItemEntity> items = db.shoppingItemDao().getItemsByListIdSync(list.getId());
                export.put("items_" + list.getId(), items);
            }

            Gson gson = new Gson();
            String json = gson.toJson(export);
            try {
                File dir = context.getExternalFilesDir(null);
                if (dir != null && !dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, "shopping_data.json");
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fos);
                writer.write(json);
                writer.close();
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> Toast.makeText(context, "Data exported to " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> Toast.makeText(context, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    public void importData(Uri uri) {
        new Thread(() -> {
            try {
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                if (inputStream == null) {
                    showToast("Cannot open file");
                    return;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();

                Gson gson = new Gson();
                Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> data = gson.fromJson(sb.toString(), mapType);

                if (data == null) {
                    showToast("Invalid file format");
                    return;
                }

                AppDatabase db = AppDatabase.getInstance(context);

                Object listsObj = data.get("lists");
                if (listsObj != null) {
                    String listsJson = gson.toJson(listsObj);
                    Type listType = new TypeToken<List<ShoppingListEntity>>() {}.getType();
                    List<ShoppingListEntity> lists = gson.fromJson(listsJson, listType);
                    if (lists != null) {
                        for (ShoppingListEntity list : lists) {
                            db.shoppingListDao().insert(list);
                            Object itemsObj = data.get("items_" + list.getId());
                            if (itemsObj != null) {
                                String itemsJson = gson.toJson(itemsObj);
                                Type itemListType = new TypeToken<List<ShoppingItemEntity>>() {}.getType();
                                List<ShoppingItemEntity> items = gson.fromJson(itemsJson, itemListType);
                                if (items != null) {
                                    for (ShoppingItemEntity item : items) {
                                        db.shoppingItemDao().insert(item);
                                    }
                                }
                            }
                        }
                    }
                }
                showToast("Import finished");
            } catch (Exception e) {
                showToast("Import failed: " + e.getMessage());
            }
        }).start();
    }

    private void showToast(String message) {
        new android.os.Handler(android.os.Looper.getMainLooper())
                .post(() -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
