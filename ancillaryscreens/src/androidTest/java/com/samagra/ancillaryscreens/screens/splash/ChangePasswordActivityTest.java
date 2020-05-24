package com.samagra.ancillaryscreens.screens.splash;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.screens.login.LoginActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ChangePasswordActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void changePasswordActivityTest() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.forgot_password), withText("Forgot Password?"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0),
                                4),
                        isDisplayed()));
        textView.check(matches(withText("Forgot Password?")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.forgot_password), withText("Forgot Password?"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0),
                                4),
                        isDisplayed()));
        textView2.check(matches(withText("Forgot Password?")));

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.forgot_password), withText("Forgot Password?"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                4),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withText("Reset App Password"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.rootView),
                                                0)),
                                1),
                        isDisplayed()));
        textView3.check(matches(withText("Reset App Password")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.app_logo),
                        childAtPosition(
                                allOf(withId(R.id.rootView),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                2),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.reset_password_label), withText("Enter the registered Mobile number associated with your account."),
                        childAtPosition(
                                allOf(withId(R.id.rootView),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                3),
                        isDisplayed()));
        textView4.check(matches(withText("Enter the registered Mobile number associated with your account.")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.user_phone), withText("Phone Number"),
                        childAtPosition(
                                allOf(withId(R.id.rootView),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                4),
                        isDisplayed()));
        editText.check(matches(withText("Phone Number")));

        ViewInteraction button = onView(
                allOf(withId(R.id.phone_submit),
                        childAtPosition(
                                allOf(withId(R.id.rootView),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                5),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.phone_submit),
                        childAtPosition(
                                allOf(withId(R.id.rootView),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                5),
                        isDisplayed()));
        button2.check(matches(isDisplayed()));

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.phone_submit), withText("Send OTP"),
                        childAtPosition(
                                allOf(withId(R.id.rootView),
                                        childAtPosition(
                                                withId(R.id.fragment_container),
                                                0)),
                                6),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.snackbar_text), withText("Invalid Phone Number"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView5.check(matches(withText("Invalid Phone Number")));
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
