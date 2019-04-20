package com.example.icecream;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {
  @Rule
  public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
      LoginActivity.class);

  @Test
  public void loginEmptyPhoneNumber() {
    onView(withId(R.id.phone)).perform(typeText(""), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(typeText("wdhkjasdhjk"), closeSoftKeyboard());
    onView(withId(R.id.bt_login)).perform(click());
    onView(withText("手机号不能为空"))
        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
        .perform(click());
  }

  @Test
  public void loginPasswordWrong() {
    onView(withId(R.id.phone)).perform(typeText("15602432271"), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(typeText("sadhkadhj"), closeSoftKeyboard());
    onView(withId(R.id.bt_login)).perform(click());
    try {
      Thread.sleep(1000);
      onView(withText("密码错误"))
          .inRoot(withDecorView(is(mActivityRule.getActivity().getWindow().getDecorView())))
          .check(matches(isDisplayed()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @Test
  public void loginNoAccount() {
    onView(withId(R.id.phone)).perform(typeText("13811111111"), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(typeText("sadhkadhj"), closeSoftKeyboard());
    onView(withId(R.id.bt_login)).perform(click());
    try {
      Thread.sleep(1000);
      onView(withText("用户账号不存在"))
          .inRoot(withDecorView(is(mActivityRule.getActivity().getWindow().getDecorView())))
          .check(matches(isDisplayed()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void loginSuccess() {
    onView(withId(R.id.phone)).perform(typeText("15602432271"), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(typeText("szjjsjdjdd"), closeSoftKeyboard());
    onView(withId(R.id.bt_login)).perform(click());
    try {
      Thread.sleep(1000);
      onView(withText("登录成功"))
          .inRoot(withDecorView(is(mActivityRule.getActivity().getWindow().getDecorView())))
          .check(matches(isDisplayed()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void loginInvalidPhoneNumber() {
    onView(withId(R.id.phone)).perform(typeText("15602432xw"), closeSoftKeyboard());
    onView(withId(R.id.password)).perform(typeText("szjjsjdjdd"), closeSoftKeyboard());
    onView(withId(R.id.bt_login)).perform(click());
    onView(withText("手机号格式不正确"))
        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
        .perform(click());
  }


}