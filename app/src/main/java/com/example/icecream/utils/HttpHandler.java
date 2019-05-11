package com.example.icecream.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A util class to handle the http request and response.<br/>
 *
 * @author Kemo
 * @author Hanmei
 * @version V1.0
 */
public class HttpHandler {

  private final OkHttpClient okHttpClient;
  private static final String PROTOCOL = "http";
  private static final String HOST = "39.108.73.166";
  private static final String PORT = "8080";
  private static final String MAIN_URL = PROTOCOL + "://" + HOST + ":" + PORT + "/";

  /**
   * User login and register urls
   */
  private static final String LOGIN_URL = MAIN_URL + "signin";
  private static final String REGISTER_URL = MAIN_URL + "signup";
  private static final String BEFORE_REGISTER = MAIN_URL + "before-register";

  /**
   * User feeds and articles urls
   */
  private static final String RSS_FEEDS_URL = MAIN_URL + "list/feeds";
  private static final String ARTICLES_URL = MAIN_URL + "list/feed/all/articles";
  private static final String SUBSCRIBE_URL = MAIN_URL + "/addChannel";
  private static final String UNSUBSCRIBE_URL = MAIN_URL + "/deleteChannel";

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");

  public HttpHandler(final OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  /**
   * States of validation results from server
   * <li>{@link #DuplicatePhoneNumber}</li>
   * <li>{@link #NoSuchUser}</li>
   * <li>{@link #WrongPassword}</li>
   * <li>{@link #Valid}</li>
   *
   * @author kemo
   */
  public enum ResponseState {
    /**
     * Find duplicate phone number in com.example.icecream.database
     */
    DuplicatePhoneNumber,
    /**
     * Cannot find the user in com.example.icecream.database
     */
    NoSuchUser,
    /**
     * Password cannot match in com.example.icecream.database
     */
    WrongPassword,
    /**
     * Token is invalid, may be empty or expired, needs to login again
     */
    InvalidToken,
    /**
     * user account is matched in com.example.icecream.database
     */
    Valid
  }

  /**
   * user token
   */
  private String token;

  /**
   * User Login <br/>
   * Send post request and get the login response state from the server.
   *
   * @param phoneNumber The phone number string that used for login.
   * @param password    The password string that used for login.
   * @return The validation result state.
   * @author Kemo
   */
  public ResponseState postLoginState(final String phoneNumber, final String password) {
    String url = LOGIN_URL + String.format(
        "?phone=%s&password=%s",
        phoneNumber,
        password
    );
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("phoneNumber", phoneNumber);
      jsonObject.put("password", password);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    String responseString = postHttpResponseString(url, jsonObject.toString());
    JSONObject responseJsonObject;
    ResponseState responseState = null;
    try {
      responseJsonObject = new JSONObject(responseString);
      switch (responseJsonObject.getString("msgCode")) {
        case "0":
          responseState = ResponseState.NoSuchUser;
        case "1":
          responseState = ResponseState.WrongPassword;
          break;
        case "2":
          responseState = ResponseState.Valid;
          // add token here
          token = responseJsonObject.getString("token");
          break;
        default:
          break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
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
  public ResponseState postRegisterState(
      final String phoneNumber, final String username, final String password) {
    String url = REGISTER_URL + String.format(
        "?phone=%s&username=%s&password=%s",
        phoneNumber,
        username,
        password
    );
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("phoneNumber", phoneNumber);
      jsonObject.put("password", password);
      jsonObject.put("username", username);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    String responseString = postHttpResponseString(url, jsonObject.toString());
    JSONObject responseJsonObject;
    ResponseState responseState = null;
    try {
      responseJsonObject = new JSONObject(responseString);
      if (responseJsonObject.getString("msgCode").equals("0")) {
        responseState = ResponseState.Valid;
      }
    } catch (JSONException e) {
      e.printStackTrace();
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
  public ResponseState postPhoneState(final String phoneNumber) {
    String url = BEFORE_REGISTER + "?phone=" + phoneNumber;
    JSONObject requestJsonObject = new JSONObject();
    try {
      requestJsonObject.put("phoneNumber", phoneNumber);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    String responseString = postHttpResponseString(url, requestJsonObject.toString());
    JSONObject responseJsonObject;
    ResponseState responseState = null;
    try {
      responseJsonObject = new JSONObject(responseString);
      switch (responseJsonObject.getString("msgCode")) {
        case "1":
          responseState = ResponseState.DuplicatePhoneNumber;
          break;
        case "2":
          responseState = ResponseState.Valid;
          break;
        default:
          break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return responseState;
  }

  /**
   * Send get request to refresh personal RSS feeds for local com.example.icecream.database
   *
   * @return The response state of token validation
   */
  public ResponseState getRefreshState() {
    String url = RSS_FEEDS_URL + "?token=" + token;
    String responseString = getHttpResponseString(url);
    JSONObject responseJsonObject;
    ResponseState responseState = null;
    try {
      responseJsonObject = new JSONObject(responseString);
      switch (responseJsonObject.getString("msgCode")) {
        case "0":
          // token is invalid
          responseState = ResponseState.InvalidToken;
          break;
        case "1":
          // user account may have been deleted
          responseState = ResponseState.NoSuchUser;
          break;
        case "2":
          // token valid and store data to local com.example.icecream.database
          responseState = ResponseState.Valid;

          break;
        default:
          break;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return responseState;
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
        System.out.println(result);
      }
    } catch (IOException e) {
      e.printStackTrace();
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
    OkHttpClient httpClient = new OkHttpClient();
    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    String result = null;
    try {
      Response response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        assert response.body() != null;
        result = response.body().string();
        System.out.println(result);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
}
