package com.example.icecream.utils;

import android.app.Application;
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
    Application application = mActivityRule.getActivity().getApplication();
    String url = ResourceHandler.getSpeechFileUrl(704L, application);
    assertEquals(application.getFilesDir() + "/speech/704.mp3", url);
  }

  @Test
  public void updateAllRssFeeds() {
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.updateAllRssFeeds();
  }

  @Test
  public void updateCommonArticles() {
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.updateCommonArticles();
  }

  @Test
  public void updatePersonalResources() {
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.updatePersonalResources("18929357397");
  }

  @Test
  public void subscribe() {
    String phone = "18929357397";
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.subscribe(phone, "https://www.zhihu.com/rss");
  }

  @Test
  public void unsubscribe() {
    String phone = "18929357397";
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.unsubscribe(phone, "https://www.zhihu.com/rss");
  }

  @Test
  public void loadRssFeeds() {
    String phone = "18929357397";
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.loadRssFeeds(phone);
  }

  @Test
  public void loadArticles() {
    String phone = "18929357397";
    Application application = mActivityRule.getActivity().getApplication();
    HttpHandler httpHandler = HttpHandler.getInstance(application);
    AppViewModel viewModel = new AppViewModel(application);
    ResourceHandler resourceHandler = ResourceHandler.getInstance(httpHandler, viewModel);
    resourceHandler.loadArticles(phone);
  }
}