package com.example.icecream.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.UserArticleJoin;

import java.util.List;

@Dao
public interface UserArticleJoinDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(UserArticleJoin... userArticleJoins);

  @Update
  void update(UserArticleJoin... userArticleJoins);

  @Delete
  void delete(UserArticleJoin... userArticleJoins);

  @Query("SELECT Article.* FROM Article INNER JOIN UserArticleJoin ON "
      + "Article.id = UserArticleJoin.articleId WHERE "
      + "UserArticleJoin.userId = :userId")
  List<Article> getArticlesByUserId(final Long userId);

}
