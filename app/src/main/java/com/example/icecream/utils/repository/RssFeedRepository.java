package com.example.icecream.utils.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import com.example.icecream.database.AppDataBase;
import com.example.icecream.database.dao.RssFeedDao;
import com.example.icecream.database.entity.RssFeed;

/**
 * The repository to handle RSS feeds in database
 */
public class RssFeedRepository {

  private RssFeedDao rssFeedDao;
  private LiveData<List<RssFeed>> allFeeds;

  /**
   * Constructor<br/>
   * register the application to repository
   *
   * @param application input application
   */
  public RssFeedRepository(Application application) {
    AppDataBase db = AppDataBase.getDatabase(application);
    rssFeedDao = db.rssFeedDao();
    allFeeds = rssFeedDao.getAllFeeds();
  }

  /**
   * Getter for feed list
   *
   * @return all the RSS feeds
   */
  public LiveData<List<RssFeed>> getAllFeeds() {
    return allFeeds;
  }

  /**
   * insert RSS feed to database asynchronously
   *
   * @param rssFeed input RSS feed
   */
  public void insert(RssFeed rssFeed) {
    new insertAsyncTask(rssFeedDao).execute(rssFeed);
  }

  private static class insertAsyncTask extends AsyncTask<RssFeed, Void, Void> {
    private RssFeedDao rssFeedDao;

    insertAsyncTask(RssFeedDao dao) {
      rssFeedDao = dao;
    }

    @Override
    protected Void doInBackground(final RssFeed... params) {
      rssFeedDao.insert(params[0]);
      return null;
    }
  }
}
