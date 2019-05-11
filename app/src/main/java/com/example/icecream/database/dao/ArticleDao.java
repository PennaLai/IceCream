package com.example.icecream.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import com.example.icecream.database.entity.Article;

/**
 * Query for articles
 */
@Dao
public interface ArticleDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(Article article);

  @Query("DELETE FROM article")
  void deleteAll();

  @Query("SELECT * FROM article WHERE id = :id")
  LiveData<Article> getArticleById(Long id);

  @Query("SELECT * FROM article WHERE title = :title")
  LiveData<List<Article>> getArticlesByTitle(String title);

  @Query("SELECT * FROM article ORDER BY title")
  LiveData<List<Article>> getAllArticles();
}
