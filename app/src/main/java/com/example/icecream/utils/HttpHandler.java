package com.example.icecream.utils;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.icecream.database.Repository;
import com.example.icecream.database.entity.Article;
import com.example.icecream.database.entity.RssFeed;
import com.example.icecream.database.entity.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A util class to handle the http request and response.<br/>
 *
 * @author Kemo
 * @author Hanmei
 * @version V1.0
 */
public class HttpHandler {

  private static volatile HttpHandler instance;
  private static final String TAG = HttpHandler.class.getName();
  private final OkHttpClient okHttpClient = new OkHttpClient();

  private static final String PROTOCOL = "http";
  private static final String HOST = "39.108.73.166";
  //  private static final String HOST = "10.0.2.2";
  private static final String PORT = "8080";
  private static final String MAIN_URL = PROTOCOL + "://" + HOST + ":" + PORT + "/";

  /**
   * User login and register urls.
   */
  private static final String LOGIN_URL = MAIN_URL + "signin";
  private static final String REGISTER_URL = MAIN_URL + "signup";
  private static final String BEFORE_REGISTER = MAIN_URL + "before-register";
  private static final String USER_INFO_URL = MAIN_URL + "userinfo";

  /**
   * User feeds and articles urls.
   */
  private static final String ALL_RSS_FEEDS_URL = MAIN_URL + "list/all/feeds";
  private static final String PERSONAL_RSS_FEEDS_URL = MAIN_URL + "list/feeds";
  private static final String ARTICLES_URL = MAIN_URL + "list/feed/all/articles";
  private static final String SUBSCRIBE_URL = MAIN_URL + "addChannel";
  private static final String UNSUBSCRIBE_URL = MAIN_URL + "deleteChannel";

  private static final String MESSAGE = "message";
  private static final String MESSAGE_CODE = "msgCode";
  private static final String MESSAGE_DATA = "data";

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");

  /**
   * We mainly use synchronous method of repository in http handler.
   */
  private Repository repository;

  private List<RssFeed> rssFeeds = new ArrayList<>();
  private List<Article> articles = new ArrayList<>();
  private List<RssFeed> allRssFeeds = new ArrayList<>();

  /**
   * Constructor for http handler.
   *
   * @param application This app.
   */
  private HttpHandler(final Application application) {
    repository = Repository.getInstance(application);
  }

  public static HttpHandler getInstance(final Application application) {
    if (instance == null) {
      synchronized (HttpHandler.class) {
        if (instance == null) {
          instance = new HttpHandler(application);
        }
      }
    }
    return instance;
  }

  /**
   * States of validation results from server.
   * <li>{@link #DuplicatePhoneNumber}</li>
   * <li>{@link #NoSuchUser}</li>
   * <li>{@link #WrongPassword}</li>
   * <li>{@link #Valid}</li>
   *
   * @author kemo
   */
  public enum ResponseState {
    /**
     * Find duplicate phone number in database.
     */
    DuplicatePhoneNumber,
    /**
     * Cannot find the user in database.
     */
    NoSuchUser,
    /**
     * Password cannot match in database.
     */
    WrongPassword,
    /**
     * Token is invalid, may be empty or expired, needs to login again.
     */
    InvalidToken,
    /**
     * Cannot subscribe, maybe have already subscribed or the url is wrong.
     * Needs to refresh user information.
     */
    SubscribeFail,
    /**
     * Cannot unsubscribe, maybe haven't subscribed or the url is wrong.
     */
    UnsubscribeFail,
    /**
     * User account is matched in database.
     */
    Valid,
    /**
     * Server side goes wrong.
     */
    ServerWrong
  }

  private String getUpdateUsername(@NonNull String phoneNumber) {
    String url = USER_INFO_URL + "?phone=" + phoneNumber;
    String responseString = getHttpResponseString(url);
    if (responseString != null) {
      Log.i(TAG, responseString);
    } else {
      return null;
    }
    JSONObject responseJsonObject;
    String response = "";
    try {
      responseJsonObject = new JSONObject(responseString);
      if (responseJsonObject.getString(MESSAGE_CODE).equals("2")) {
        // user found.
        response = responseJsonObject.getString(MESSAGE);
      }
    } catch (Exception e) {
      Log.e(TAG, "getUpdateUsername: ", e);
    }
    return response;
  }


  /**
   * Get request and get the content of a get http response.
   *
   * @param url The request url.
   * @return The http response from server.
   */
  private String getHttpResponseString(final String url) {
    Request request = new Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .build();
    String result = null;
    try {
      Response response = okHttpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        assert response.body() != null;
        result = response.body().string();
      }
    } catch (IOException e) {
      Log.e(TAG, "getHttpResponseString: ", e);
    }
    return result;
  }

  /**
   * Post request and get the content of a post http response.
   *
   * @param url  The post request url.
   * @param json The post data content.
   * @return The response from the server.
   */
  private String postHttpResponseString(final String url, final String json) {
    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    String result = null;
    try {
      Response response = okHttpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        assert response.body() != null;
        result = response.body().string();
      }
    } catch (IOException e) {
      Log.e(TAG, "postHttpResponseString: ", e);
    }
    return result;
  }

  /**
   * User Login <br/>
   * Send post request and get the login response state from the server.
   *
   * @param phoneNumber The phone number string that used for login.
   * @param password    The password string that used for login.
   * @return The validation result state.
   * @author Kemo
   */
  public ResponseState postLoginState(@NonNull final String phoneNumber,
                                      @NonNull final String password) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("phoneNumber", phoneNumber);
      jsonObject.put("password", password);
    } catch (Exception e) {
      Log.e(TAG, "postLoginState: ", e);
    }
    String responseString = postHttpResponseString(LOGIN_URL, jsonObject.toString());
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        switch (responseJsonObject.getString(MESSAGE_CODE)) {
          case "0":
            responseState = ResponseState.NoSuchUser;
            break;
          case "1":
            responseState = ResponseState.WrongPassword;
            break;
          case "2":
            responseState = ResponseState.Valid;
            String token = responseJsonObject.getString("token");
            // check if it is in local database
            User user = repository.getUserByPhoneSync(phoneNumber);
            if (user == null) {
              String username = getUpdateUsername(phoneNumber);
              if (username == null) {
                username = "";
              }
              repository.insertUserSync(new User(phoneNumber, username, password, token));
            } else {

              repository.updateTokenSync(user, token);
            }
            break;
          default:
            break;
        }
      } catch (Exception e) {
        Log.e(TAG, "postLoginState: ", e);
      }
    }
    return responseState;
  }

  /**
   * User Register <br/>
   * Send post request and get the register response state from the server.
   *
   * @param phoneNumber The phone number string that used for register.
   * @param username    The username string that used for register.
   * @param password    The password string that used for register.
   * @return The validation result state.
   */
  public ResponseState postRegisterState(@NonNull final String phoneNumber,
                                         @NonNull final String username,
                                         @NonNull final String password) {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("phoneNumber", phoneNumber);
      jsonObject.put("password", password);
      jsonObject.put("username", username);
    } catch (Exception e) {
      Log.e(TAG, "postRegisterState: ", e);
    }
    String responseString = postHttpResponseString(REGISTER_URL, jsonObject.toString());
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        if (responseJsonObject.getString(MESSAGE_CODE).equals("0")) {
          responseState = ResponseState.Valid;
          // add user to database
          User user = new User(phoneNumber, username, password, "");
          repository.insertUser(user);
        } else {
          responseState = ResponseState.DuplicatePhoneNumber;
        }
      } catch (Exception e) {
        Log.e(TAG, "postRegisterState: ", e);
      }
    }
    return responseState;
  }

  /**
   * Send post request to check if the phone has been registered. <br/>
   * This method should be called before register.
   *
   * @param phoneNumber The phone number string that used for register.
   * @return The validation result state.
   */
  public ResponseState postPhoneState(@NonNull final String phoneNumber) {
    JSONObject requestJsonObject = new JSONObject();
    try {
      requestJsonObject.put("phoneNumber", phoneNumber);
    } catch (Exception e) {
      Log.e(TAG, "postPhoneState: ", e);
    }
    String responseString = postHttpResponseString(BEFORE_REGISTER, requestJsonObject.toString());
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        switch (responseJsonObject.getString(MESSAGE_CODE)) {
          case "1":
            responseState = ResponseState.DuplicatePhoneNumber;
            break;
          case "2":
            responseState = ResponseState.Valid;
            break;
          default:
            break;
        }
      } catch (Exception e) {
        Log.e(TAG, "postPhoneState: ", e);
      }
    }
    return responseState;
  }

  /**
   * Gets all RSS feeds in remote database.
   *
   * @return response state.
   */
  public ResponseState getUpdateAllFeedsState() {
    String responseString = getHttpResponseString(ALL_RSS_FEEDS_URL);
    ResponseState responseState;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      responseState = ResponseState.Valid;
      Log.i(TAG, responseString);
      try {
        JSONArray jsonArray = new JSONArray(responseString);
        allRssFeeds.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonobject = jsonArray.getJSONObject(i);
          RssFeed rssFeed = new RssFeed(
              jsonobject.getString("url"),
              jsonobject.getString("channelName"),
              jsonobject.getString("category"));
          allRssFeeds.add(rssFeed);
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    return responseState;
  }

  /**
   * Gets all RSS feeds result.
   *
   * @return all RSS feeds.
   */
  public List<RssFeed> getAllRssFeeds() {
    return allRssFeeds;
  }

  /**
   * Send get request to refresh personal RSS feeds for local database.
   *
   * @return The response state of token validation.
   * If InvalidToken, needs to re-login.
   * If NoSuchUser, needs to re-login.
   */
  public ResponseState getUpdateRSSFeedsState(@NonNull final String phoneNumber) {
    User user = repository.getUserByPhoneSync(phoneNumber);
    String token = user.getAuthToken();
    String url = PERSONAL_RSS_FEEDS_URL + "?token=" + token;
    String responseString = getHttpResponseString(url);
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        switch (responseJsonObject.getString(MESSAGE_CODE)) {
          case "0":
            // token is invalid. Needs to re-login.
            responseState = ResponseState.InvalidToken;
            break;
          case "1":
            // user account may have been deleted. Needs to re-login.
            responseState = ResponseState.NoSuchUser;
            break;
          case "2":
            // token is valid and stores data to local database.
            responseState = ResponseState.Valid;
            Log.i(TAG, "Successfully get RSS feeds\n");
            String data = responseJsonObject.getString(MESSAGE_DATA);
            if (data != null) {
              JSONArray jsonArray = new JSONArray(data);
              rssFeeds.clear();
              for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                RssFeed rssFeed = new RssFeed(
                    jsonobject.getString("url"),
                    jsonobject.getString("channelName"),
                    jsonobject.getString("category"));
                rssFeeds.add(rssFeed);
              }
            }
            break;
          default:
            break;
        }
      } catch (Exception e) {
        Log.e(TAG, "getUpdateRSSFeedsState: ", e);
      }
    }
    return responseState;
  }

  /**
   * Get the result RSS feeds.
   *
   * @return RSS feeds of the user.
   */
  public List<RssFeed> getPersonalRssFeeds() {
    return rssFeeds;
  }

  /**
   * Send get request to refresh personal articles for local database.
   *
   * @return The response state of token validation.
   * If InvalidToken, needs to re-login.
   * If NoSuchUser, needs to re-login.
   */
  public ResponseState getUpdateArticlesState(@NonNull final String phoneNumber) {
    User user = repository.getUserByPhoneSync(phoneNumber);
    String token = user.getAuthToken();
    String url = ARTICLES_URL + "?token=" + token;
    String responseString = getHttpResponseString(url);
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        switch (responseJsonObject.getString(MESSAGE_CODE)) {
          case "0":
            // token is invalid. Needs to re-login.
            responseState = ResponseState.InvalidToken;
            break;
          case "1":
            // user account may have been deleted. Needs to re-login.
            responseState = ResponseState.NoSuchUser;
            break;
          case "2":
            // token is valid and stores data to local database.
            responseState = ResponseState.Valid;
            Log.i(TAG, "Successfully get articles");
            String data = responseJsonObject.getString(MESSAGE_DATA);
            if (data != null) {
              JSONArray jsonArray = new JSONArray(data);
              articles.clear();
              for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonobject = jsonArray.getJSONObject(i);
                JSONObject rssFeed = new JSONObject(jsonobject.getString("rssFeedEntity"));
                Article article = new Article(
                    jsonobject.getLong("id"),
                    rssFeed.getString("url"),
                    jsonobject.getString("title"),
                    jsonobject.getString("link"),
                    jsonobject.getString("description"),
                    jsonobject.getString("publishedTime")
                );
                articles.add(article);
              }
            }
            break;
          default:
            break;
        }
      } catch (Exception e) {
        Log.e(TAG, "getUpdateRSSFeedsState: ", e);
      }
    }
    return responseState;
  }

  /**
   * Get the result articles.
   *
   * @return 30 newest articles subscribed by the user.
   */
  public List<Article> getPersonalArticles() {
    return articles;
  }

  /**
   * Send get request to subscribe RSS feed.
   *
   * @return The response state of token validation.
   * If InvalidToken, needs to re-login.
   * If NoSuchUser, needs to re-login.
   */
  public ResponseState getSubscribeFeedState(@NonNull final String phoneNumber, String rssFeedUrl) {
    User user = repository.getUserByPhoneSync(phoneNumber);
    String token = user.getAuthToken();
    String url = SUBSCRIBE_URL + "?token=" + token + "&url=" + rssFeedUrl;
    Log.i(TAG, "Subscribe request: " + url);
    String responseString = getHttpResponseString(url);
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        switch (responseJsonObject.getString(MESSAGE_CODE)) {
          case "0":
            // token is invalid. Needs to re-login.
            responseState = ResponseState.InvalidToken;
            break;
          case "1":
            // user account may have been deleted. Needs to re-login.
            responseState = ResponseState.NoSuchUser;
            break;
          case "2":
            // cannot subscribe
            responseState = ResponseState.SubscribeFail;
            break;
          case "3":
            // token is valid and refresh local database.
            responseState = ResponseState.Valid;
            Log.i(TAG, "Successfully subscribe");
            break;
          default:
            break;
        }
      } catch (Exception e) {
        Log.e(TAG, "getSubscribeFeedState: ", e);
      }
    }
    return responseState;
  }


  /**
   * Send get request to unsubscribe RSS feed.
   *
   * @return The response state of token validation.
   * If InvalidToken, needs to re-login.
   * If NoSuchUser, needs to re-login.
   */
  public ResponseState getUnsubscribeFeedState(@NonNull final String phoneNumber, String rssFeedUrl) {
    User user = repository.getUserByPhoneSync(phoneNumber);
    String token = user.getAuthToken();
    String url = UNSUBSCRIBE_URL + "?token=" + token + "&url=" + rssFeedUrl;
    String responseString = getHttpResponseString(url);
    ResponseState responseState = null;
    if (responseString == null) {
      responseState = ResponseState.ServerWrong;
    } else {
      Log.i(TAG, responseString);
      JSONObject responseJsonObject;
      try {
        responseJsonObject = new JSONObject(responseString);
        switch (responseJsonObject.getString(MESSAGE_CODE)) {
          case "0":
            // token is invalid. Needs to re-login.
            responseState = ResponseState.InvalidToken;
            break;
          case "1":
            // user account may have been deleted. Needs to re-login.
            responseState = ResponseState.NoSuchUser;
            break;
          case "2":
            // cannot unsubscribe
            responseState = ResponseState.UnsubscribeFail;
            break;
          case "3":
            // token is valid and refresh local database.
            responseState = ResponseState.Valid;
            Log.i(TAG, "Successfully unsubscribe");
            break;
          default:
            break;
        }
      } catch (Exception e) {
        Log.e(TAG, "getUnsubscribeFeedState: ", e);
      }
    }
    return responseState;
  }
}
