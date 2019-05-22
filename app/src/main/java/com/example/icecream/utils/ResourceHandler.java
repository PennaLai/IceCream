package com.example.icecream.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.example.icecream.database.entity.RssFeed;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ResourceHandler {

  private AppViewModel viewModel;

  private HttpHandler httpHandler;

  public ResourceHandler(HttpHandler httpHandler, AppViewModel viewModel) {
    this.viewModel = viewModel;
    this.httpHandler = httpHandler;
  }

  public static void getAllRssFeeds() {
    new ResourceHandler.UpdateAllFeedsAsyncTask().execute();
  }

  public static void getPersonalRssFeeds(String phoneNumber) {
    new ResourceHandler.UpdatePersonalFeedsAsyncTask().execute(phoneNumber);
  }

  public static void getPersonalArticles(String phoneNumber) {
    new ResourceHandler.UpdateArticlesAsyncTask().execute(phoneNumber);
  }

  public static void subscribe(String phoneNumber, String url) {
    new ResourceHandler.SubscribeAsyncTask().execute(phoneNumber, url);
  }

  public static void unsubscribe(String phoneNumber, String url) {
    new ResourceHandler.UnsubscribeAsyncTask().execute(phoneNumber, url);
  }


  private static class UpdateAllFeedsAsyncTask extends AsyncTask<Void, Void, HttpHandler.ResponseState> {
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

    @Override
    protected HttpHandler.ResponseState doInBackground(String... params) {
      return httpHandler.getUpdateRSSFeedsState(params[0]);
    }

    @Override
    protected void onPostExecute(HttpHandler.ResponseState responseState) {
      switch (responseState) {
        case Valid:
          // get those feeds successfully
          Log.i(TAG, "get rss feeds");
          viewModel.setPersonalRssFeeds(httpHandler.getPersonalRssFeeds());
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
}
