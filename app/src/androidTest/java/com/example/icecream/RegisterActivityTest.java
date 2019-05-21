package com.example.icecream;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.icecream.ui.activity.RegisterActivity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterActivityTest {
  @Rule
  public ActivityTestRule<RegisterActivity> mActivityRule = new ActivityTestRule<>(
      RegisterActivity.class);

  @Before
  public void setUp() {
    onView(withId(R.id.signup)).perform(click());
  }

  @Test
  public void signUpEmptyPhoneNumber() {
    onView(withId(R.id.usernameRegister)).perform(typeText("赖鹏楠"), closeSoftKeyboard());
    onView(withId(R.id.passwordRegister)).perform(typeText("hjkdashdjkashdjk"), closeSoftKeyboard());
    onView(withId(R.id.bt_signUp)).perform(click());
    onView(withText("手机号不能为空"))
        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
        .perform(click());
  }


  @Test
  public void signUpInvalidPhoneNumber() {
    onView(withId(R.id.usernameRegister)).perform(typeText("赖鹏楠"), closeSoftKeyboard());
    onView(withId(R.id.passwordRegister)).perform(typeText("hjkdashdjkashdjk"), closeSoftKeyboard());
    onView(withId(R.id.phoneNumber)).perform(typeText("123sadhjk"), closeSoftKeyboard());
    onView(withId(R.id.bt_signUp)).perform(click());
    onView(withText("手机号不合法"))
        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
        .perform(click());
  }

  @Test
  public void signUpInvalidDupNumber() {
    onView(withId(R.id.usernameRegister)).perform(typeText("赖鹏楠"), closeSoftKeyboard());
    onView(withId(R.id.passwordRegister)).perform(typeText("hjkdashdjkashdjk"), closeSoftKeyboard());
    onView(withId(R.id.phoneNumber)).perform(typeText("15602432271"), closeSoftKeyboard());
    onView(withId(R.id.bt_signUp)).perform(click());
    onView(withText("这个手机号已经被注册过了"))
        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
        .perform(click());
  }

  @Test
  public void signUpUserNameTooLong() {
    onView(withId(R.id.usernameRegister)).perform(typeText("asdjhkasdkjashdjkashdjkashjdkashjkdhasjkdhashdjkashkjdhasdas"), closeSoftKeyboard());
    onView(withId(R.id.passwordRegister)).perform(typeText("hjkdashdjkashdjk"), closeSoftKeyboard());
    onView(withId(R.id.phoneNumber)).perform(typeText("15602432271"), closeSoftKeyboard());
    onView(withId(R.id.bt_signUp)).perform(click());
    onView(withText("用户名过长"))
        .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
        .perform(click());
  }

}