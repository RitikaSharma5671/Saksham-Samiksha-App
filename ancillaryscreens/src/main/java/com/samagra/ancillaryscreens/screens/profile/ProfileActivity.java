package com.samagra.ancillaryscreens.screens.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.samagra.ancillaryscreens.InvalidConfigurationException;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.R2;
import com.samagra.ancillaryscreens.base.BaseActivity;
import com.samagra.ancillaryscreens.models.UserProfileElement;
import com.samagra.ancillaryscreens.utils.SnackbarUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.DateTextViewHolder;
import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.NumberTextViewHolder;
import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.ProfileElementHolder;
import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.SimpleTextViewHolder;
import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.SpinnerTextViewHolder;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {

    @BindView(R2.id.parent_profile_elements)
    public LinearLayout parentProfileElements;

    private ArrayList<UserProfileElement> userProfileElements;
    private ArrayList<ProfileElementHolder> dynamicHolders;
    private Unbinder unbinder;
    private boolean isInEditMode;
    private Snackbar progressSnackbar = null;

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
        initToolbar();
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
     * This method populates the fields displayed on the screen using multiple {@link UserProfileElement}
     * objects provided to {@link ProfileActivity} during its launch.
     *
     * @param userProfileElements - An {@link ArrayList} of {@link UserProfileElement} objects.
     *                            These list of objects represent individual elements of a User's
     *                            Profile.
     * @see com.samagra.ancillaryscreens.AncillaryScreensDriver#launchProfileActivity(Context, ArrayList)
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
                    SimpleTextViewHolder simpleTextViewHolder = new SimpleTextViewHolder(simpleTextView, profileElement, profilePresenter.getContentValueFromKey(profileElement.getContent()));
                    dynamicHolders.add(simpleTextViewHolder);
                    parentProfileElements.addView(simpleTextView);
                    break;
                case DATE:
                    View dateTextView = LayoutInflater.from(this).inflate(R.layout.profile_date_text_row, parentProfileElements, false);
                    DateTextViewHolder dateTextViewHolder = new DateTextViewHolder(dateTextView, profileElement, profilePresenter.getContentValueFromKey(profileElement.getContent()));
                    dynamicHolders.add(dateTextViewHolder);
                    parentProfileElements.addView(dateTextView);
                    break;
                case PHONE_NUMBER:
                    View numberTextView = LayoutInflater.from(this).inflate(R.layout.profile_number_text_row, parentProfileElements, false);
                    NumberTextViewHolder numberTextViewHolder = new NumberTextViewHolder(numberTextView, profileElement, profilePresenter.getContentValueFromKey(profileElement.getContent()));
                    dynamicHolders.add(numberTextViewHolder);
                    parentProfileElements.addView(numberTextView);
                    break;
                case SPINNER:
                    View spinnerView = LayoutInflater.from(this).inflate(R.layout.profile_spinner_row, parentProfileElements, false);
                    SpinnerTextViewHolder spinnerTextViewHolder = new SpinnerTextViewHolder(spinnerView, profileElement, profilePresenter.getContentValueFromKey(profileElement.getContent()));
                    dynamicHolders.add(spinnerTextViewHolder);
                    parentProfileElements.addView(spinnerView);
                    break;
            }
            prevSection = currentSection;
        }
    }

    @OnClick(R2.id.fab)
    @Override
    public void onProfileEditButtonClicked(View v) {
        // save if already in edit mode prior to click.
        if (isInEditMode) {
            if (profilePresenter.validateUdpatedFields(dynamicHolders)) {
                profilePresenter.updateUserProfileAtRemote(dynamicHolders);
                isInEditMode = !isInEditMode; // update the edit mode flag (accounting for the click)
            }
        } else {
            isInEditMode = true; // update the edit mode flag (accounting for the click)
        }

        if (isInEditMode)
            ((FloatingActionButton) v).setImageResource(R.drawable.ic_save_icon_color_24dp);
        else
            ((FloatingActionButton) v).setImageResource(R.drawable.ic_edit_icon_color_24dp);
        for (ProfileElementHolder elementHolder : dynamicHolders) {
            elementHolder.toggleHolderEnable(isInEditMode);
        }
    }

    @OnClick(R2.id.fab_edit_password)
    @Override
    public void onEditPasswordButtonClicked(View v) {
        // TODO : Implement this
        Toast.makeText(this, " Edit Password Clicked ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(String message) {
        if (progressSnackbar == null) {
            progressSnackbar = SnackbarUtils.getSnackbarWithProgressIndicator(findViewById(android.R.id
                    .content), this, message);
            progressSnackbar.show();
        } else {
            progressSnackbar.setText(message);
            progressSnackbar.show();
        }
    }

    @Override
    public void hideLoading() {
        if (progressSnackbar != null && progressSnackbar.isShownOrQueued()) {
            progressSnackbar.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profilePresenter.onDestroy();
        unbinder.unbind();
        profilePresenter.onDetach();
    }
}
