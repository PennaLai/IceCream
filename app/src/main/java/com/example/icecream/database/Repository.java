package com.example.icecream.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.example.icecream.database.dao.ArticleDao;
import com.example.icecream.database.dao.RssFeedDao;
import com.example.icecream.database.dao.UserArticleJoinDao;
import com.example.icecream.database.dao.UserDao;
import com.example.icecream.database.dao.UserRssFeedJoinDao;
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;

import java.util.List;

/**
 * The repository to hold all the data in local database.
 *
 * @author Kemo
 */
public class Repository {

  private UserDao userDao;
  private RssFeedDao rssFeedDao;
  private ArticleDao articleDao;

  private UserRssFeedJoinDao userRssFeedJoinDao;
  private UserArticleJoinDao userArticleJoinDao;
  private LiveData<List<User>> allUsers;
  private LiveData<List<RssFeed>> allRssFeeds;
  private LiveData<List<Article>> allArticles;

  /**
   * Constructor<br/>
   * register the application to repository.
   *
   * @param application input application.
   */
  public Repository(Application application) {
    AppDataBase db = AppDataBase.getDatabase(application);
    userDao = db.userDao();
    rssFeedDao = db.rssFeedDao();
    articleDao = db.articleDao();
    userRssFeedJoinDao = db.userRssFeedJoinDao();
    userArticleJoinDao = db.userArticleJoinDao();
    allUsers = userDao.getAllUsers();
    allRssFeeds = rssFeedDao.getAllFeeds();
    allArticles = articleDao.getAllArticles();
  }

  /**
   * Get all users from local database.
   *
   * @return all users.
   */
  public LiveData<List<User>> getAllUsers() {
    return allUsers;
  }

  /**
   * Get all RSS feeds from local database.
   *
   * @return all feeds.
   */
  public LiveData<List<RssFeed>> getAllRssFeeds() {
    return allRssFeeds;
  }


  /**
   * Get all articles from local database.
   *
   * @return all articles.
   */
  public LiveData<List<Article>> getAllArticles() {
    return allArticles;
  }


  /**
   * Insert a user to database.
   *
   * @param user the input user.
   */
  public void insertUser(User user) {
    userDao.insert(user);
  }

  /**
   * Update a user if its primary key exists.
   *
   * @param user the input user.
   */
  public void updateUser(User user) {
    userDao.update(user);
  }

  /**
   * Delete a user from database.
   *
   * @param user the input user.
   */
  public void deleteUser(User user) {
    userDao.delete(user);
  }

  public void insertRssFeed(RssFeed rssFeed) {
    rssFeedDao.insert(rssFeed);
  }

  public void updateRssFeed(RssFeed rssFeed) {
    rssFeedDao.update(rssFeed);
  }

  public void deleteRssFeed(RssFeed rssFeed) {
    rssFeedDao.delete(rssFeed);
  }

  /**
   * Insert an article to database.
   *
   * @param article the input article.
   */
  public void insertArticle(Article article) {
    articleDao.insert(article);
  }

  /**
   * Update an article if its primary key exists.
   *
   * @param article the input article.
   */
  public void updateArticle(Article article) {
    articleDao.update(article);
  }

  /**
   * Delete an article from database.
   *
   * @param article the input article.
   */
  public void deleteArticle(Article article) {
    articleDao.delete(article);
  }


  /**
   * Find the user by phone number from database.
   *
   * @param phone the input user's phone number.
   */
  public LiveData<User> getUserByPhone(String phone) {
    return userDao.getUserByPhone(phone);
  }

  /**
   * Get all the RSS feeds of a user.
   *
   * @param user user of interest.
   * @return the RSS feed list of the user.
   */
  public LiveData<List<RssFeed>> getRssFeedsByUser(User user) {
    return userRssFeedJoinDao.getRssFeedsByUserId(user.getId());
  }

  /**
   * Get all the articles of a user.
   *
   * @param user user of interest.
   * @return the article list of the user.
   */
  public LiveData<List<Article>> getArticlesByUser(User user) {
    return userArticleJoinDao.getArticlesByUserId(user.getId());
  }


}
