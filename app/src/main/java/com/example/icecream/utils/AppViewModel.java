package com.example.icecream.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.icecream.database.Repository;
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;

import java.util.List;

/**
 * This view model is to provide data to the UI and survive configuration changes. <br/>
 * Activity and Fragment classes are responsible for drawing data to the screen,
 * AppViewModel class takes care of holding and processing all the data needed for the UI.
 *
 * @author Kemo
 */
public class AppViewModel extends AndroidViewModel {

  private Repository repository;

  private LiveData<List<User>> allUsers;

  private MutableLiveData<User> userSearchResult;

  private MutableLiveData<Article> articleSearchResult;

  private MutableLiveData<List<RssFeed>> personalRssFeeds;

  private MutableLiveData<List<Article>> personalArticles;

  private MutableLiveData<List<Article>> starArticles;

  private MutableLiveData<List<Article>> commonArticles;

  private MutableLiveData<Boolean> downloadComplete;

  /**
   * View model constructor.
   *
   * @param application the register application.
   */
  public AppViewModel(Application application) {
    super(application);
    repository = Repository.getInstance(application);
    allUsers = repository.getAllUsers();
    userSearchResult = repository.getUserSearchResult();
    articleSearchResult = repository.getArticleSearchResults();
    personalRssFeeds = repository.getPersonalRssFeeds();
    personalArticles = repository.getPersonalArticles();
    starArticles = repository.getStarArticles();
    commonArticles = repository.getCommonArticles();
    downloadComplete = repository.getDownloadComplete();
  }

  /**
   * Get all the users in local database.
   *
   * @return local users.
   */
  public LiveData<List<User>> getAllUsers() {
    return allUsers;
  }

  /**
   * Execute insertion for user(s) to database.
   *
   * @param user input user(s).
   */
  public void insertUser(User user) {
    repository.insertUser(user);
  }

  /**
   * Update the user(s).
   *
   * @param user input user(s).
   */
  public void updateUser(User... user) {
    repository.updateUser(user);
  }

  /**
   * Update article in database.
   *
   * @param id article id.
   */
  public void updateArticleParagraph(Long id, String paragraph) {
    repository.updateArticleParagraph(id, paragraph);
  }

  /**
   * Insert the RSS feed(s).
   *
   * @param rssFeeds RSS feeds.
   */
  public void insertAllRssFeeds(RssFeed... rssFeeds) {
    repository.insertRssFeeds(rssFeeds);
  }

  /**
   * Get the search result of the user.
   *
   * @return result user.
   */
  public MutableLiveData<User> getUserSearchResult() {
    return userSearchResult;
  }

  /**
   * Get the article search result.
   *
   * @return article.
   */
  public MutableLiveData<Article> getArticleSearchResult() {
    return articleSearchResult;
  }

  /**
   * Getter for the personal RSS feeds.
   *
   * @return result RSS feeds.
   */
  public MutableLiveData<List<RssFeed>> getPersonalRssFeeds() {
    return personalRssFeeds;
  }

  /**
   * Getter for personal the articles.
   *
   * @return result articles.
   */
  public MutableLiveData<List<Article>> getPersonalArticles() {
    return personalArticles;
  }

  /**
   * Getter for star articles.
   *
   * @return list.
   */
  public MutableLiveData<List<Article>> getStarArticles() {
    return starArticles;
  }

  /**
   * Getter for common articles.
   *
   * @return list.
   */
  public MutableLiveData<List<Article>> getCommonArticles() {
    return commonArticles;
  }

  /**
   * Getter for download status.
   *
   * @return boolean.
   */
  public MutableLiveData<Boolean> getDownloadComplete() {
    return downloadComplete;
  }

  /**
   * Setter for personal RSS feeds.
   *
   * @param feeds feeds
   */
  public void setPersonalRssFeeds(List<RssFeed> feeds) {
    personalRssFeeds.setValue(feeds);
  }

  /**
   * Setter for personal articles.
   *
   * @param articles articles.
   */
  public void setPersonalArticles(List<Article> articles) {
    personalArticles.setValue(articles);
  }

  /**
   * Setter for common articles.
   *
   * @param articles articles.
   */
  public void setCommonArticles(List<Article> articles) {
    commonArticles.setValue(articles);
  }

  /**
   * Subscribe.
   *
   * @param phone phone.
   * @param url   feed url.
   */
  public void subscribe(String phone, String url) {
    repository.insertUserRssFeedByPhoneUrl(phone, url);
  }

  /**
   * Unsubscribe.
   *
   * @param phone phone.
   * @param url   feed url.
   */
  public void unsubscribe(String phone, String url) {
    repository.deleteUserRssFeedByPhoneUrl(phone, url);
  }

  /**
   * Star the article.
   *
   * @param phone user phone.
   * @param url   article url.
   */
  public void star(String phone, String url) {

  }

  /**
   * Unstar the article.
   *
   * @param phone user phone.
   * @param url   article url.
   */
  public void unstar(String phone, String url) {

  }

  /**
   * Load the feeds from phone.
   *
   * @param phone phone.
   */
  public void loadRssFeedsByPhone(String phone) {
    repository.findRssFeedsByPhone(phone);
  }

  /**
   * Load the articles without network.
   *
   * @param phone user phone.
   */
  public void loadArticlesByPhone(String phone) {
    repository.findPersonalArticlesByPhone(phone);
  }

  /**
   * Load the starred articles.
   *
   * @param phone phone.
   */
  public void loadStarArticlesByPhone(String phone) {
    repository.findStarArticlesByPhone(phone);
  }

  /**
   * Insert articles.
   *
   * @param articles article list.
   */
  public void insertArticles(List<Article> articles) {
    repository.insertArticle(articles.toArray(new Article[0]));
  }

  /**
   * Insert RSS feeds.
   *
   * @param phone    user phone.
   * @param rssFeeds rss feed list.
   * @param articles article list.
   */
  public void insertPersonalRssFeedsArticles(
      String phone, List<RssFeed> rssFeeds, List<Article> articles) {
    repository.insertUserRssFeedsArticles(phone, rssFeeds, articles);
  }

  /**
   * Finds article by title.
   *
   * @param title title.
   */
  public void findArticleByTitle(String title) {
    repository.findArticleByTitle(title);
  }

  /**
   * Finds article by id.
   *
   * @param id article id.
   */
  public void findArticleById(Long id) {
    repository.findArticleById(id);
  }

}
