package com.samagra.ancillaryscreens.screens.profile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.samagra.ancillaryscreens.AncillaryScreensDriver;
import com.samagra.ancillaryscreens.InvalidConfigurationException;
import com.samagra.ancillaryscreens.R;
import com.samagra.ancillaryscreens.base.BaseActivity;
import com.samagra.ancillaryscreens.screens.passReset.ChangePasswordActionListener;
import com.samagra.ancillaryscreens.screens.passReset.OTPActivity;
import com.samagra.ancillaryscreens.screens.passReset.SendOTPTask;
import com.samagra.ancillaryscreens.utils.SnackbarUtils;
import com.samagra.cascading_module.CascadingModuleDriver;
import com.samagra.commons.InstitutionInfo;
import com.samagra.commons.MainApplication;
import com.samagra.grove.logging.Grove;

import org.odk.collect.android.application.Collect1;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.NumberTextViewHolder;
import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.ProfileElementHolder;
import static com.samagra.ancillaryscreens.screens.profile.ProfileElementViewHolders.SpinnerTextViewHolder;

public class ProfileActivity extends BaseActivity implements ProfileContract.View {

    private LinearLayout parentProfileElements;
    private FloatingActionButton fab;
    private ArrayList<UserProfileElement> userProfileElements;
    private ArrayList<ProfileElementHolder> dynamicHolders;
    private boolean isInEditMode;
    private Snackbar progressSnackbar = null;
    private ProgressDialog mProgress;
    TextView schoolCode;
    TextView schoolName;
    TextView schoolBlock;
    TextView schoolDistrict;
    TextView change_details;

    @Inject
    ProfilePresenter<ProfileContract.View, ProfileContract.Interactor> profilePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        parentProfileElements = findViewById(R.id.parent_profile_elements);
        fab = findViewById(R.id.fab);
        Button editPassword = findViewById(R.id.fab_edit_password);
        getActivityComponent().inject(this);
        profilePresenter.onAttach(this);
        if (getIntent() != null && getIntent().getParcelableArrayListExtra("config") != null) {
            userProfileElements = getIntent().getParcelableArrayListExtra("config");
            dynamicHolders = new ArrayList<>();
        } else {
            throw new InvalidConfigurationException(ProfileActivity.class);
        }
        initToolbar();
        Grove.d("Profile Activity onCreate() called ");
        initUserDetails(userProfileElements);
        fab.setOnClickListener(v -> onProfileEditButtonClicked());
        editPassword.setOnClickListener(v -> onEditPasswordButtonClicked());
        attachListenersForSchoolLayout();
        mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.logging_in));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    @SuppressLint("SetTextI18n")
    private void attachListenersForSchoolLayout() {
        RelativeLayout schoolLayout = findViewById(R.id.school_layout);
        if(profilePresenter.isTeacherAccount() || profilePresenter.isSchoolAccount()) {
            schoolLayout.setVisibility(View.VISIBLE);
            change_details = findViewById(R.id.change_details);
            LinearLayout changeDetailsLL = findViewById(R.id.change_details_label);
            if(profilePresenter.isSchoolAccount()){
                changeDetailsLL.setVisibility(View.GONE);
            }else if(profilePresenter.isTeacherAccount()) {
                changeDetailsLL.setVisibility(View.VISIBLE);
            }
            SpannableString content = new SpannableString(getText(R.string.change_details));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            change_details.setText(content);
            String ROOT = Collect1.getInstance().getStoragePathProvider().getScopedStorageRootDirPath();
            String FILE_PATH =  Collect1.getInstance().getStoragePathProvider().getScopedStorageRootDirPath() + "/saksham_data_json.json";
            InstitutionInfo institutionInfo = profilePresenter.fetchSchoolDetails();
            schoolDistrict = findViewById(R.id.school_district);
            schoolBlock = findViewById(R.id.school_block);
            schoolName = findViewById(R.id.school_name);
            schoolCode = findViewById(R.id.school_code);
            schoolCode.setText(String.format(getString(R.string.school_code),String.valueOf(institutionInfo.getSchoolCode())));
            schoolName.setText(String.format(getString(R.string.school_name_label), institutionInfo.getSchoolName()));
            schoolBlock.setText(String.format(getString(R.string.school_block_label),institutionInfo.getBlock()));
            schoolDistrict.setText(String.format(getString(R.string.school_district_label),institutionInfo.getDistrict()));
            change_details.setOnClickListener(v -> {
                CascadingModuleDriver.init((MainApplication) getApplicationContext(), FILE_PATH, ROOT);
                launch();
            });
        }else{
            schoolLayout.setVisibility(View.GONE);
        }

    }

    private void launch() {
        CascadingModuleDriver.launchSearchView(getActivityContext(),
                this, CascadingModuleDriver.SEARCH_ACTIVITY_REQUEST_CODE_PROFILE);
    }

    @Override
    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivityContext(), R.drawable.ic_icon_back));
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
                parentProfileElements.addView(LayoutInflater.from(this).inflate(R.layout.profile_section_change, parentProfileElements, false));
            }
            switch (profileElement.getProfileElementContentType()) {
                case TEXT:
                    View simpleTextView = LayoutInflater.from(this).inflate(R.layout.profile_simple_text_row, parentProfileElements, false);
                    ProfileElementViewHolders.SimpleTextViewHolder simpleTextViewHolder = new ProfileElementViewHolders.SimpleTextViewHolder(simpleTextView, profileElement, profilePresenter.getContentValueFromKey(profileElement.getContent()));
                    dynamicHolders.add(simpleTextViewHolder);
                    parentProfileElements.addView(simpleTextView);
                    break;
                case DATE:
                    View dateTextView = LayoutInflater.from(this).inflate(R.layout.profile_date_text_row, parentProfileElements, false);
                    ProfileElementViewHolders.DateTextViewHolder dateTextViewHolder = new ProfileElementViewHolders.DateTextViewHolder(dateTextView, profileElement, profilePresenter.getContentValueFromKey(profileElement.getContent()));
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

    @Override
    public void onProfileEditButtonClicked() {
        if (profilePresenter.isNetworkConnected()) {
            // save if already in edit mode prior to click.
            if (isInEditMode) {
                if (profilePresenter.validateUpdatedFields(dynamicHolders)) {
                    String updatedPhone = dynamicHolders.get(1).getUpdatedElementValue();
                    if (!updatedPhone.equals("") && isValidPhoneNumber(updatedPhone)) {
                        if (profilePresenter.isNetworkConnected()) {
                            profilePresenter.updateUserProfileAtRemote(dynamicHolders, "");
                        } else {
                            showSnackbar(getActivityContext().getResources().getString(R.string.internet_not_connected_profile_screen), 3000);
                        }
                    } else {
                        showSnackbar(getString(R.string.try_otp_for_no_phone), 3000);
                    }
                    isInEditMode = !isInEditMode; // update the edit mode flag (accounting for the click)
                }
            } else {
                isInEditMode = true; // update the edit mode flag (accounting for the click)
            }

            if (isInEditMode)
                fab.setImageResource(R.drawable.ic_save_icon_color_24dp);
            else
                fab.setImageResource(R.drawable.ic_edit_icon_color_24dp);
            for (ProfileElementHolder elementHolder : dynamicHolders) { elementHolder.toggleHolderEnable(isInEditMode);
            }
        } else {
            fab.setImageResource(R.drawable.ic_edit_icon_color_24dp);
            showSnackbar(getActivityContext().getResources().getString(R.string.internet_not_connected_profile_screen), 3000);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        Pattern p = Pattern.compile("[6-9][0-9]{9}");
        Matcher m = p.matcher(phoneNumber);
        return !m.find() || !m.group().equals(phoneNumber);
    }


    @Override
    public void onEditPasswordButtonClicked() {
        showLoading("Sending OTP");
        if(profilePresenter.isNetworkConnected()){
            String phoneNumber= profilePresenter.getContentValueFromKey(dynamicHolders.get(1).getUserProfileElement().getContent());
            if(!phoneNumber.isEmpty() && !isValidPhoneNumber(phoneNumber)) {
            new SendOTPTask(new ChangePasswordActionListener() {
                @Override
                public void onSuccess() {
                    hideLoading();
                    Intent otpIntent = new Intent(ProfileActivity.this, OTPActivity.class);
                    otpIntent.putExtra("phoneNumber", phoneNumber);
                    otpIntent.putExtra("last", "profile");
                    startActivity(otpIntent);
                }

                @Override
                public void onFailure(Exception exception) {
                    showSnackbar(getActivityContext().getResources().getString(R.string.error_sending_otp), 3000);
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, phoneNumber);
        }else{
            hideLoading();
                showSnackbar(getString(R.string.try_otp_for_no_phone), 3000);

        }}else{
            showSnackbar(getActivityContext().getResources().getString(R.string.internet_not_connected_otp), 3000);
        }

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
    public void onSuccessDone(InstitutionInfo institutionInfo) {
        mProgress.dismiss();
        schoolCode.setText(String.format(getString(R.string.school_code),String.valueOf(institutionInfo.getSchoolCode())));
        schoolName.setText(String.format(getString(R.string.school_name_label), institutionInfo.getSchoolName()));
        schoolBlock.setText(String.format(getString(R.string.school_block_label),institutionInfo.getBlock()));
        schoolDistrict.setText(String.format(getString(R.string.school_district_label),institutionInfo.getDistrict()));
        AncillaryScreensDriver.onProfileSuccessfullyUpdated(String.valueOf(institutionInfo.getSchoolCode()));
        showSnackbar(getString(R.string.school_changed), 3000);
    }

    @Override
    public void onErrorUpdateSchoolData() {
        mProgress.dismiss();
        showSnackbar(getString(R.string.unable_to_change_school), 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        profilePresenter.onDestroy();
        profilePresenter.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CascadingModuleDriver.SEARCH_ACTIVITY_REQUEST_CODE_PROFILE) {
            Grove.d("Received result from Search Activity...");
            String selectedDistrict = data.getStringExtra("selectedDistrict");
            String selectedBlock = data.getStringExtra("selectedBlock");
            String selectedSchool = data.getStringExtra("selectedSchool");
            int schoolCode = data.getIntExtra("selectedSchoolCode", 0);
            Grove.d("Selected District Name is >> " + selectedDistrict + "  Selected Block name is >> " +
                    selectedBlock + "  Selected school name is >> " + selectedSchool + " with school code: " + schoolCode);
            InstitutionInfo institutionInfo = new InstitutionInfo(selectedDistrict, selectedBlock, selectedSchool, schoolCode);
            mProgress.show();
            profilePresenter.performUpdateSchoolCode(getActivityContext(),
                    getActivityContext().getResources().getString(R.string.fusionauth_api_key), institutionInfo);
        }
    }
}
