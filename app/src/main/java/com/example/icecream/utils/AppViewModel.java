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

  private LiveData<List<RssFeed>> allRssFeeds;

  private LiveData<List<Article>> allArticles;

  private MutableLiveData<User> userSearchResult;

  private MutableLiveData<List<RssFeed>> personalRssFeeds;

  private MutableLiveData<List<Article>> personalArticles;

  /**
   * View model constructor.
   *
   * @param application the register application.
   */
  public AppViewModel(Application application) {
    super(application);
    repository = new Repository(application);

    allUsers = repository.getAllUsers();
    allRssFeeds = repository.getAllRssFeeds();
    allArticles = repository.getAllArticles();
    userSearchResult = repository.getUserSearchResult();
    personalRssFeeds = repository.getPersonalRssFeeds();
    personalArticles = repository.getPersonalArticles();
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
   * Get all the feeds that currently have been stored.
   *
   * @return List of RSS feeds.
   */
  public LiveData<List<RssFeed>> getAllRssFeeds() {
    return allRssFeeds;
  }

  /**
   * Get all the articles that currently have been stored.
   *
   * @return List of articles.
   */
  public LiveData<List<Article>> getAllArticles() {
    return allArticles;
  }

  /**
   * Find the user by phone number.
   *
   * @param phone the input phone number.
   */
  public void findUserByPhone(String phone) {
    repository.findUserByPhone(phone);
  }

  /**
   * Find all Feeds of the user.
   *
   * @param user user of interest.
   */
  public void findRssFeedsByUser(User user) {
    repository.findRssFeedsByUser(user);
  }

  /**
   * Find all articles of the user.
   *
   * @param user user of interest.
   */
  public void findArticlesByUser(User user) {
    repository.findArticlesByUser(user);
  }

  /**
   * Get all the RSS feeds of a user.
   *
   * @return the RSS feed list of the user.
   */
  public LiveData<List<RssFeed>> getRssFeedsOfUser() {
    return repository.getPersonalRssFeeds();
  }

  /**
   * Get all the articles of a user.
   *
   * @return the article list of the user.
   */
  public LiveData<List<Article>> getArticlesOfUser() {
    return repository.getPersonalArticles();
  }

  /**
   * Execute insertion for user(s) to database.
   *
   * @param user input user(s).
   */
  public void insertUser(User... user) {
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
   * Get the search result of the user.
   *
   * @return result user.
   */
  public MutableLiveData<User> getUserSearchResult() {
    return userSearchResult;
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
   * Setter for personal RSS feeds.
   *
   * @param feeds feeds
   */
  public void setPersonalRssFeeds(List<RssFeed> feeds) {
    repository.setPersonalRssFeeds(feeds);
  }

  /**
   * Setter for personal articles.
   *
   * @param articles articles.
   */
  public void setPersonalArticles(List<Article> articles) {
    repository.setPersonalArticles(articles);
  }

  /**
   * Subscribe.
   *
   * @param phone phone.
   * @param url   url.
   */
  public void subscribe(String phone, String url) {
    User user = repository.getUserByPhoneSync(phone);
    RssFeed rssFeed = repository.getRssFeedByUrlSync(url);
    repository.insertUserRssFeed(user, rssFeed);
  }

  /**
   * Unsubscribe.
   *
   * @param phone phone.
   * @param url   url.
   */
  public void unsubscribe(String phone, String url) {
    User user = repository.getUserByPhoneSync(phone);
    RssFeed rssFeed = repository.getRssFeedByUrlSync(url);
    repository.deleteUserRssFeed(user, rssFeed);
  }
}
