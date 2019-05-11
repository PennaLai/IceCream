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

  private Repository mRepository;

  private LiveData<List<User>> allUsers;

  private LiveData<List<RssFeed>> allRssFeeds;

  private LiveData<List<Article>> allArticles;

  public ViewModel(Application application) {
    super(application);
    mRepository = new Repository(application);
    allUsers = mRepository.getAllUsers();
    allRssFeeds = mRepository.getAllRssFeeds();
    allArticles = mRepository.getAllArticles();
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
   * Get the user by phone number.
   *
   * @param phone input phone number.
   * @return the user.
   */
  public LiveData<User> getUserByPhone(String phone) {
    return mRepository.getUserByPhone(phone);
  }

  /**
   * Get all the RSS feeds of a user.
   *
   * @param user user of interest.
   * @return the RSS feed list of the user.
   */
  public LiveData<List<RssFeed>> getRssFeedsOfUser(User user) {
    return mRepository.getRssFeedsByUser(user);
  }

  /**
   * Get all the articles of a user.
   *
   * @param user user of interest.
   * @return the article list of the user.
   */
  public LiveData<List<Article>> getArticlesOfUser(User user) {
    return mRepository.getArticlesByUser(user);
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
}
