package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.icecream.database.entity.RssFeed;

import java.util.List;

@Dao
public interface RssFeedDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(RssFeed... rssFeed);

  @Delete
  void delete(RssFeed... rssFeed);

  @Update
  void update(RssFeed... rssFeed);

  @Query("SELECT * FROM RssFeed ORDER BY category")
  LiveData<List<RssFeed>> getAllFeeds();

  @Query("SELECT * FROM RssFeed WHERE channelName =:name")
  List<RssFeed> getRssFeedByName(String name);

  @Query("SELECT * FROM RssFeed WHERE url =:url")
  RssFeed getRssFeedByUrl(String url);
}
