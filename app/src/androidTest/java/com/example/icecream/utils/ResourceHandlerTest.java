package com.example.icecream.utils;

import android.support.test.rule.ActivityTestRule;

import com.example.icecream.ui.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class ResourceHandlerTest {
  @Rule
  public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void getInstance() {
    HttpHandler httpHandler = HttpHandler.getInstance(mActivityRule.getActivity().getApplication());
    AppViewModel appViewModel = new AppViewModel(mActivityRule.getActivity().getApplication());
    ResourceHandler resourceHandler1 = ResourceHandler.getInstance(httpHandler, appViewModel);
    ResourceHandler resourceHandler2 = ResourceHandler.getInstance(httpHandler, appViewModel);
    assertEquals(resourceHandler1, resourceHandler2);
  }

  @Test
  public void getSpeechFileUrl() {
    ResourceHandler.getSpeechFileUrl(704L, mActivityRule.getActivity().getApplication());

  }

  @Test
  public void updateAllRssFeeds() {
  }

  @Test
  public void updateCommonArticles() {
  }

  @Test
  public void downloadSpeech() {
  }

  @Test
  public void updatePersonalResources() {
  }

  @Test
  public void subscribe() {
  }

  @Test
  public void unsubscribe() {
  }

  @Test
  public void loadRssFeeds() {
  }

  @Test
  public void loadArticles() {
  }
}