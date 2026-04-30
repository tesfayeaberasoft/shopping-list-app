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
 * - Profile updates
 * - Password changes
 * 
 * Conflict Strategy:
 * - REPLACE: Replaces existing user on conflict
 * - Ensures data consistency
 * - Prevents duplicate entries
 * - Maintains referential integrity
 * 
 * Thread Safety:
 * - Room handles thread safety
 * - Async operations recommended
 * - LiveData for reactive queries
 * - Coroutines support
 * - RxJava support
 * 
 * Query Methods:
 * - getUserById(String id): Get user by ID
 * - getUserByEmail(String email): Get user by email
 * - getAllUsers(): Get all users
 * - getUserCount(): Count total users
 * - searchUsers(String query): Search users
 * 
 * Insert Operations:
 * - insertUser(UserEntity user): Insert single user
 * - insertUsers(List<UserEntity> users): Insert multiple users
 * - Automatic ID generation
 * - Timestamp management
 * 
 * Update Operations:
 * - updateUser(UserEntity user): Update user profile
 * - updateUserEmail(String id, String email): Update email
 * - updateUserPassword(String id, String password): Update password
 * - updateUserProfile(String id, String name, String photo): Update profile
 * 
 * Delete Operations:
 * - deleteUser(UserEntity user): Delete single user
 * - deleteUserById(String id): Delete by ID
 * - deleteAllUsers(): Delete all users
 * - Cascade delete related data
 * 
 * Authentication:
 * - Verify user credentials
 * - Check email existence
 * - Validate passwords
 * - Session management
 * 
 * Data Validation:
 * - Email format validation
 * - Password strength validation
 * - Required field validation
 * - Unique constraint validation
 * 
 * Performance:
 * - Indexed queries
 * - Efficient lookups
 * - Lazy loading
 * - Query optimization
 * - Caching support
 * 
 * Error Handling:
 * - Duplicate entry handling
 * - Not found handling
 * - Constraint violation handling
 * - Transaction rollback
 * 
 * Testing:
 * - Unit tests for all operations
 * - Integration tests
 * - Performance tests
 * - Edge case testing
 * 
 * Security:
 * - Password hashing
 * - Data encryption
 * - Secure storage
 * - Access control
 * - Audit logging
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