package utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A util class to handle the http request.
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

  private static final String LOGIN_URL = MAIN_URL + "login";
  private static final String REGISTER_URL = MAIN_URL + "register";
  private static final String PHONE_URL = MAIN_URL + "before-register";

  private static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");

  public HttpHandler(final OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  /**
   * Validation result states.
   *
   * @author kemo
   */
  public enum State {
    DuplicatePhoneNumber,
    NoSuchUser,
    WrongPassword,
    Valid
  }

  /**
   * This method is to get the login response state from the server.
   *
   * @param phoneNumber The phone number string that used for login.
   * @param password    The password string that used for login.
   * @return The validation result state.
   */
  public State postLoginResponseState(final String phoneNumber, final String password) {
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
    return parseResponseJson(postHttpResponseString(url, jsonObject.toString()));
  }

  /**
   * This method is to get the register response state from the server.
   *
   * @param phoneNumber The phone number string that used for register.
   * @param username    The username string that used for register.
   * @param password    The password string that used for register.
   * @return The validation result state.
   */
  public State postRegisterResponseState(
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
    return parseResponseJson(postHttpResponseString(url, jsonObject.toString()));
  }

  /**
   * This method is to check if the phone number has been registered.
   *
   * @param phoneNumber The phone number string that used for register.
   * @return The validation result state.
   */
  public State getPhoneResponseState(final String phoneNumber) {
    String url = PHONE_URL + "?phone=" + phoneNumber;
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put("phoneNumber", phoneNumber);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return parseResponseJson(postHttpResponseString(url, jsonObject.toString()));
  }

  /**
   * To get the content of a get http response.
   *
   * @param url The request url.
   * @return The http response.
   */
  private String getHttpResponseString(final String url) {
    Request request = new Request.Builder()
        .url(url)
        .header("Content-Type", "application/json")
        .build();
    try {
      Response response = okHttpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        assert response.body() != null;
        return response.body().string();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * To get the content of a post http response.
   *
   * @param url  The post request url.
   * @param json The post content.
   * @return The response from the server.
   */
  private String postHttpResponseString(final String url, final String json) {
    OkHttpClient httpClient = new OkHttpClient();
    RequestBody body = RequestBody.create(JSON, json);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();
    try {
      Response response = httpClient.newCall(request).execute();
      if (response.isSuccessful()) {
        assert response.body() != null;
        return response.body().string();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * This method is to parse the json format response.
   *
   * @param jsonData The json String needed to parse.
   * @return The validation result state.
   */
  private State parseResponseJson(final String jsonData) {
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject(jsonData);
      switch (jsonObject.getString("state")) {
        case "DuplicatePhoneNumber":
          return State.DuplicatePhoneNumber;
        case "NoSuchUser":
          return State.NoSuchUser;
        case "WrongPassword":
          return State.WrongPassword;
        case "Valid":
          return State.Valid;
        default:
          return null;
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}
