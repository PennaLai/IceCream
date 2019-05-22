package com.example.icecream.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * This class is to handle the resource from server and client.
 *
 * @author Kemo
 */
public class ResourceHandler {

  private AppViewModel viewModel;

  private HttpHandler httpHandler;

  /**
   * Constructor.
   *
   * @param httpHandler http handler.
   * @param viewModel   view model.
   */
  public ResourceHandler(HttpHandler httpHandler, AppViewModel viewModel) {
    this.viewModel = viewModel;
    this.httpHandler = httpHandler;
  }

  /**
   * Updates all the available feeds.
   */
  public void updateAllRssFeeds() {
    new UpdateAllFeedsAsyncTask(this).execute();
  }

  /**
   * Updates the newest articles.
   */
  public void updateNewArticles() {

  }

  /**
   * Update personal resources.
   *
   * @param phoneNumber user phone.
   */
  public void updatePersonalFeedsAndArticles(String phoneNumber) {
    updatePersonalRssFeeds(phoneNumber);
    updatePersonalArticles(phoneNumber);
  }

  /**
   * Updates the subscribed feeds.
   *
   * @param phoneNumber user phone.
   */
  private void updatePersonalRssFeeds(String phoneNumber) {
    new UpdatePersonalFeedsAsyncTask(this).execute(phoneNumber);
  }

  /**
   * Updates the subscribes articles.
   *
   * @param phoneNumber user phone.
   */
  private void updatePersonalArticles(String phoneNumber) {
    new UpdateArticlesAsyncTask(this).execute(phoneNumber);
  }

  /**
   * Subscribes the feed.
   *
   * @param phoneNumber user phone.
   * @param url         feed url.
   */
  public void subscribe(String phoneNumber, String url) {
    new SubscribeAsyncTask(this).execute(phoneNumber, url);
  }

  /**
   * Unsubscribes the feed.
   *
   * @param phoneNumber user phone.
   * @param url         feed url.
   */
  public void unsubscribe(String phoneNumber, String url) {
    new UnsubscribeAsyncTask(this).execute(phoneNumber, url);
  }

  public void loadRssFeeds() {

  }

  /**
   * Load the articles.
   *
   * @param phone phone.
   */
  public void loadArticles(String phone) {
    viewModel.loadArticlesByPhone(phone);
  }


  private static class UpdateAllFeedsAsyncTask extends AsyncTask<Void, Void, HttpHandler.ResponseState> {

    private AppViewModel viewModel;

    private HttpHandler httpHandler;

    UpdateAllFeedsAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(Void... params) {
      return httpHandler.getUpdateAllFeedsState();
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      if (responseState == HttpHandler.ResponseState.Valid) {
        // get those feeds successfully
        Log.i(TAG, "inserting all rss feeds");
        List<RssFeed> list = httpHandler.getAllRssFeeds();
        RssFeed[] arr = list.toArray(new RssFeed[0]);
        viewModel.insertAllRssFeeds(arr);
      }
    }
  }

  private static class UpdatePersonalFeedsAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private AppViewModel viewModel;
    private HttpHandler httpHandler;
    private String phone;

    UpdatePersonalFeedsAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      phone = params[0];
      return httpHandler.getUpdateRSSFeedsState(phone);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          // get those feeds successfully
          Log.i(TAG, "get rss feeds");
          viewModel.setPersonalRssFeeds(httpHandler.getPersonalRssFeeds());
          // store
          viewModel.insertPersonalRssFeeds(phone, httpHandler.getPersonalRssFeeds());
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class UpdateArticlesAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private AppViewModel viewModel;

    private HttpHandler httpHandler;

    UpdateArticlesAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      return httpHandler.getUpdateArticlesState(params[0]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          // get those articles successfully
          Log.i(TAG, "get articles");
          viewModel.setPersonalArticles(httpHandler.getPersonalArticles());
          // store
          viewModel.insertArticles(httpHandler.getPersonalArticles());
          Log.i(TAG, "insert articles succeed");
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class SubscribeAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private String phone;
    private String rssFeedUrl;
    private AppViewModel viewModel;
    private HttpHandler httpHandler;

    SubscribeAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      phone = params[0];
      rssFeedUrl = params[1];
      return httpHandler.getSubscribeFeedState(phone, rssFeedUrl);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          viewModel.subscribe(phone, rssFeedUrl);
          Log.i(TAG, "subscribe succeed");
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class UnsubscribeAsyncTask extends AsyncTask<String, Void, HttpHandler.ResponseState> {
    private String phone;
    private String rssFeedUrl;
    private AppViewModel viewModel;
    private HttpHandler httpHandler;

    UnsubscribeAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
      httpHandler = resourceHandler.httpHandler;
    }

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      phone = params[0];
      rssFeedUrl = params[1];
      return httpHandler.getUnsubscribeFeedState(phone, rssFeedUrl);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          viewModel.unsubscribe(phone, rssFeedUrl);
          Log.i(TAG, "unsubscribe succeed");
          break;
        case InvalidToken:
          // TODO back to login
//          activity.login();
          break;
        case NoSuchUser:
          // TODO back to login
//          activity.login();
          break;
        default:
          break;
      }
    }
  }

  private static class LoadArticlesAsyncTask extends AsyncTask<String, Void, Void> {
    private AppViewModel viewModel;

    LoadArticlesAsyncTask(ResourceHandler resourceHandler) {
      viewModel = resourceHandler.viewModel;
    }

    @Override
    protected Void doInBackground(String... params) {


      return null;
    }

    @Override
    protected void onPostExecute(Void result) {


    }
  }

}
