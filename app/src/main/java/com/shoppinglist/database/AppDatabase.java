/**
 * Shopping List App - Room Database Configuration
 * Manages all local database operations and entity relationships
 */
package com.shoppinglist.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.shoppinglist.database.converters.Converters;
import com.shoppinglist.database.dao.ShoppingItemDao;
import com.shoppinglist.database.dao.ShoppingListDao;
import com.shoppinglist.database.dao.UserDao;
import com.shoppinglist.database.dao.PantryItemDao;
import com.shoppinglist.database.dao.ReminderDao;
import com.shoppinglist.database.dao.ShoppingHistoryDao;
import com.shoppinglist.database.entities.ShoppingItemEntity;
import com.shoppinglist.database.entities.ShoppingListEntity;
import com.shoppinglist.database.entities.UserEntity;
import com.shoppinglist.database.entities.PantryItemEntity;
import com.shoppinglist.database.entities.ReminderEntity;
import com.shoppinglist.database.entities.ShoppingHistoryEntity;

@Database(entities = {ShoppingListEntity.class, ShoppingItemEntity.class, UserEntity.class, PantryItemEntity.class, ReminderEntity.class, ShoppingHistoryEntity.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract ShoppingListDao shoppingListDao();
    public abstract ShoppingItemDao shoppingItemDao();
    public abstract UserDao userDao();
    public abstract PantryItemDao pantryItemDao();
    public abstract ReminderDao reminderDao();
    public abstract ShoppingHistoryDao shoppingHistoryDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "shopping_list_db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
    
    // Migration from version 1 to 2: Add budget tracking fields
    static final androidx.room.migration.Migration MIGRATION_1_2 = new androidx.room.migration.Migration(1, 2) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            // Add price column to shopping_items table
            database.execSQL("ALTER TABLE shopping_items ADD COLUMN price REAL NOT NULL DEFAULT 0.0");
            
            // Add budget column to shopping_lists table
            database.execSQL("ALTER TABLE shopping_lists ADD COLUMN budget REAL NOT NULL DEFAULT 0.0");
        }
    };
    
    // Migration from version 2 to 3: Add pantry_items table
    static final androidx.room.migration.Migration MIGRATION_2_3 = new androidx.room.migration.Migration(2, 3) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            // Create pantry_items table
            database.execSQL("CREATE TABLE IF NOT EXISTS `pantry_items` (" +
                    "`id` TEXT NOT NULL, " +
                    "`user_id` TEXT, " +
                    "`name` TEXT, " +
                    "`current_quantity` REAL NOT NULL, " +
                    "`unit` TEXT, " +
                    "`category` TEXT, " +
                    "`minimum_quantity` REAL NOT NULL, " +
                    "`location` TEXT, " +
                    "`expiry_date` INTEGER NOT NULL, " +
                    "`notes` TEXT, " +
                    "`created_at` INTEGER NOT NULL, " +
                    "`updated_at` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))");
        }
    };
    
    // Migration from version 3 to 4: Add reminders table
    static final androidx.room.migration.Migration MIGRATION_3_4 = new androidx.room.migration.Migration(3, 4) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            // Create reminders table
            database.execSQL("CREATE TABLE IF NOT EXISTS `reminders` (" +
                    "`id` TEXT NOT NULL, " +
                    "`user_id` TEXT, " +
                    "`list_id` TEXT, " +
                    "`title` TEXT, " +
                    "`message` TEXT, " +
                    "`reminder_type` TEXT, " +
                    "`scheduled_time` INTEGER NOT NULL, " +
                    "`repeat_interval` TEXT, " +
                    "`is_active` INTEGER NOT NULL, " +
                    "`is_triggered` INTEGER NOT NULL, " +
                    "`created_at` INTEGER NOT NULL, " +
                    "`updated_at` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))");
        }
    };
    
    // Migration from version 4 to 5: Add shopping_history table
    static final androidx.room.migration.Migration MIGRATION_4_5 = new androidx.room.migration.Migration(4, 5) {
        @Override
        public void migrate(androidx.sqlite.db.SupportSQLiteDatabase database) {
            // Create shopping_history table
            database.execSQL("CREATE TABLE IF NOT EXISTS `shopping_history` (" +
                    "`id` TEXT NOT NULL, " +
                    "`user_id` TEXT, " +
                    "`list_id` TEXT, " +
                    "`list_name` TEXT, " +
                    "`item_name` TEXT, " +
                    "`category` TEXT, " +
                    "`quantity` REAL NOT NULL, " +
                    "`unit` TEXT, " +
                    "`price` REAL NOT NULL, " +
                    "`total_cost` REAL NOT NULL, " +
                    "`purchase_date` INTEGER NOT NULL, " +
                    "`store_name` TEXT, " +
                    "`notes` TEXT, " +
                    "`created_at` INTEGER NOT NULL, " +
                    "PRIMARY KEY(`id`))");
        }
    };
}