package com.example.icecream.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.icecream.database.dao.ArticleDao;
import com.example.icecream.database.dao.RssFeedDao;
import com.example.icecream.database.dao.UserArticleJoinDao;
import com.example.icecream.database.dao.UserDao;
import com.example.icecream.database.dao.UserRssFeedJoinDao;
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;
import com.example.icecream.database.entity.UserArticleJoin;
import com.example.icecream.database.entity.UserRssFeedJoin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * The repository to hold all the data in local database.
 *
 * @author Kemo
 */
public class Repository {

  private static volatile Repository instance;

  private UserDao userDao;
  private RssFeedDao rssFeedDao;
  private ArticleDao articleDao;

  private UserRssFeedJoinDao userRssFeedJoinDao;
  private UserArticleJoinDao userArticleJoinDao;
  private LiveData<List<User>> allUsers;
  private LiveData<List<RssFeed>> allRssFeeds;
  private LiveData<List<Article>> allArticles;

  private MutableLiveData<Boolean> downloadComplete = new MutableLiveData<>();

  private MutableLiveData<User> userSearchResult = new MutableLiveData<>();
  private MutableLiveData<List<RssFeed>> feedSearchResults = new MutableLiveData<>();
  private MutableLiveData<List<Article>> articleSearchResults = new MutableLiveData<>();
  private MutableLiveData<List<RssFeed>> personalRssFeeds = new MutableLiveData<>();
  private MutableLiveData<List<Article>> personalArticles = new MutableLiveData<>();
  private MutableLiveData<List<Article>> starArticles = new MutableLiveData<>();
  private MutableLiveData<List<Article>> commonArticles = new MutableLiveData<>();

  private void setUser(User user) {
    userSearchResult.setValue(user);
  }

  private void setRssFeeds(List<RssFeed> feeds) {
    feedSearchResults.setValue(feeds);
  }

  private void setArticles(List<Article> articles) {
    articleSearchResults.setValue(articles);
  }

  public void setPersonalRssFeeds(List<RssFeed> feeds) {
    personalRssFeeds.setValue(feeds);
  }

  public void setPersonalArticles(List<Article> articles) {
    personalArticles.setValue(articles);
  }

  public void setStarArticles(List<Article> articles) {
    starArticles.setValue(articles);
  }

  public void setDownloadComplete(Boolean flag) {
    downloadComplete.setValue(flag);
  }

  public void setCommonArticles(List<Article> articles) {
    commonArticles.setValue(articles);
  }

  /**
   * Constructor<br/>
   * register the application to repository.
   *
   * @param application input application.
   */
  private Repository(Application application) {
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
   * Gets the singleton instance.
   *
   * @param application input application.
   * @return instance.
   */
  public static Repository getInstance(final Application application) {
    if (instance == null) {
      synchronized (Repository.class) {
        if (instance == null) {
          instance = new Repository(application);
        }
      }
    }
    return instance;
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
   * Synchronously insert user(s).
   *
   * @param user input user.
   */
  public void insertUserSync(User user) {
    if (userDao.insert(user) == -1) {
      userDao.update(user);
    }
  }

  /**
   * Insert user(s) to database.
   *
   * @param user the input user.
   */
  public void insertUser(User user) {
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

  public void insertRssFeeds(RssFeed... rssFeeds) {
    new RssFeedInsertAsyncTask(rssFeedDao).execute(rssFeeds);
  }

  /**
   * Insert RSS feed(s) and articles into database.
   *
   * @param phone    user phone.
   * @param rssFeeds input RSS feeds.
   * @param articles input articles.
   */
  public void insertUserRssFeedsArticles(
      String phone,
      List<RssFeed> rssFeeds,
      List<Article> articles) {
    new InsertUserRssFeedsArticleAsyncTask(
        userDao,
        userRssFeedJoinDao,
        rssFeedDao,
        userArticleJoinDao,
        articleDao
    ).execute(new ParamPhoneFeedArticleList(phone, rssFeeds, articles));
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
    new ArticleInsertAsyncTask(articleDao).execute(article);
  }

  /**
   * Update article(s) if its primary key exists.
   *
   * @param article the input article.
   */
  public void updateArticles(Article... article) {
    new ArticleUpdateAsyncTask(articleDao).execute(article);
  }

  /**
   * Delete article(s) from database.
   *
   * @param article the input article.
   */
  public void deleteArticle(Article... article) {
    new ArticleDeleteAsyncTask(articleDao).execute(article);
  }

  /**
   * Insert user rss feed join.
   *
   * @param phone phone.
   * @param url   url.
   */
  public void insertUserRssFeedByPhoneUrl(String phone, String url) {
    new InsertUserRssFeedAsyncTask(
        userRssFeedJoinDao,
        userDao,
        rssFeedDao,
        this).execute(phone, url);
  }

  /**
   * Delete user rss feed join.
   *
   * @param phone phone.
   * @param url   url.
   */
  public void deleteUserRssFeedByPhoneUrl(String phone, String url) {
    new DeleteUserRssFeedAsyncTask(
        userRssFeedJoinDao,
        userDao,
        rssFeedDao).execute(phone, url);
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
   * @param phone user phone.
   */
  public void findRssFeedsByPhone(String phone) {
    PersonalFeedsGetAsyncTask task = new PersonalFeedsGetAsyncTask(
        userDao, userRssFeedJoinDao);
    task.delegate = this;
    task.execute(phone);
  }


  public void findPersonalArticlesByPhone(String phone) {
    PersonalArticlesGetAsyncTask task = new PersonalArticlesGetAsyncTask(
        userDao, userRssFeedJoinDao, articleDao
    );
    task.delegate = this;
    task.execute(phone);
  }

  /**
   * Find all the starred articles of a user.
   *
   * @param phone user phone.
   */
  public void findStarArticlesByPhone(String phone) {
    StarArticlesAsyncTask task = new StarArticlesAsyncTask(userDao, userArticleJoinDao);
    task.delegate = this;
    task.execute(phone);
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
   * Get the starred articles of user.
   *
   * @return article list.
   */
  public MutableLiveData<List<Article>> getStarArticles() {
    return starArticles;
  }

  /**
   * Get the common article.
   *
   * @return common article list.
   */
  public MutableLiveData<List<Article>> getCommonArticles() {
    return commonArticles;
  }

  /**
   * Get the download status.
   *
   * @return status.
   */
  public MutableLiveData<Boolean> getDownloadComplete() {
    return downloadComplete;
  }

  /**
   * Synchronously update user token.
   *
   * @param user  user.
   * @param token user's token.
   */
  public void updateTokenSync(@NonNull User user, @NonNull String token) {
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

  /**
   * Update paragraph.
   *
   * @param id   article id.
   * @param para paragraph.
   */
  public void updateArticleParagraph(Long id, String para) {
    UpdateSpeechAsyncTask task = new UpdateSpeechAsyncTask(articleDao);
    task.delegate = this;
    task.execute(new ParamSpeech(id, para));
  }


  /**
   * Synchronously get RSS feeds by url.
   *
   * @param url input url.
   * @return target feed.
   */
  public RssFeed getRssFeedByUrlSync(String url) {
    return rssFeedDao.getRssFeedByUrl(url);
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
      delegate.setUser(result);
    }
  }


  private static class UserInsertAsyncTask extends AsyncTask<User, Void, Void> {
    private UserDao userDao;

    UserInsertAsyncTask(UserDao dao) {
      userDao = dao;
    }

    @Override
    protected Void doInBackground(final User... params) {
      if (userDao.insert(params[0]) == -1) {
        userDao.update(params);
      }
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
      delegate.setRssFeeds(result);
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
      delegate.setArticles(result);
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

  private static class PersonalFeedsGetAsyncTask extends AsyncTask<String, Void, List<RssFeed>> {
    private UserDao userDao;
    private UserRssFeedJoinDao userRssFeedJoinDao;
    private Repository delegate = null;

    PersonalFeedsGetAsyncTask(UserDao userDao, UserRssFeedJoinDao dao) {
      this.userDao = userDao;
      userRssFeedJoinDao = dao;
    }

    @Override
    protected List<RssFeed> doInBackground(final String... params) {
      User user = userDao.getUserByPhone(params[0]);
      List<RssFeed> result = null;
      if (user != null) {
        result = userRssFeedJoinDao.getRssFeedsByUserId(user.getId());
      }
      return result;
    }

    @Override
    protected void onPostExecute(List<RssFeed> result) {
      delegate.setPersonalRssFeeds(result);
    }
  }

  private static class PersonalArticlesGetAsyncTask extends AsyncTask<String, Void, List<Article>> {
    private UserDao userDao;
    private UserRssFeedJoinDao userRssFeedJoinDao;
    private ArticleDao articleDao;
    private Repository delegate = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    PersonalArticlesGetAsyncTask(UserDao userDao,
                                 UserRssFeedJoinDao userRssFeedJoinDao,
                                 ArticleDao articleDao) {
      this.userDao = userDao;
      this.userRssFeedJoinDao = userRssFeedJoinDao;
      this.articleDao = articleDao;
    }

    @Override
    protected List<Article> doInBackground(final String... params) {
      Long userId = userDao.getUserByPhone(params[0]).getId();
      List<RssFeed> rssFeeds = userRssFeedJoinDao.getRssFeedsByUserId(userId);
      List<Article> articles = new LinkedList<>();
      for (RssFeed rssFeed : rssFeeds) {
        Log.i(TAG, rssFeed.getUrl());
        articles.addAll(articleDao.getArticlesByRssFeedUrl(rssFeed.getUrl()));
        if (articles.size() > 30) {
          // do not load too much.
          break;
        }
      }
      Collections.sort(articles, (a1, a2) -> {
        try {
          return sdf.parse(a2.getPublishTime()).compareTo(sdf.parse(a1.getPublishTime()));
        } catch (ParseException e) {
          e.printStackTrace();
        }
        return 0;
      });
      return articles;
    }

    @Override
    protected void onPostExecute(List<Article> result) {
      delegate.setPersonalArticles(result);
    }
  }


  private static class StarArticlesAsyncTask extends AsyncTask<String, Void, List<Article>> {
    private UserDao userDao;
    private UserArticleJoinDao userArticleJoinDao;
    private Repository delegate = null;

    StarArticlesAsyncTask(UserDao userDao, UserArticleJoinDao dao) {
      this.userDao = userDao;
      userArticleJoinDao = dao;
    }

    @Override
    protected List<Article> doInBackground(final String... params) {
      return userArticleJoinDao.getArticlesByUserId(userDao.getUserByPhone(params[0]).getId());
    }


    @Override
    protected void onPostExecute(List<Article> result) {
      delegate.setStarArticles(result);
    }
  }

  private static class InsertUserRssFeedAsyncTask extends AsyncTask<String, Void, List<RssFeed>> {
    private UserRssFeedJoinDao userRssFeedJoinDao;
    private UserDao userDao;
    private RssFeedDao rssFeedDao;
    private Repository delegate;

    InsertUserRssFeedAsyncTask(UserRssFeedJoinDao userRssFeedJoinDao,
                               UserDao userDao,
                               RssFeedDao rssFeedDao,
                               Repository repository) {
      this.userRssFeedJoinDao = userRssFeedJoinDao;
      this.userDao = userDao;
      this.rssFeedDao = rssFeedDao;
      delegate = repository;
    }

    @Override
    protected List<RssFeed> doInBackground(final String... params) {
      Long userId = userDao.getUserByPhone(params[0]).getId();
      userRssFeedJoinDao.insert(new UserRssFeedJoin(
          userId,
          rssFeedDao.getRssFeedByUrl(params[1]).getId()));
      return userRssFeedJoinDao.getRssFeedsByUserId(userId);
    }

    @Override
    protected void onPostExecute(List<RssFeed> result) {
      delegate.setPersonalRssFeeds(result);
    }
  }

  private static class DeleteUserRssFeedAsyncTask extends AsyncTask<String, Void, Void> {
    private UserRssFeedJoinDao userRssFeedJoinDao;
    private UserDao userDao;
    private RssFeedDao rssFeedDao;

    DeleteUserRssFeedAsyncTask(UserRssFeedJoinDao userRssFeedJoinDao,
                               UserDao userDao, RssFeedDao rssFeedDao) {
      this.userRssFeedJoinDao = userRssFeedJoinDao;
      this.userDao = userDao;
      this.rssFeedDao = rssFeedDao;
    }

    @Override
    protected Void doInBackground(final String... params) {
      userRssFeedJoinDao.delete(new UserRssFeedJoin(
          userDao.getUserByPhone(params[0]).getId(),
          rssFeedDao.getRssFeedByUrl(params[1]).getId()));
      return null;
    }
  }

  private class ParamPhoneFeedArticleList {
    String phone;
    List<RssFeed> rssFeeds;
    List<Article> articles;

    ParamPhoneFeedArticleList(String phone, List<RssFeed> rssFeeds, List<Article> articles) {
      this.phone = phone;
      this.rssFeeds = rssFeeds;
      this.articles = articles;
    }
  }


  private static class InsertUserRssFeedsArticleAsyncTask extends AsyncTask<ParamPhoneFeedArticleList, Void, Void> {
    private UserDao userDao;
    private UserRssFeedJoinDao userRssFeedJoinDao;
    private RssFeedDao rssFeedDao;
    private UserArticleJoinDao userArticleJoinDao;
    private ArticleDao articleDao;

    InsertUserRssFeedsArticleAsyncTask(
        UserDao userDao, UserRssFeedJoinDao userRssFeedJoinDao, RssFeedDao rssFeedDao,
        UserArticleJoinDao userArticleJoinDao, ArticleDao articleDao) {
      this.userDao = userDao;
      this.userRssFeedJoinDao = userRssFeedJoinDao;
      this.rssFeedDao = rssFeedDao;
      this.userArticleJoinDao = userArticleJoinDao;
      this.articleDao = articleDao;
    }

    @Override
    protected Void doInBackground(final ParamPhoneFeedArticleList... params) {
      List<RssFeed> rssFeeds = params[0].rssFeeds;
      List<Article> articles = params[0].articles;
      for (RssFeed rssFeed : rssFeeds) {
        rssFeedDao.insert(rssFeed);
      }
      for (Article article : articles) {
        articleDao.insert(article);
      }
      Long userId = userDao.getUserByPhone(params[0].phone).getId();
      for (RssFeed rssFeed : rssFeeds) {
        userRssFeedJoinDao.insert(new UserRssFeedJoin(
            userId, rssFeedDao.getRssFeedByUrl(rssFeed.getUrl()).getId())
        );
      }
      for (Article article : articles) {
        userArticleJoinDao.insert(new UserArticleJoin(
            userId, article.getId()
        ));
      }
      return null;
    }
  }

  private static class ParamSpeech {
    Long id;
    String paragraph;

    ParamSpeech(Long id, String paragraph) {
      this.id = id;
      this.paragraph = paragraph;
    }
  }


  private static class UpdateSpeechAsyncTask extends AsyncTask<ParamSpeech, Void, Void> {
    private ArticleDao articleDao;
    private Repository delegate;

    UpdateSpeechAsyncTask(ArticleDao articleDao) {
      this.articleDao = articleDao;
    }

    @Override
    protected Void doInBackground(final ParamSpeech... params) {
      Article article = articleDao.getArticleById(params[0].id);
      article.setParagraph(params[0].paragraph);
      articleDao.update(article);
      return null;
    }

    @Override
    protected void onPostExecute(Void result) {
      delegate.setDownloadComplete(true);
    }

  }

}
