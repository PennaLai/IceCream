package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.icecream.database.entity.Article;

import java.util.List;

/**
 * Query for articles.
 */
@Dao
public interface ArticleDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Article... article);

  @Update
  void update(Article... article);

  @Delete
  void delete(Article... article);

  @Query("SELECT * FROM article ORDER BY title")
  LiveData<List<Article>> getAllArticles();

  @Query("SELECT * FROM article WHERE id = :id")
  Article getArticleById(Long id);

  @Query("SELECT * FROM article WHERE title = :title")
  List<Article> getArticlesByTitle(String title);

  @Query("SELECT * FROM article WHERE feedUrl = :url")
  List<Article> getArticlesByRssFeedUrl(String url);
}
