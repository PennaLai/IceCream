package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


/**
 * The RSS feed table.
 *
 * @author Kemo
 */
@Entity(indices = {@Index(value = "url", unique = true)})
public class RssFeed {

  @PrimaryKey(autoGenerate = true)
  private Long id;

  @NonNull
  private String url;

  private String channelName;

  private String category;

  /**
   * Constructor for RSS feed.
   *
   * @param url         RSS feed url.
   * @param channelName the channel name.
   * @param category    the category.
   */
  public RssFeed(@NonNull String url, String channelName, String category) {
    this.url = url;
    this.channelName = channelName;
    this.category = category;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @NonNull
  public String getUrl() {
    return url;
  }

  public void setUrl(@NonNull String url) {
    this.url = url;
  }

  public String getChannelName() {
    return channelName;
  }

  public void setChannelName(String channelName) {
    this.channelName = channelName;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }
}
