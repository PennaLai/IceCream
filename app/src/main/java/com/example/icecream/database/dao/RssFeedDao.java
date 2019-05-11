package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import com.example.icecream.database.entity.RssFeed;

@Dao
public interface RssFeedDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(RssFeed rssFeed);

  @Query("DELETE FROM RssFeed WHERE id = :feedId")
  void delete(Long feedId);

  @Query("SELECT * FROM RssFeed ORDER BY category")
  LiveData<List<RssFeed>> getAllFeeds();

  @Query("SELECT * FROM RssFeed WHERE id = :feedId")
  LiveData<RssFeed> loadRssFeed(Long feedId);

  @Query("SELECT * FROM RssFeed WHERE id = :feedId")
  RssFeed loadRssFeedSync(Long feedId);
}
