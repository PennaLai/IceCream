package com.example.icecream.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

/**
 * Join class for user and RSS feed many to many relationship.
 */
@Entity(primaryKeys = {"userId", "rssFeedId"},
//    foreignKeys = {
//        @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"),
//        @ForeignKey(entity = RssFeed.class, parentColumns = "id", childColumns = "rssFeedId")},
    indices = {@Index("userId"), @Index("rssFeedId")})
public class UserRssFeedJoin {

  @NonNull
  private Long userId;
  @NonNull
  private Long rssFeedId;

  /**
   * Constructor.
   *
   * @param userId    user id.
   * @param rssFeedId feed id.
   */
  public UserRssFeedJoin(@NonNull Long userId, @NonNull Long rssFeedId) {
    this.userId = userId;
    this.rssFeedId = rssFeedId;
  }

  @NonNull
  public Long getUserId() {
    return userId;
  }

  public void setUserId(@NonNull Long userId) {
    this.userId = userId;
  }

  @NonNull
  public Long getRssFeedId() {
    return rssFeedId;
  }

  public void setRssFeedId(@NonNull Long rssFeedId) {
    this.rssFeedId = rssFeedId;
  }
}
