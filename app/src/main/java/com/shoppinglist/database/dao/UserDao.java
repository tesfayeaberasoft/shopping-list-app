/**
 * UserDao - Data Access Object for User Entity
 * Handles all database operations related to user accounts and profiles
 * Supports CRUD operations and user authentication queries
 */
package com.shoppinglist.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.shoppinglist.database.entities.UserEntity;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserEntity user);

    @Update
    void update(UserEntity user);

    @Query("SELECT * FROM users WHERE id = :userId")
    UserEntity getUserById(String userId);

    @Query("DELETE FROM users")
    void deleteAll();
}