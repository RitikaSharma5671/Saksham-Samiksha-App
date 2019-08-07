package com.psx.ancillaryscreens.screens.profile;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.psx.ancillaryscreens.InvalidConfigurationException;
import com.psx.ancillaryscreens.R;
import com.psx.ancillaryscreens.base.BaseActivity;
import com.psx.ancillaryscreens.models.UserProfileElement;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {

    private UserProfileElement userProfileElementObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (getIntent() != null && getIntent().getParcelableArrayListExtra("config") != null) {

        } else {
            throw new InvalidConfigurationException(ProfileActivity.class);
        }
    }

    @Override
    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialises Calendar that is used to select User's Data of joining.
     */
    @Override
    public void initCalendar() {

    }

    /**
     * This method populates the fields displayed on the screen using a {@link UserProfileElement} object
     * provided to {@link ProfileActivity} during its launch.
     *
     * @param userProfileElement - The {@link UserProfileElement} object containing necessary info about the
     *                           user that has to be displayed on the screen. Any Null fields will be
     *                           hidden from view.
     */
    @Override
    public void initUserDetails(ArrayList<UserProfileElement> userProfileElement) {

    }

    @Override
    public void onProfileEditButtonClicked(ProfileContract.View v) {

    }

    @Override
    public void onEditPasswordButtonClicked(ProfileContract.View v) {

    }

    @Override
    public boolean validatePhoneNumber(String phoneNumber) {
        return false;
    }
}
