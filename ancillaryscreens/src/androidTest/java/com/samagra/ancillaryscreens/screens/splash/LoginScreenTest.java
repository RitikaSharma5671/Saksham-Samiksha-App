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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void loginScreenTest() throws InterruptedException {
        ViewInteraction textView = onView(
                allOf(withText("Login"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Login")));

        ViewInteraction editText = onView(
                allOf(withId(R.id.login_username), withText("Username"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.userLayout),
                                        0),
                                0),
                        isDisplayed()));
        editText.check(matches(withText("Username")));

        ViewInteraction editText2 = onView(
                allOf(withId(R.id.login_password), withText("Password"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.pwdLayout),
                                        0),
                                0),
                        isDisplayed()));
        editText2.check(matches(withText("Password")));

        ViewInteraction button = onView(
                allOf(withId(R.id.login_submit),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0),
                                3),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.forgot_password), withText("Forgot Password?"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0),
                                4),
                        isDisplayed()));
        textView2.check(matches(withText("Forgot Password?")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.forgot_password), withText("Forgot Password?"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                        0),
                                4),
                        isDisplayed()));
        textView3.check(matches(withText("Forgot Password?")));

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.login_username),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.userLayout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText.perform(replaceText("ffdcdcdc"), closeSoftKeyboard());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.pwdLayout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText2.perform(replaceText("ddfdfdfeded"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.login_submit), withText("SUBMIT"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                3),
                        isDisplayed()));
        appCompatButton2.perform(click());

        Thread.sleep(2000);
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.snackbar_text), withText("Username or Password didn't match. Please try again"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        textView4.check(matches(withText("Username or Password didn't match. Please try again")));


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
