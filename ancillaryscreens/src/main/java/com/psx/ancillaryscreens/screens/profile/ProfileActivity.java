package com.psx.ancillaryscreens.screens.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.psx.ancillaryscreens.InvalidConfigurationException;
import com.psx.ancillaryscreens.R;
import com.psx.ancillaryscreens.R2;
import com.psx.ancillaryscreens.base.BaseActivity;
import com.psx.ancillaryscreens.models.UserProfileElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {

    @BindView(R2.id.parent_profile_elements)
    public LinearLayout parentProfileElements;

    private ArrayList<UserProfileElement> userProfileElements;
    private Unbinder unbinder;

    @Inject
    ProfilePresenter<ProfileContract.View, ProfileContract.Interactor> profilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        unbinder = ButterKnife.bind(this);
        getActivityComponent().inject(this);
        profilePresenter.onAttach(this);
        if (getIntent() != null && getIntent().getParcelableArrayListExtra("config") != null) {
            userProfileElements = getIntent().getParcelableArrayListExtra("config");
        } else {
            throw new InvalidConfigurationException(ProfileActivity.class);
        }
        initUserDetails(userProfileElements);
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
     * @param userProfileElements - The {@link UserProfileElement} object containing necessary info about the
     *                            user that has to be displayed on the screen. Any Null fields will be
     *                            hidden from view.
     */
    @Override
    public void initUserDetails(ArrayList<UserProfileElement> userProfileElements) {
        Collections.sort(userProfileElements, (userProfileElement, t1) -> userProfileElement.getSection() - t1.getSection());
        int currentSection = -1;
        int prevSection = -1;
        boolean sectionChanged = false;
        for (UserProfileElement profileElement : userProfileElements) {
            if (prevSection != -1 && sectionChanged) {
                // TODO Draw divider;
                sectionChanged = false;
            }
            prevSection = currentSection;
            currentSection = profileElement.getSection();
            switch (profileElement.getProfileElementContentType()) {
                case TEXT:
                    //TODO: Inflate View
                    View simpleTextView = LayoutInflater.from(this).inflate(R.layout.profile_simple_text_row, parentProfileElements, true);
                    SimpleTextViewHolder simpleTextViewHolder = new SimpleTextViewHolder(simpleTextView, profileElement);
                    break;
                case DATE:
                    //TODO: Inflate View
                    break;
                case NUMBER:
                    //TODO: Inflate View
                    break;
                case SPINNER:
                    //TODO: Inflate View
                    break;
            }
            sectionChanged = prevSection != -1 && prevSection != currentSection;
        }
    }

    private void bindValuesToView(UserProfileElement userProfileElement, View view, UserProfileElement.ProfileElementContentType profileElementContentType) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    static class SimpleTextViewHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        SimpleTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(userProfileElement.getContent());
            itemIcon.setImageResource(R.drawable.ic_people_black_24dp);
        }
    }
}
