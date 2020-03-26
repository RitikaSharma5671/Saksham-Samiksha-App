package com.samagra.odktest.odktest;

import android.Manifest;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.samagra.odktest.EspressoTools.waitFor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;


import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.collect.android.activities.FormEntryActivity;

import static android.app.Instrumentation.ActivityResult;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4ClassRunner.class)

public class FillFormTest {

    private SplashActivity mLocatingActivity;

    @Rule
    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.CAMERA",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WRITE_EXTERNAL_STORAGE");

    @Rule public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);


  /*  @Before
    public void setup()
    {
        mLocatingActivity = mActivityTestRule.getActivity();
    }*/


    @Test
    public void FillFormTest() {

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
                allOf(withId(R.id.inspect_school),
                        childAtPosition(
                                allOf(withId(R.id.parent_of_fill_forms),
                                        childAtPosition(
                                                withClassName(is("android.widget.RelativeLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        linearLayout.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.district_spinner),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.samagra.ancillaryscreens.R.id.parent),
                                        3),
                                4),
                        isDisplayed()));
        appCompatSpinner.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(1);
        appCompatCheckedTextView.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.next_button), withText("NEXT"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.samagra.ancillaryscreens.R.id.parent),
                                        3),
                                9),
                        isDisplayed()));
        appCompatButton2.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        DataInteraction linearLayout2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(R.id.llParent),
                                0)))
                .atPosition(0);
        linearLayout2.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(com.samagra.ancillaryscreens.R.id.buttonholder),
                                        childAtPosition(
                                                withId(com.samagra.ancillaryscreens.R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView.perform(click());
        onView(isRoot()).perform(waitFor(1000));
        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(com.samagra.ancillaryscreens.R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(com.samagra.ancillaryscreens.R.id.buttonholder),
                                        childAtPosition(
                                                withId(com.samagra.ancillaryscreens.R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView2.perform(click());
        onView(isRoot()).perform(waitFor(1000));
        ViewInteraction appCompatRadioButton = onView(
                allOf(withText("Very Good"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));

        appCompatRadioButton.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView3.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        onView(first(withText("Very Good")) ).perform(click() );
        onView(isRoot()).perform(ViewActions.swipeUp());
        onView(isRoot()).perform(waitFor(3000));

        ViewInteraction textInputEditText4 = onView(withIndex(withId(R.id.select_container),withText("Good"), 1));
        textInputEditText4.perform(click());
        onView(isRoot()).perform(waitFor(3000));

        ViewInteraction textInputEditText5 = onView(withIndex(withId(R.id.select_container),withText("Good"), 2));
        textInputEditText5.perform(click());
        onView(isRoot()).perform(waitFor(3000));

        ViewInteraction appCompatTextView4 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView4.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton5 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton5.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView5 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView5.perform(click());
        onView(isRoot()).perform(waitFor(1000));


        ViewInteraction scrolledToTopSpinner = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                3),
                        2),
                        isDisplayed()));
        scrolledToTopSpinner.perform(click());

        onView(isRoot()).perform(waitFor(1000));


        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(0);
        appCompatCheckedTextView2.perform(click());
        onView(isRoot()).perform(waitFor(2000));


        ViewInteraction appCompatRadioButton6 = onView(withIndex(withId(R.id.select_container),withText("0: Teacher doesn't know what Learning Outcome is mapped to the topic/subtopic OR they mention the wrong Learning Outcome"), 0));
        appCompatRadioButton6.perform(click());
        onView(isRoot()).perform(waitFor(2000));


        onView(isRoot()).perform(ViewActions.swipeUp());
        onView(isRoot()).perform(waitFor(3000));

        ViewInteraction appCompatRadioButton7 = onView(withIndex(withId(R.id.select_container),withText("Yes"), 0));
        appCompatRadioButton7.perform(click());
        onView(isRoot()).perform(waitFor(2000));


        ViewInteraction appCompatRadioButton8 = onView(withIndex(withId(R.id.select_container),withText("0: Teacher is not using Activity Based Learning when required"), 0));
        appCompatRadioButton8.perform(click());
        onView(isRoot()).perform(waitFor(2000));


        ViewInteraction appCompatRadioButton9 = onView(withIndex(withId(R.id.select_container),withText("Yes"), 1));
        appCompatRadioButton9.perform(click());
        onView(isRoot()).perform(waitFor(2000));



        ViewInteraction appCompatTextView6 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView6.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button.perform(click());

        onView(isRoot()).perform(waitFor(5000));


        ViewInteraction appCompatTextView7 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView7.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton10 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton10.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView8 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView8.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0),
                        2),
                        isDisplayed()));
        editText.perform(replaceText("Ggg"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView9 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView9.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText2 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0),
                        2),
                        isDisplayed()));
        editText2.perform(replaceText("Ghj"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView10 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView10.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton11 = onView(
                allOf(withText("Yes, all the teachers use the learning outcomes (Seekhne ke Pratiphal) charts pasted in the classrooms"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton11.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView11 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView11.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button2 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button2.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView12 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView12.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton12 = onView(
                allOf(withText("None of the material has reached"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton12.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView13 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView13.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton13 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton13.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView14 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView14.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton14 = onView(
                allOf(withText("Yes, teachers are using the kits"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton14.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView15 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView15.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton15 = onView(
                allOf(withText("Yes, teachers are using the books"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton15.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView16 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView16.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton16 = onView(
                allOf(withText("Yes, teachers refer to the question bank to create the assessments"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton16.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView17 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView17.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton17 = onView(
                allOf(withText("Yes, all teachers attend the cluster meetings"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton17.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView18 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView18.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton18 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton18.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView19 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView19.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton19 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton19.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView20 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView20.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button3 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button3.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView21 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView21.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton20 = onView(
                allOf(withText("Yes, all teachers are getting information through groups"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton20.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView22 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView22.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton21 = onView(
                allOf(withText("Yes, all teachers refer to online resources"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton21.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView23 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView23.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1),
                        2),
                        isDisplayed()));
        editText3.perform(replaceText("Hhh"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView24 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView24.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton22 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton22.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView25 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView25.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText4 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1),
                        2),
                        isDisplayed()));
        editText4.perform(replaceText("Ghj"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView26 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView26.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton25 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton25.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView27 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView27.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button4 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button4.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView28 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView28.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton26 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton26.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView29 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView29.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton27 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton27.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView30 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView30.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton28 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton28.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView31 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView31.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton29 = onView(
                allOf(withText("Increase"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton29.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView32 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView32.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton30 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton30.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView33 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView33.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton31 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton31.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView34 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView34.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton32 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton32.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView35 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView35.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton33 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton33.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView36 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView36.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton34 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton34.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView37 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView37.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton35 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton35.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView38 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView38.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText5 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1),
                        2),
                        isDisplayed()));
        editText5.perform(replaceText("Hjk"), closeSoftKeyboard());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView39 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView39.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button5 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button5.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView40 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView40.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton36 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton36.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView41 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView41.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText6 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1),
                        2),
                        isDisplayed()));
        editText6.perform(replaceText("Hj"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView42 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView42.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button6 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button6.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView43 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView43.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton37 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton37.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView44 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView44.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText7 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                1),
                        2),
                        isDisplayed()));
        editText7.perform(replaceText("Ghj"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView45 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView45.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button7 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button7.perform(click());

        onView(isRoot()).perform(waitFor(1000));
        ViewInteraction appCompatTextView46 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView46.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton38 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton38.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView47 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView47.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton39 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton39.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView48 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView48.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button8 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button8.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView49 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView49.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button9 = onView(
                allOf(withId(R.id.capture_image), withText("Take Picture"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.ImageWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button9.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView50 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView50.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton40 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton40.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView51 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView51.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton41 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton41.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView52 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView52.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton42 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton42.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView53 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView53.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatRadioButton43 = onView(
                allOf(withText("Yes"),
                        childAtPosition(
                                allOf(withId(R.id.select_container),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton43.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView54 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView54.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction editText8 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0),
                        2),
                        isDisplayed()));
        editText8.perform(replaceText("Gjk"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatTextView55 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView55.perform(click());
        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction button10 = onView(
                allOf(withId(R.id.get_location), withText("Start GeoPoint"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("org.odk.collect.android.widgets.GeoPointWidget")),
                                        2),
                                0),
                        isDisplayed()));
        button10.perform(click());

        onView(isRoot()).perform(waitFor(10000));

        ViewInteraction appCompatTextView56 = onView(
                allOf(withId(R.id.form_forward_button), withText("Next"), withContentDescription("Next"),
                        childAtPosition(
                                allOf(withId(R.id.buttonholder),
                                        childAtPosition(
                                                withId(R.id.navigation_view),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatTextView56.perform(click());

        onView(isRoot()).perform(waitFor(1000));

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.save_exit_button), withText("Save Form and Exit"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                6)));
        appCompatButton3.perform(scrollTo(), click());
        onView(isRoot()).perform(waitFor(2000));



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

    private static Matcher<View> withIndex(Matcher<View> viewMatcher, final Matcher<View> matcher, final int index) {
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

    private static Matcher<View> first(Matcher<View> expected ){

        return new TypeSafeMatcher<View>() {
            private boolean first = false;

            @Override
            protected boolean matchesSafely(View item) {

                if( expected.matches(item) && !first ){
                    return first = true;
                }

                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Matcher.first( " + expected.toString() + " )" );
            }
        };
    }

}
