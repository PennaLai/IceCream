package com.example.icecream;


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest2 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest2() {
        ViewInteraction materialEditText = onView(
                allOf(withId(R.id.phone),
                        childAtPosition(
                                allOf(withId(R.id.head),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialEditText.perform(click());

        ViewInteraction materialEditText2 = onView(
                allOf(withId(R.id.phone),
                        childAtPosition(
                                allOf(withId(R.id.head),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialEditText2.perform(click());

        ViewInteraction materialEditText3 = onView(
                allOf(withId(R.id.phone),
                        childAtPosition(
                                allOf(withId(R.id.head),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));
        materialEditText3.perform(replaceText("17603074710"), closeSoftKeyboard());

        pressBack();

        ViewInteraction materialEditText4 = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.head),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        materialEditText4.perform(replaceText("102119"), closeSoftKeyboard());

        ViewInteraction materialEditText5 = onView(
                allOf(withId(R.id.password), withText("102119"),
                        childAtPosition(
                                allOf(withId(R.id.head),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        materialEditText5.perform(pressImeActionButton());

        pressBack();

        ViewInteraction fancyButton = onView(
                allOf(withId(R.id.t), withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.head),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        fancyButton.perform(click());
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
