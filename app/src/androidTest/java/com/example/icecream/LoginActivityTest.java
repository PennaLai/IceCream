package com.example.icecream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import android.support.test.rule.ActivityTestRule;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest{
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void loginEmptyPhoneNumber(){
        onView(withId(R.id.phone)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("wdhkjasdhjk"), closeSoftKeyboard());
        try {
            Thread.sleep(1000);
            onView(withText("手机号不能为空"))
                    .inRoot(withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                    .perform(click());
        }catch (Exception e){
            e.getStackTrace();
        }

    }


    @Test
    public void loginWrongPassword(){

    }


    @Test
    public void loginDupAccount(){

    }


}