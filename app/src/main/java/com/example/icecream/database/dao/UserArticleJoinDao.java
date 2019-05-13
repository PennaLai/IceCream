package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.UserArticleJoin;

import java.util.List;

@Dao
public interface UserArticleJoinDao {

  @Insert
  void insert(UserArticleJoin userRssFeedJoin);

  @Query("SELECT * FROM Article INNER JOIN UserArticleJoin ON "
      + "Article.id = UserArticleJoin.articleId WHERE "
      + "UserArticleJoin.userId = :userId")
  LiveData<List<Article>> getArticlesByUserId(final Long userId);

}
