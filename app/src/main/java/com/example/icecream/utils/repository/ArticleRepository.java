package com.example.icecream.utils.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.icecream.database.AppDataBase;
import com.example.icecream.database.dao.ArticleDao;
import com.example.icecream.database.entity.Article;

import java.util.List;

/**
 * The repository to handle articles in database
 */
public class ArticleRepository {
  private ArticleDao articleDao;
  private LiveData<List<Article>> allArticles;

  /**
   * Constructor<br/>
   * register the application to repository
   *
   * @param application input application
   */
  public ArticleRepository(Application application) {
    AppDataBase db = AppDataBase.getDatabase(application);
    articleDao = db.articleDao();
    allArticles = articleDao.getAllArticles();
  }

  /**
   * Getter for article list
   *
   * @return all the articles
   */
  public LiveData<List<Article>> getAllArticles() {
    return allArticles;
  }

  /**
   * insert article to database asynchronously
   *
   * @param article input article
   */
  public void insert(Article article) {
    new ArticleRepository.insertAsyncTask(articleDao).execute(article);
  }

  private static class insertAsyncTask extends AsyncTask<Article, Void, Void> {
    private ArticleDao articleDao;

    insertAsyncTask(ArticleDao dao) {
      articleDao = dao;
    }

    @Override
    protected Void doInBackground(final Article... params) {
      articleDao.insert(params[0]);
      return null;
    }
  }
}
