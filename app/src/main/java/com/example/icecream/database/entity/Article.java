package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

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

  @PrimaryKey()
  private Long id;

  @NonNull
  private Long feedId;

  @NonNull
  private String title;

  private String link;

  private String description;

  private String publishTime;

  /**
   * Constructor for article.
   *
   * @param title       article title.
   * @param link        article url.
   * @param description article description.
   * @param publishTime article publish time.
   */
  public Article(@NonNull Long id, @NonNull Long feedId, @NonNull String title,
                 String link, String description, String publishTime) {
    this.id = id;
    this.feedId = feedId;
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

  @NonNull
  public Long getFeedId() {
    return feedId;
  }

  public void setFeedId(@NonNull Long feedId) {
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

  public String getPublishTime() {
    return publishTime;
  }

  public void setPublishTime(String publishTime) {
    this.publishTime = publishTime;
  }

}
