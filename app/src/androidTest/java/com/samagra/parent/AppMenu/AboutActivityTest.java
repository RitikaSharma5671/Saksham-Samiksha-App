package com.samagra.parent.AppMenu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.samagra.commons.Constants;
import com.samagra.parent.R;
import com.samagra.parent.ui.HomeScreen.HomeActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.samagra.parent.EspressoTools.waitFor;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AboutActivityTest {
    Intent intent;
    SharedPreferences.Editor preferencesEditor;
    @Rule
    public ActivityTestRule<HomeActivity> mActivityTestRule = new ActivityTestRule<>(HomeActivity.class, true, false);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Before
    public void initializeMockData() throws IOException {
        Context targetContext = getInstrumentation().getTargetContext();
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(targetContext).edit();
    }

    @Test
    public void changeLanguageTest() throws InterruptedException, IOException {
        preferencesEditor.putString("user.username", "shc_core2");
        preferencesEditor.putString("user.fullName", "shc_core2");
        preferencesEditor.putString(Constants.APP_LANGUAGE_KEY, "en");
        preferencesEditor.commit();

        mActivityTestRule.launchActivity(new Intent());
        onView(isRoot()).perform(waitFor(10000));
        onView(isRoot()).perform(waitFor(10000));

        onView(allOf(withId(R.id.submit_forms))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.view_submitted_forms))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.fill_forms))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.need_help))).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.submitted_forms_text)).check(matches(isDisplayed())).check(ViewAssertions.matches (withText ("Inspect School")));
        onView(ViewMatchers.withId(R.id.my_visits_label)).check(matches(isDisplayed())).check(ViewAssertions.matches (withText ("My Visits")));
        onView(ViewMatchers.withId(R.id.submit_form_label)).check(matches(isDisplayed())).check(ViewAssertions.matches (withText ("Submit Form")));
        onView(ViewMatchers.withId(R.id.need_help_label)).check(matches(isDisplayed())).check(ViewAssertions.matches (withText ("Need Help?")));
        ViewInteraction appCompatImageButton2 =
                onView(
                        allOf(withContentDescription("Hello"),
                                childAtPosition(
                                        allOf(withId(R.id.toolbar),
                                                childAtPosition(
                                                        withId(R.id.parent),
                                                        0)),
                                        0),
                                isDisplayed()));
        appCompatImageButton2.perform(click());
        ViewInteraction textView16 = onView(
                allOf(withId(android.R.id.title), withText("About Us"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView16.check(matches(withText("About Us")));

        ViewInteraction textView18 = onView(
                allOf(withId(android.R.id.title), withText("Profile"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView18.check(matches(withText("Profile")));

        ViewInteraction textView20 = onView(
                allOf(withId(android.R.id.title), withText("Logout"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        textView20.check(matches(withText("Logout")));

//
        ViewInteraction appCompatTextView1 = onView(
                allOf(withId(android.R.id.title), withText("About Us"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView1.perform(click());
        onView(ViewMatchers.withId(R.id.imageView)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.summary)).check(matches(isDisplayed())).check(ViewAssertions.matches (withText ("Saksham Samiksha application is a product of the Department of School Education, Haryana Government.  The application will be used to record observations of academic and non-academic nature from school visits across the state.")));
        onView(ViewMatchers.withId(R.id.title)).check(matches(isDisplayed())).check(ViewAssertions.matches (withText ("Saksham Samiksha")));

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
