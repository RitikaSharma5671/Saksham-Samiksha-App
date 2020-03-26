package com.samagra.odktest.odktest;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import com.samagra.ancillaryscreens.screens.splash.SplashActivity;
import com.samagra.odktest.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.samagra.odktest.EspressoTools.waitFor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;


@RunWith(AndroidJUnit4ClassRunner.class)

public class LoginActivityTest {
    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Test
    public void LoginActivityTest() {

        onView(isRoot()).perform(waitFor(3000));

        ViewInteraction appCompatEditText_username = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_username),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText_username.perform(replaceText("chaks"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatEditText_password = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText_password.perform(replaceText("abcd1234"));
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatEditText_password1 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_password), withText("abcd1234"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText_password1.perform(pressImeActionButton());

        ViewInteraction appCompatButton_submit = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_submit), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                3),
                        isDisplayed()));
        appCompatButton_submit.perform(click());

        onView(isRoot()).perform(waitFor(2000));

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.view_issues),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        2),
                                2),
                        isDisplayed()));
        linearLayout.perform(click());

        onView(isRoot()).perform(waitFor(2000));

        pressBack();

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200F\u200F\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200E\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200F\u200E\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200ENavigate up\u200E\u200F\u200E\u200E\u200F\u200E"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.parent),
                                                0)),
                                1),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        appCompatImageButton.perform(click());
        onView(isRoot()).perform(waitFor(1000));

      ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.title), withText("Logout"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),

                                        0),
                                0),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        appCompatTextView.perform(click());

        onView(isRoot()).perform(waitFor(2000));


        ViewInteraction appCompatEditText_username2 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_username),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText_username2.perform(replaceText("chaks"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatEditText_password2 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_password),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText_password2.perform(replaceText("abcd1234"));
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatEditText_password3 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_password), withText("abcd1234"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatEditText_password3.perform(pressImeActionButton());


        ViewInteraction appCompatButton_submit1 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.login_submit), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                3),
                        isDisplayed()));
        appCompatButton_submit1.perform(click());

        onView(isRoot()).perform(waitFor(2000));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200F\u200F\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200E\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200F\u200E\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200ENavigate up\u200E\u200F\u200E\u200E\u200F\u200E"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.parent),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton2.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(android.R.id.title), withText("About Us"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        appCompatTextView2.perform(click());

        onView(isRoot()).perform(waitFor(2000));

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        appCompatImageButton3.perform(click());

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withContentDescription("\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200F\u200F\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200E\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200F\u200E\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200ENavigate up\u200E\u200F\u200E\u200E\u200F\u200E"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.parent),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton4.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(android.R.id.title), withText("Tutorial Videos"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        appCompatTextView3.perform(click());

        onView(isRoot()).perform(waitFor(2000));

        ViewInteraction appCompatImageButton5 = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.parent),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatImageButton5.perform(click());

        ViewInteraction appCompatImageButton6 = onView(
                allOf(withContentDescription("\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200F\u200F\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200E\u200E\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200F\u200E\u200F\u200E\u200E\u200E\u200E\u200E\u200F\u200F\u200E\u200F\u200F\u200E\u200E\u200F\u200E\u200F\u200E\u200F\u200F\u200F\u200F\u200F\u200E\u200ENavigate up\u200E\u200F\u200E\u200E\u200F\u200E"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withId(R.id.parent),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton6.perform(click());
        onView(isRoot()).perform(waitFor(1000));


        ViewInteraction appCompatTextView4 = onView(
                allOf(withId(android.R.id.title), withText("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("com.android.internal.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        appCompatTextView4.perform(click());


        onView(isRoot()).perform(waitFor(2000));
        //Click Edit 1st
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab),
                        childAtPosition(
                                allOf(withId(com.samagra.ancillaryscreens.R.id.parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        floatingActionButton.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction textInputEditText_username1 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.text_edit_text),withText("Sanjay Kumar"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.samagra.ancillaryscreens.R.id.edit_text_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText_username1.perform(replaceText("Sanjay Kumar"));

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction textInputEditText_username2 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.text_edit_text), withText("Sanjay Kumar"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.samagra.ancillaryscreens.R.id.edit_text_layout),
                                        0),
                                0),
                        isDisplayed()));
        textInputEditText_username2.perform(closeSoftKeyboard());

        //Click editSave (username)
        ViewInteraction floatingActionButton1 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.fab),
                        childAtPosition(
                                allOf(withId(com.samagra.ancillaryscreens.R.id.parent),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        onView(isRoot()).perform(waitFor(1000));
        floatingActionButton1.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        //snackbar Name Updated(user details updated )







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

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}
