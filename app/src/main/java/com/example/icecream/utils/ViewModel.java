package com.example.icecream.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;
import com.example.icecream.utils.repository.ArticleRepository;
import com.example.icecream.utils.repository.RssFeedRepository;
import com.example.icecream.utils.repository.UserRepository;

import java.util.List;

/**
 * This view model is to provide data to the UI and survive configuration changes. <br/>
 * Activity and Fragment classes are responsible for drawing data to the screen,
 * ViewModel class takes care of holding and processing all the data needed for the UI.
 *
 * @author Kemo
 */
public class ViewModel extends AndroidViewModel {

  private UserRepository mUserRepository;

  private RssFeedRepository mRssFeedRepository;

  private ArticleRepository mArticleRepository;

  private LiveData<List<User>> allUsers;

  private LiveData<List<RssFeed>> allRssFeeds;

  private LiveData<List<Article>> allArticles;

  public ViewModel(Application application) {
    super(application);
    mUserRepository = new UserRepository(application);
    mRssFeedRepository = new RssFeedRepository(application);
    mArticleRepository = new ArticleRepository(application);
    allUsers = mUserRepository.getAllUsers();
    allRssFeeds = mRssFeedRepository.getAllFeeds();
    allArticles = mArticleRepository.getAllArticles();
  }

  public LiveData<List<User>> getAllUsers() {
    return allUsers;
  }

  public LiveData<List<RssFeed>> getAllRssFeeds() {
    return allRssFeeds;
  }

  public LiveData<List<Article>> getAllArticles() {
    return allArticles;
  }

  public void insertUser(User user) {
    mUserRepository.insert(user);
  }

  public void insertRssFeed(RssFeed rssFeed) {
    mRssFeedRepository.insert(rssFeed);
  }

  public void insertArticle(Article article) {
    mArticleRepository.insert(article);
  }

  public void deleteUser(String phone) {
    mUserRepository.delete(phone);
  }

  public void getUserByPhone(String phone) {
    mUserRepository.getUserByPhone(phone);
  }

}
