package com.example.icecream.utils;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.icecream.ui.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class HttpHandlerTest {
  @Rule
  public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
      MainActivity.class);

  /**
   * check login and assert an error "no such user".
   */
  @Test
  public void postLoginStateNoSuchUserTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("15602432290", "dhkjahdjka");
    assertEquals(HttpHandler.ResponseState.NoSuchUser, responseState);
  }

  /**
   * check login valid.
   */
  @Test
  public void postLoginStateValidTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("18929357397", "123456");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  /**
   * check login and assert an error "Wrong password".
   */
  @Test
  public void postLoginStateWrongPwdTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.postLoginState("18929357397", "1231232");
    assertEquals(HttpHandler.ResponseState.WrongPassword, responseState);
  }

  /**
   * check the phone register valid, no duplicate.
   */
  @Test
  public void postPhoneStateValidTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.postPhoneState("12346546627");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }


  /**
   * check the phone has been registered.
   */
  @Test
  public void postPhoneStateDuplicateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.postPhoneState("18929357397");
    assertEquals(HttpHandler.ResponseState.DuplicatePhoneNumber, responseState);
  }

  @Test
  public void getInstanceTest() {
    HttpHandler httpHandler1 = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler httpHandler2 = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    assertEquals("singleton", httpHandler1, httpHandler2);
  }

  @Test
  public void getCheckTokenCheckTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.getCheckToken("18929357397");
    assertEquals("token", HttpHandler.ResponseState.Valid, responseState);
  }

  @Test
  public void getUpdateAllFeedsStateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.getUpdateAllFeedsState();
    assertEquals("update all feeds", HttpHandler.ResponseState.Valid, responseState);
  }


  @Test
  public void getAllRssFeedsTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    httpHandler.getUpdateAllFeedsState();
    assertTrue("all feeds", httpHandler.getAllRssFeeds().size() > 0);
  }

  @Test
  public void getUpdateCommonArticlesStateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.getUpdateCommonArticlesState();
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  @Test
  public void getCommonArticlesTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    httpHandler.getUpdateCommonArticlesState();
    assertTrue("new articles", httpHandler.getCommonArticles().size() > 0);
  }

  @Test
  public void getUpdateRssFeedsStateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.getUpdateRssFeedsState("18929357397");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  @Test
  public void getPersonalRssFeeds() {

  }

  @Test
  public void getUpdateArticlesStateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState = httpHandler.getUpdateArticlesState("18929357397");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  @Test
  public void getPersonalArticles() {

  }

  @Test
  public void getSubscribeFeedStateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    HttpHandler.ResponseState responseState =
        httpHandler.getSubscribeFeedState("18929357397", "https://www.zhihu.com/rss");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  @Test
  public void getUnsubscribeFeedStateTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    httpHandler.getSubscribeFeedState("18929357397", "https://www.zhihu.com/rss");
    HttpHandler.ResponseState responseState =
        httpHandler.getUnsubscribeFeedState("18929357397", "https://www.zhihu.com/rss");
    assertEquals(HttpHandler.ResponseState.Valid, responseState);
  }

  @Test
  public void getUpdateSpeechTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    httpHandler.getUpdateSpeech(1134L);
  }

  @Test
  public void getUpdateSpeechInfoTest() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    assertNotNull(httpHandler.getUpdateSpeechInfo(1134L));
  }
}