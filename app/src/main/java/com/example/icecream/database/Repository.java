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

  private MutableLiveData<Boolean> downloadComplete = new MutableLiveData<>();

  private MutableLiveData<User> userSearchResult = new MutableLiveData<>();
  private MutableLiveData<Article> articleSearchResults = new MutableLiveData<>();
  private MutableLiveData<List<RssFeed>> personalRssFeeds = new MutableLiveData<>();
  private MutableLiveData<List<Article>> personalArticles = new MutableLiveData<>();
  private MutableLiveData<List<Article>> starArticles = new MutableLiveData<>();
  private MutableLiveData<List<Article>> commonArticles = new MutableLiveData<>();

  private final static String TAG = "Repository: ";

  private void setPersonalRssFeeds(List<RssFeed> feeds) {
    personalRssFeeds.setValue(feeds);
  }

  private void setPersonalArticles(List<Article> articles) {
    personalArticles.setValue(articles);
  }

  private void setStarArticles(List<Article> articles) {
    starArticles.setValue(articles);
  }

  private void setArticleSearchResults(Article article) {
    articleSearchResults.setValue(article);
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
   * Insert article(s) to database.
   *
   * @param article the input article.
   */
  public void insertArticle(Article... article) {
    new ArticleInsertAsyncTask(articleDao).execute(article);
  }

  /**
   * Find article.
   *
   * @param id article id.
   */
  public void findArticleById(Long id) {
    new ArticleIdQueryAsyncTask(articleDao, this).execute(id);
  }

  public void findArticleByTitle(String title) {
    new ArticleTitleQueryAsyncTask(articleDao, this).execute(title);
  }

  public void findUserByPhone(String phone) {
    new UserQueryAsyncTask(userDao, this).execute(phone);
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

  /**
   * Find the user articles.
   *
   * @param phone user phone.
   */
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
    StarArticlesQueryAsyncTask task = new StarArticlesQueryAsyncTask(userDao, userArticleJoinDao);
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
   * Get the result of searching article(s) by its title.
   *
   * @return the result article(s)
   */
  public MutableLiveData<Article> getArticleSearchResults() {
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
   * Update paragraph.
   *
   * @param id   article id.
   * @param para paragraph.
   */
  public void updateArticleParagraphSync(Long id, String para) {
    Article article = articleDao.getArticleById(id);
    article.setParagraph(para);
    articleDao.update(article);
  }

  /**
   * Star article.
   *
   * @param phone   user phone.
   * @param article article.
   */
  public void starArticleByPhone(String phone, Article article) {
    new StarAsyncTask(this).execute(new ParamPhoneArticle(phone, article));
  }

  public void unstarArticleByPhone(String phone, Article article) {
    new UnStarAsyncTask(this).execute(new ParamPhoneArticle(phone, article));

  }

  private static class UserQueryAsyncTask extends AsyncTask<String, Void, User> {
    private UserDao userDao;
    private Repository delegate;

    UserQueryAsyncTask(UserDao dao, Repository repository) {
      userDao = dao;
      delegate = repository;
    }

    @Override
    protected User doInBackground(final String... params) {
      return userDao.getUserByPhone(params[0]);
    }

    @Override
    protected void onPostExecute(User result) {
      delegate.userSearchResult.setValue(result);
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

  private static class ArticleIdQueryAsyncTask extends AsyncTask<Long, Void, Article> {
    private ArticleDao articleDao;
    private Repository delegate;

    ArticleIdQueryAsyncTask(ArticleDao dao, Repository repository) {
      articleDao = dao;
      delegate = repository;
    }

    @Override
    protected Article doInBackground(final Long... params) {
      return articleDao.getArticleById(params[0]);
    }

    @Override
    protected void onPostExecute(Article result) {
      delegate.setArticleSearchResults(result);
    }
  }

  private static class ArticleTitleQueryAsyncTask extends AsyncTask<String, Void, List<Article>> {
    private ArticleDao articleDao;
    private Repository delegate;

    ArticleTitleQueryAsyncTask(ArticleDao dao, Repository repository) {
      articleDao = dao;
      delegate = repository;
    }

    @Override
    protected List<Article> doInBackground(final String... params) {
      return articleDao.getArticlesByTitle(params[0]);
    }

    @Override
    protected void onPostExecute(List<Article> result) {
      if (result.size() > 0) {
        delegate.setArticleSearchResults(result.get(0));
      } else {
        delegate.setArticleSearchResults(null);
      }
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
          Log.i(TAG, e.getMessage());
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


  private static class StarArticlesQueryAsyncTask extends AsyncTask<String, Void, List<Article>> {
    private UserDao userDao;
    private UserArticleJoinDao userArticleJoinDao;
    private Repository delegate = null;

    StarArticlesQueryAsyncTask(UserDao userDao, UserArticleJoinDao dao) {
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

  private static class ParamPhoneFeedArticleList {
    String phone;
    List<RssFeed> rssFeeds;
    List<Article> articles;

    ParamPhoneFeedArticleList(String phone, List<RssFeed> rssFeeds, List<Article> articles) {
      this.phone = phone;
      this.rssFeeds = rssFeeds;
      this.articles = articles;
    }
  }


  private static class InsertUserRssFeedsArticleAsyncTask
      extends AsyncTask<ParamPhoneFeedArticleList, Void, Void> {
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

  private static class ParamPhoneArticle {
    String phone;
    Article article;

    ParamPhoneArticle(String phone, Article article) {
      this.phone = phone;
      this.article = article;
    }
  }

  private static class StarAsyncTask extends AsyncTask<ParamPhoneArticle, Void, Void> {

    private UserDao userDao;
    private UserArticleJoinDao userArticleJoinDao;

    StarAsyncTask(Repository repository) {
      userDao = repository.userDao;
      userArticleJoinDao = repository.userArticleJoinDao;
    }

    @Override
    protected Void doInBackground(final ParamPhoneArticle... params) {
      Long userId = userDao.getUserByPhone(params[0].phone).getId();
      UserArticleJoin userArticleJoin = new UserArticleJoin(userId, params[0].article.getId());
      userArticleJoinDao.insert(userArticleJoin);
      return null;
    }
  }

  private static class UnStarAsyncTask extends AsyncTask<ParamPhoneArticle, Void, Void> {

    private UserDao userDao;
    private UserArticleJoinDao userArticleJoinDao;

    UnStarAsyncTask(Repository repository) {
      userDao = repository.userDao;
      userArticleJoinDao = repository.userArticleJoinDao;
    }

    @Override
    protected Void doInBackground(final ParamPhoneArticle... params) {
      Long userId = userDao.getUserByPhone(params[0].phone).getId();
      UserArticleJoin userArticleJoin = new UserArticleJoin(userId, params[0].article.getId());
      userArticleJoinDao.delete(userArticleJoin);
      return null;
    }
  }
}
