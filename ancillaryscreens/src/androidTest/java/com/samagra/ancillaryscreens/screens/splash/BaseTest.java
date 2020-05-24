package com.samagra.ancillaryscreens.screens.splash;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;

/**
 * Created by Umang Bhola on 21/5/20.
 * Samagra- Transforming Governance
 */
public abstract class BaseTest {

    /**
     * Start Activity with given intent. This method will put extra necessary stuff required to start Activity
     * @param activityTestRule
     * @param intent
     */
    public void startActivity(@NonNull ActivityTestRule activityTestRule, @NonNull Intent intent) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        activityTestRule.launchActivity(intent);
    }

    public Intent getDefaultIntent() {
        Intent intent = new Intent();
        return intent;
    }

    @After
    public void resetItems() {
        try {
            Intents.release();
        } catch (Exception e) {
            Log.i("Exception Catch", "Exception at releasing intent in GB Test");
        }
    }

    /**t
     * Must return their corresponding ActivityTestRule by all subclasses
     * @return
     */
    public abstract ActivityTestRule getActivityTestRule();
}

