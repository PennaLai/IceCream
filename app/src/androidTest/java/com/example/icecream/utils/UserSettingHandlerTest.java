package com.example.icecream.utils;

import android.app.Application;
import android.support.test.rule.ActivityTestRule;

import com.example.icecream.ui.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserSettingHandlerTest {
  @Rule
  public ActivityTestRule<MainActivity> mActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test
  public void getInstance() {
    Application application = mActivityRule.getActivity().getApplication();
    UserSettingHandler userSettingHandler1 = UserSettingHandler.getInstance(application);
    UserSettingHandler userSettingHandler2 = UserSettingHandler.getInstance(application);
    assertEquals(userSettingHandler1, userSettingHandler2);
  }

  @Test
  public void setLoginPhone() {
    Application application = mActivityRule.getActivity().getApplication();
    UserSettingHandler userSettingHandler = UserSettingHandler.getInstance(application);
    userSettingHandler.setLoginPhone(null);
    assertNull(userSettingHandler.getLoginPhone());
  }
}