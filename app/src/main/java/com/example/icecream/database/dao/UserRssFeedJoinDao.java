package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.UserRssFeedJoin;

import java.util.List;

@Dao
public interface UserRssFeedJoinDao {

  @Insert
  void insert(UserRssFeedJoin userRssFeedJoin);

  @Query("SELECT * FROM RssFeed INNER JOIN UserRssFeedJoin ON " +
      "RssFeed.id = UserRssFeedJoin.rssFeedId WHERE " +
      "UserRssFeedJoin.userId = :userId")
  LiveData<List<RssFeed>> getRssFeedsByUserId(final Long userId);


}
