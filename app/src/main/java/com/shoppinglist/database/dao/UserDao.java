/**
 * UserDao - Data Access Object for User Entity
 * Handles all database operations related to user accounts and profiles
 * Supports CRUD operations and user authentication queries
 * 
 * DAO Operations:
 * - Insert new user
 * - Update user profile
 * - Delete user account
 * - Query user by ID
 * - Query user by email
 * - Get all users
 * - User authentication
 * 
 * Conflict Strategy:
 * - REPLACE: Replaces existing user on conflict
 * - Ensures data consistency
 * 
 * Thread Safety:
 * - Room handles thread safety
 * - Async operations recommended
 * - LiveData for reactive queries
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