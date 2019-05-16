package com.example.icecream.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

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

  private MutableLiveData<User> userSearchResult = new MutableLiveData<>();
  private MutableLiveData<List<RssFeed>> feedSearchResults = new MutableLiveData<>();
  private MutableLiveData<List<Article>> articleSearchResults = new MutableLiveData<>();
  private MutableLiveData<List<RssFeed>> personalRssFeeds = new MutableLiveData<>();
  private MutableLiveData<List<Article>> personalArticles = new MutableLiveData<>();

  private void userAsyncFinished(User user) {
    userSearchResult.setValue(user);
  }

  private void feedsAsyncFinished(List<RssFeed> feeds) {
    feedSearchResults.setValue(feeds);
  }

  private void articlesAsyncFinished(List<Article> articles) {
    articleSearchResults.setValue(articles);
  }

  private void personalFeedsAsyncFinished(List<RssFeed> feeds) {
    personalRssFeeds.setValue(feeds);
  }

  private void personalArticlesAsyncFinished(List<Article> articles) {
    personalArticles.setValue(articles);
  }

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
   * Synchronously insert user(s).
   *
   * @param users input users.
   */
  public void insertUserSync(User... users) {
    userDao.insert(users);
  }

  /**
   * Insert user(s) to database.
   *
   * @param user the input user.
   */
  public void insertUser(User... user) {
    new UserInsertAsyncTask(userDao).execute(user);
  }

  /**
   * Update user(s) if its primary key exists.
   *
   * @param user the input user.
   */
  public void updateUser(User... user) {
    new UserUpdateAsyncTask(userDao).execute(user);
  }

  /**
   * Delete user(s) from database.
   *
   * @param user the input user.
   */
  public void deleteUser(User... user) {
    new UserDeleteAsyncTask(userDao).execute(user);
  }

  /**
   * Insert RSS feed(s) into database.
   *
   * @param rssFeed the input RSS feed.
   */
  public void insertRssFeed(RssFeed... rssFeed) {
    RssFeedInsertAsyncTask task = new RssFeedInsertAsyncTask(rssFeedDao);
    task.execute(rssFeed);
  }

  /**
   * Update RSS feed(s) if its primary key exists.
   *
   * @param rssFeed the input RSS feed.
   */
  public void updateRssFeed(RssFeed... rssFeed) {
    RssFeedUpdateAsyncTask task = new RssFeedUpdateAsyncTask(rssFeedDao);
    task.execute(rssFeed);
  }

  /**
   * Delete RSS feed(s) from database.
   *
   * @param rssFeed the input RSS feed.
   */
  public void deleteRssFeed(RssFeed... rssFeed) {
    RssFeedDeleteAsyncTask task = new RssFeedDeleteAsyncTask(rssFeedDao);
    task.execute(rssFeed);
  }

  /**
   * Insert article(s) to database.
   *
   * @param article the input article.
   */
  public void insertArticle(Article... article) {
    articleDao.insert(article);
  }

  /**
   * Update article(s) if its primary key exists.
   *
   * @param article the input article.
   */
  public void updateArticle(Article... article) {
    articleDao.update(article);
  }

  /**
   * Delete article(s) from database.
   *
   * @param article the input article.
   */
  public void deleteArticle(Article... article) {

  }

  /**
   * Find the user by phone number from database.
   *
   * @param phone the input user's phone number.
   */
  public void findUserByPhone(String phone) {
    UserQueryAsyncTask task = new UserQueryAsyncTask(userDao);
    task.delegate = this;
    task.execute(phone);
  }

  /**
   * Find all the RSS feeds of a user.
   *
   * @param user user of interest.
   */
  public void findRssFeedsByUser(User user) {
    PersonalFeedAsyncTask task = new PersonalFeedAsyncTask(userRssFeedJoinDao);
    task.delegate = this;
    task.execute(user.getId());
  }

  /**
   * Find all the articles of a user.
   *
   * @param user user of interest.
   */
  public void findArticlesByUser(User user) {
    PersonalArticleAsyncTask task = new PersonalArticleAsyncTask(userArticleJoinDao);
    task.delegate = this;
    task.execute(user.getId());
  }

  /**
   * Get the result of searching the user by its phone number.
   *
   * @return the result user
   */
  public MutableLiveData<User> getUserSearchResult() {
    return userSearchResult;
  }

  /**
   * Get the result of searching RSS feed(s) by its channel name.
   *
   * @return the result RSS feed(s)
   */
  public MutableLiveData<List<RssFeed>> getFeedSearchResults() {
    return feedSearchResults;
  }

  /**
   * Get the result of searching article(s) by its title.
   *
   * @return the result article(s)
   */
  public MutableLiveData<List<Article>> getArticleSearchResults() {
    return articleSearchResults;
  }

  /**
   * Get the result of searching RSS feed(s) by the user.
   *
   * @return the result RSS feed(s)
   */
  public MutableLiveData<List<RssFeed>> getPersonalRssFeeds() {
    return personalRssFeeds;
  }

  /**
   * Get the result of searching article(s) by the user.
   *
   * @return the result article(s)
   */
  public MutableLiveData<List<Article>> getPersonalArticles() {
    return personalArticles;
  }

  /**
   * Synchronously update user token.
   *
   * @param phone user's phone.
   * @param token user's token.
   */
  public void updateTokenByPhoneSync(@NonNull String phone, @NonNull String token) {
    User user = userDao.getUserByPhone(phone);
    user.setAuthToken(token);
    userDao.update(user);
  }

  /**
   * Synchronously get user by phone.
   *
   * @param phone input phone.
   * @return target user.
   */
  public User getUserByPhoneSync(String phone) {
    return userDao.getUserByPhone(phone);
  }

  /**
   * Synchronously get articles by title.
   *
   * @param title input title.
   * @return target articles.
   */
  public List<Article> getArticlesByTitleSync(String title) {
    return articleDao.getArticlesByTitle(title);
  }

  /**
   * Synchronously get RSS feeds by name.
   *
   * @param name input name.
   * @return target feeds.
   */
  public List<RssFeed> getRssFeedsByNameSync(String name) {
    return rssFeedDao.getRssFeedByName(name);
  }


  private static class UserQueryAsyncTask extends AsyncTask<String, Void, User> {
    private UserDao userDao;
    private Repository delegate = null;

    UserQueryAsyncTask(UserDao dao) {
      userDao = dao;
    }

    @Override
    protected User doInBackground(final String... params) {
      return userDao.getUserByPhone(params[0]);
    }

    @Override
    protected void onPostExecute(User result) {
      delegate.userAsyncFinished(result);
    }
  }


  private static class UserInsertAsyncTask extends AsyncTask<User, Void, Void> {
    private UserDao userDao;

    UserInsertAsyncTask(UserDao dao) {
      userDao = dao;
    }

    @Override
    protected Void doInBackground(final User... params) {
      userDao.insert(params);
      return null;
    }
  }

  private static class UserUpdateAsyncTask extends AsyncTask<User, Void, Void> {
    private UserDao userDao;

    UserUpdateAsyncTask(UserDao dao) {
      userDao = dao;
    }

    @Override
    protected Void doInBackground(final User... params) {
      userDao.update(params);
      return null;
    }
  }


  private static class UserDeleteAsyncTask extends AsyncTask<User, Void, Void> {
    private UserDao userDao;

    UserDeleteAsyncTask(UserDao dao) {
      userDao = dao;
    }

    @Override
    protected Void doInBackground(final User... params) {
      userDao.delete(params);
      return null;
    }
  }


  private static class RssFeedQueryAsyncTask extends AsyncTask<String, Void, List<RssFeed>> {
    private RssFeedDao rssFeedDao;
    private Repository delegate = null;

    RssFeedQueryAsyncTask(RssFeedDao dao) {
      rssFeedDao = dao;
    }

    @Override
    protected List<RssFeed> doInBackground(final String... params) {
      return rssFeedDao.getRssFeedByName(params[0]);
    }

    @Override
    protected void onPostExecute(List<RssFeed> result) {
      delegate.feedsAsyncFinished(result);
    }
  }


  private static class RssFeedInsertAsyncTask extends AsyncTask<RssFeed, Void, Void> {
    private RssFeedDao rssFeedDao;

    RssFeedInsertAsyncTask(RssFeedDao dao) {
      rssFeedDao = dao;
    }

    @Override
    protected Void doInBackground(final RssFeed... params) {
      rssFeedDao.insert(params);
      return null;
    }
  }

  private static class RssFeedUpdateAsyncTask extends AsyncTask<RssFeed, Void, Void> {
    private RssFeedDao rssFeedDao;

    RssFeedUpdateAsyncTask(RssFeedDao dao) {
      rssFeedDao = dao;
    }

    @Override
    protected Void doInBackground(final RssFeed... params) {
      rssFeedDao.update(params);
      return null;
    }
  }


  private static class RssFeedDeleteAsyncTask extends AsyncTask<RssFeed, Void, Void> {
    private RssFeedDao rssFeedDao;

    RssFeedDeleteAsyncTask(RssFeedDao dao) {
      rssFeedDao = dao;
    }

    @Override
    protected Void doInBackground(final RssFeed... params) {
      rssFeedDao.delete(params);
      return null;
    }
  }


  private static class ArticleQueryAsyncTask extends AsyncTask<String, Void, List<Article>> {
    private ArticleDao articleDao;
    private Repository delegate = null;

    ArticleQueryAsyncTask(ArticleDao dao) {
      articleDao = dao;
    }

    @Override
    protected List<Article> doInBackground(final String... params) {
      return articleDao.getArticlesByTitle(params[0]);
    }

    @Override
    protected void onPostExecute(List<Article> result) {
      delegate.articlesAsyncFinished(result);
    }
  }


  private static class ArticleInsertAsyncTask extends AsyncTask<Article, Void, Void> {
    private ArticleDao articleDao;

    ArticleInsertAsyncTask(ArticleDao dao) {
      articleDao = dao;
    }

    @Override
    protected Void doInBackground(final Article... params) {
      articleDao.insert(params);
      return null;
    }
  }

  private static class ArticleUpdateAsyncTask extends AsyncTask<Article, Void, Void> {
    private ArticleDao articleDao;

    ArticleUpdateAsyncTask(ArticleDao dao) {
      articleDao = dao;
    }

    @Override
    protected Void doInBackground(final Article... params) {
      articleDao.update(params);
      return null;
    }
  }


  private static class ArticleDeleteAsyncTask extends AsyncTask<Article, Void, Void> {
    private ArticleDao articleDao;

    ArticleDeleteAsyncTask(ArticleDao dao) {
      articleDao = dao;
    }

    @Override
    protected Void doInBackground(final Article... params) {
      articleDao.delete(params);
      return null;
    }
  }

  private static class PersonalFeedAsyncTask extends AsyncTask<Long, Void, List<RssFeed>> {
    private UserRssFeedJoinDao userRssFeedJoinDao;
    private Repository delegate = null;

    PersonalFeedAsyncTask(UserRssFeedJoinDao dao) {
      userRssFeedJoinDao = dao;
    }

    @Override
    protected List<RssFeed> doInBackground(final Long... params) {
      userRssFeedJoinDao.getRssFeedsByUserId(params[0]);
      return null;
    }


    @Override
    protected void onPostExecute(List<RssFeed> result) {
      delegate.personalFeedsAsyncFinished(result);
    }
  }


  private static class PersonalArticleAsyncTask extends AsyncTask<Long, Void, List<Article>> {
    private UserArticleJoinDao userArticleJoinDao;
    private Repository delegate = null;

    PersonalArticleAsyncTask(UserArticleJoinDao dao) {
      userArticleJoinDao = dao;
    }

    @Override
    protected List<Article> doInBackground(final Long... params) {
      userArticleJoinDao.getArticlesByUserId(params[0]);
      return null;
    }


    @Override
    protected void onPostExecute(List<Article> result) {
      delegate.personalArticlesAsyncFinished(result);
    }
  }

}
