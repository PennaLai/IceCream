package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

/**
 * Join class for user and article many to many relationship.
 */
@Entity(primaryKeys = {"userId", "articleId"},
    foreignKeys = {
        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
        @ForeignKey(entity = Article.class, parentColumns = "id", childColumns = "articleId")},
    indices = {
        @Index("userId"),
        @Index("articleId")})
public class UserArticleJoin {

  @NonNull
  private Long userId;
  @NonNull
  private Long articleId;

  public UserArticleJoin(@NonNull Long userId, @NonNull Long articleId) {
    this.userId = userId;
    this.articleId = articleId;
  }

  @NonNull
  public Long getUserId() {
    return userId;
  }

  public void setUserId(@NonNull Long userId) {
    this.userId = userId;
  }

  @NonNull
  public Long getArticleId() {
    return articleId;
  }

  public void setArticleId(@NonNull Long articleId) {
    this.articleId = articleId;
  }
}
