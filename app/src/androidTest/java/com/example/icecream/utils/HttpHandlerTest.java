package com.example.icecream.utils;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.icecream.LoginActivity;
import com.example.icecream.database.entity.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.OkHttpClient;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HttpHandlerTest {
  @Rule
  public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
      LoginActivity.class);

  @Before
  public void setUp() throws Exception {
  }

  /**
   * check login and assert an error "no such user".
   */
  @Test
  public void postLoginStateNoSuchUserTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client, mActivityRule.getActivity());
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("15602432290", "dhkjahdjka");
    assertEquals(HttpHandler.ResponseState.NoSuchUser, responseState);
  }

  /**
   * check login valid.
   */
  @Test
  public void postLoginStateValidTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client, mActivityRule.getActivity());
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("18929357397", "123456");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  /**
   * check login and assert an error "Wrong password".
   */
  @Test
  public void postLoginStateWrongPwdTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client, mActivityRule.getActivity());
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("18929357397", "1231232");
    assertEquals(HttpHandler.ResponseState.WrongPassword, responseState);
  }

  /**
   * check the phone register valid, no duplicate.
   */
  @Test
  public void postRegisterStateValidTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client, mActivityRule.getActivity());
    HttpHandler.ResponseState responseState = httpHandler.postPhoneState("12346546627");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  /**
   * check the phone has been registered.
   */
  @Test
  public void postPhoneStateDuplicateTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client, mActivityRule.getActivity());
    HttpHandler.ResponseState responseState = httpHandler.postPhoneState("18929357397");
    assertEquals(HttpHandler.ResponseState.DuplicatePhoneNumber, responseState);
  }

  /**
   * check the refresh is invalid.
   */
  @Test
  public void getRefreshStateTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client, mActivityRule.getActivity());
    User user = new User("18929357397", "kemo", "123456");
    HttpHandler.ResponseState responseState = httpHandler.getRefreshState(user);
    assertEquals(HttpHandler.ResponseState.InvalidToken, responseState);
  }
}