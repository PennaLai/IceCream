package com.example.icecream.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.OkHttpClient;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class HttpHandlerTest {

  /**
   * check login and assert an error "Wrong password"
   */
  @Test
  public void getLoginResponseStateWrongPasswordTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client);
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("15602432271", "dhkjahdjka");
    assertEquals(HttpHandler.ResponseState.WrongPassword, responseState);
  }

  /**
   * check login and assert an error "no such user"
   */
  @Test
  public void getLoginResponseStateNoSuchUserTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client);
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("15602432290", "dhkjahdjka");
    assertEquals(HttpHandler.ResponseState.NoSuchUser, responseState);
  }

  /**
   * check login valid
   */
  @Test
  public void getLoginResponseStateValidTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client);
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("15602432271", "123456");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  /**
   * check the phone number register duplicate
   */
  @Test
  public void getRegisterResponseStateDupTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client);
    HttpHandler.ResponseState responseState = httpHandler.postPhoneState("15602432271");
    assertEquals(HttpHandler.ResponseState.DuplicatePhoneNumber, responseState);
  }

  /**
   * check the phone register valid, no duplicate
   */
  @Test
  public void getRegisterResponseStateValidTest() {
    OkHttpClient client = new OkHttpClient();
    HttpHandler httpHandler = new HttpHandler(client);
    HttpHandler.ResponseState responseState = httpHandler.postPhoneState("15602432293");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }
}