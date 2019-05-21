package com.example.icecream.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.UserRssFeedJoin;

import java.util.List;

@Dao
public interface UserRssFeedJoinDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(UserRssFeedJoin... userRssFeedJoins);

  @Update
  void update(UserRssFeedJoin... userRssFeedJoins);

  @Delete
  void delete(UserRssFeedJoin... userRssFeedJoins);

  @Query("SELECT RssFeed.* FROM RssFeed INNER JOIN UserRssFeedJoin ON "
      + "RssFeed.id = UserRssFeedJoin.rssFeedId WHERE "
      + "UserRssFeedJoin.userId = :userId")
  List<RssFeed> getRssFeedsByUserId(final Long userId);


}
