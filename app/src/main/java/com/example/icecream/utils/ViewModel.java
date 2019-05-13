package com.example.icecream.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.icecream.database.Repository;
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;

import java.util.List;

/**
 * This view model is to provide data to the UI and survive configuration changes. <br/>
 * Activity and Fragment classes are responsible for drawing data to the screen,
 * ViewModel class takes care of holding and processing all the data needed for the UI.
 *
 * @author Kemo
 */
public class ViewModel extends AndroidViewModel {

  private Repository repository;

  private LiveData<List<User>> allUsers;

  private LiveData<List<RssFeed>> allRssFeeds;

  private LiveData<List<Article>> allArticles;

  /**
   * View model constructor.
   *
   * @param application the register application.
   */
  public ViewModel(Application application) {
    super(application);
    repository = new Repository(application);
    allUsers = repository.getAllUsers();
    allRssFeeds = repository.getAllRssFeeds();
    allArticles = repository.getAllArticles();
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


}
