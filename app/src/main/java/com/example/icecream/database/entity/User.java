package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(indices = {@Index(value = "phoneNumber", unique = true)})
public class User {

  @PrimaryKey(autoGenerate = true)
  private Long id;

  @NonNull
  private String phoneNumber;

  @NonNull
  private String username;

  private String password;

  private String authToken;

  /**
   * Constructor for User class.
   *
   * @param phoneNumber The input phone number.
   * @param username    The input username.
   * @param password    The input password.
   */
  public User(@NonNull String phoneNumber, @NonNull String username, String password, String authToken) {
    this.phoneNumber = phoneNumber;
    this.username = username;
    this.password = password;
    this.authToken = authToken;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @NonNull
  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(@NonNull String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @NonNull
  public String getUsername() {
    return username;
  }

  public void setUsername(@NonNull String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }


  public String getAuthToken() {
    return authToken;
  }

  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
