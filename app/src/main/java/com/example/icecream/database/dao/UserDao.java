package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.icecream.database.entity.User;

import java.util.List;

/**
 * Query for users
 */
@Dao
public interface UserDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(User user);

  @Query("DELETE FROM User WHERE phoneNumber = :phone")
  void delete(String phone);

  @Query("SELECT * FROM User")
  LiveData<List<User>> getAllUsers();

  @Query("SELECT * FROM User WHERE id = :id")
  LiveData<User> getUserById(Long id);

  @Query("SELECT * FROM User WHERE phoneNumber = :phone")
  LiveData<User> getUserByPhone(String phone);

  @Query("SELECT * FROM User WHERE username = :username")
  LiveData<List<User>> getUserByUsername(String username);
}
