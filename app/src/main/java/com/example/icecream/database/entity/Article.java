package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * The article table.
 *
 * @author Kemo
 */
@Entity(foreignKeys = @ForeignKey(
    entity = RssFeed.class,
    parentColumns = "id",
    childColumns = "feedId",
    onDelete = ForeignKey.CASCADE),
    indices = {@Index("feedId"), @Index("title")})
public class Article {

  @PrimaryKey(autoGenerate = true)
  private Long id;

  private Long feedId;

  @NonNull
  private String title;

  private String link;

  private String description;

  private Date publishTime;

  /**
   * Constructor for article.
   *
   * @param title       article title.
   * @param link        article url.
   * @param description article description.
   * @param publishTime article publish time.
   */
  public Article(@NonNull String title, String link, String description, Date publishTime) {
    this.title = title;
    this.link = link;
    this.description = description;
    this.publishTime = publishTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getFeedId() {
    return feedId;
  }

  public void setFeedId(Long feedId) {
    this.feedId = feedId;
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
