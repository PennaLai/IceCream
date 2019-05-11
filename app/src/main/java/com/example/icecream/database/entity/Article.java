package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * The article table
 *
 * @author Kemo
 */
@Entity(foreignKeys = @ForeignKey(
    entity = RssFeed.class,
    parentColumns = "id",
    childColumns = "feedId",
    onDelete = CASCADE))
public class Article {

  @PrimaryKey(autoGenerate = true)
  private int id;

  @NonNull
  private String title;

  private String link;

  private String description;

  private Date publishTime;

  public Article(@NonNull String title, String link, String description, Date publishTime) {
    this.title = title;
    this.link = link;
    this.description = description;
    this.publishTime = publishTime;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @NonNull
  public String getTitle() {
    return title;
  }

  public void setTitle(@NonNull String title) {
    this.title = title;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getPublishTime() {
    return publishTime;
  }

  public void setPublishTime(Date publishTime) {
    this.publishTime = publishTime;
  }
}
