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
import timber.log.Timber;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {

    @BindView(R2.id.parent_profile_elements)
    public LinearLayout parentProfileElements;

    private ArrayList<UserProfileElement> userProfileElements;
    private ArrayList<ProfileElementHolder> dynamicHolders;
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
            dynamicHolders = new ArrayList<>();
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
        boolean sectionChanged;
        for (UserProfileElement profileElement : userProfileElements) {
            currentSection = profileElement.getSection();
            sectionChanged = prevSection != -1 && prevSection != currentSection;
            if (prevSection != -1 && sectionChanged) {
                parentProfileElements.addView(LayoutInflater.from(this).inflate(R.layout.profile_divider, parentProfileElements, false));
            }
            switch (profileElement.getProfileElementContentType()) {
                case TEXT:
                    View simpleTextView = LayoutInflater.from(this).inflate(R.layout.profile_simple_text_row, parentProfileElements, false);
                    SimpleTextViewHolder simpleTextViewHolder = new SimpleTextViewHolder(simpleTextView, profileElement);
                    dynamicHolders.add(simpleTextViewHolder);
                    parentProfileElements.addView(simpleTextView);
                    break;
                case DATE:
                    View dateTextView = LayoutInflater.from(this).inflate(R.layout.profile_date_text_row, parentProfileElements, false);
                    DateTextViewHolder dateTextViewHolder = new DateTextViewHolder(dateTextView, profileElement);
                    dynamicHolders.add(dateTextViewHolder);
                    parentProfileElements.addView(dateTextView);
                    break;
                case NUMBER:
                    View numberTextView = LayoutInflater.from(this).inflate(R.layout.profile_number_text_row, parentProfileElements, false);
                    NumberTextViewHolder numberTextViewHolder = new NumberTextViewHolder(numberTextView, profileElement);
                    dynamicHolders.add(numberTextViewHolder);
                    parentProfileElements.addView(numberTextView);
                    break;
                case SPINNER:
                    //TODO: Inflate View
                    break;
            }
            Timber.d("Prev Section - %s, Current Section - %s", prevSection, currentSection);
            prevSection = currentSection;
        }
    }

    @Override
    public void onProfileEditButtonClicked(ProfileContract.View v) {
        for (ProfileElementHolder elementHolder : dynamicHolders) {
            elementHolder.toggleHolderEnable(true);
        }
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

    interface ProfileElementHolder {
        void toggleHolderEnable(boolean enable);
    }


    static class SimpleTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        SimpleTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(userProfileElement.getContent());
            itemIcon.setImageResource(R.drawable.ic_people_black_24dp);
            toggleHolderEnable(false);
        }

        @Override
        public void toggleHolderEnable(boolean enable) {
            if (userProfileElement.isEditable()) {
                textInputEditText.setEnabled(enable);
                textInputEditText.setClickable(enable);
            }
        }
    }

    static class NumberTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_edit_text)
        TextInputEditText textInputEditText;

        private UserProfileElement userProfileElement;

        NumberTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textInputEditText.setText(userProfileElement.getContent());
            itemIcon.setImageResource(R.drawable.ic_call_black_24dp);
            toggleHolderEnable(false);
        }

        @Override
        public void toggleHolderEnable(boolean enable) {
            if (userProfileElement.isEditable()) {
                textInputEditText.setEnabled(enable);
                textInputEditText.setClickable(enable);
            }
        }
    }

    static class DateTextViewHolder implements ProfileElementHolder {
        @BindView(R2.id.icon)
        ImageView itemIcon;
        @BindView(R2.id.item_description)
        AppCompatTextView textViewItemDesc;
        @BindView(R2.id.text_date)
        AppCompatTextView textViewDate;

        private UserProfileElement userProfileElement;

        DateTextViewHolder(View view, UserProfileElement userProfileElement) {
            ButterKnife.bind(this, view);
            this.userProfileElement = userProfileElement;
            textViewItemDesc.setText(userProfileElement.getTitle());
            textViewDate.setText(userProfileElement.getContent());
            itemIcon.setImageResource(R.drawable.ic_date_range_black_24dp);
            toggleHolderEnable(false);
        }


        @Override
        public void toggleHolderEnable(boolean enable) {
            if (userProfileElement.isEditable()) {
                textViewDate.setEnabled(enable);
                textViewDate.setClickable(enable);
            }
        }
    }
}
