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
    parentColumns = "url",
    childColumns = "feedUrl",
    onDelete = ForeignKey.CASCADE),
    indices = {@Index("feedUrl"), @Index("title")})
public class Article {

  @PrimaryKey
  private Long id;

  @NonNull
  private String feedUrl;

  @NonNull
  private String title;

  private String link;

  private String description;

  private String publishTime;

  private String paragraph;

  /**
   * Constructor for article.
   *
   * @param title       article title.
   * @param link        article url.
   * @param description article description.
   * @param publishTime article publish time.
   */
  public Article(@NonNull Long id, @NonNull String feedUrl, @NonNull String title,
                 String link, String description, String publishTime) {
    this.id = id;
    this.feedUrl = feedUrl;
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
  public String getFeedUrl() {
    return feedUrl;
  }

  public void setFeedUrl(@NonNull String feedUrl) {
    this.feedUrl = feedUrl;
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

  public String getParagraph() {
    return paragraph;
  }

  public void setParagraph(String paragraph) {
    this.paragraph = paragraph;
  }
}
