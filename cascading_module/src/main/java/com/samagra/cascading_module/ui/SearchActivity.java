package com.samagra.cascading_module.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;

import com.airbnb.lottie.LottieAnimationView;
import com.samagra.cascading_module.R;
import com.samagra.cascading_module.R2;
import com.samagra.cascading_module.base.BaseActivity;
import com.samagra.commons.InstitutionInfo;
import com.samagra.grove.logging.Grove;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * View part of the Search Screen. This class only handles the UI operations, all the business logic is simply
 * abstracted from this Activity. It <b>must</b> implement the {@link SearchMvpView} and extend the {@link BaseActivity}.
 *
 * @author Pranav Sharma
 */
public class SearchActivity extends BaseActivity implements SearchMvpView {

    @BindView(R2.id.level_1_spinner)
    public Spinner districtSpinner;
    @BindView(R2.id.level_2_spinner)
    public Spinner blockSpinner;
    @BindView(R2.id.level_4_spinner)
    public Spinner schoolNameSpinner;
    @BindView(R2.id.next_button)
    public Button nextButton;
    @BindView(R2.id.rootView)
    public ConstraintLayout rootView;
    @BindView(R2.id.search_layout)
    public ConstraintLayout searchLayout;
    @BindView(R2.id.lottie_loader_search)
    public LottieAnimationView lottie_loader;

    private String selectedDistrict ="";
    private String selectedBlock = "";
    private String selectedSchoolName = "";
    InstitutionInfo selectedSchoolData;
    private KeyboardHandler keyboardHandler;
    private int count = 0;
    private String two_Spaces = " ";
    String userName = "";
    @Inject
    SearchPresenter<SearchMvpView, SearchMvpInteractor> searchPresenter;
    private Unbinder unbinder;
    SharedPreferences sharedPreferences;
    String schoolInfoFromPreferences;
    InstitutionInfo schoolGeoDataFromPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getActivityComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        searchPresenter.onAttach(this);
        setupToolbar();
        initializeKeyboardHandler();
        searchPresenter.addKeyboardListeners(keyboardHandler);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(getIntent() != null){
            userName = getIntent().getStringExtra("userName");
        }
        schoolInfoFromPreferences = sharedPreferences.getString("studentGeoData", "");

        schoolGeoDataFromPreferences = TextUtils.isEmpty(schoolInfoFromPreferences) ? null : searchPresenter.fetchObjectFromPreferenceString(schoolInfoFromPreferences);
        if (schoolGeoDataFromPreferences != null) {
            Grove.d("Data saved in Preferences for Cascading Module, District %s Block %s School %s", schoolGeoDataFromPreferences.District, schoolGeoDataFromPreferences.Block, schoolGeoDataFromPreferences.SchoolName);
            count = 1;
            selectedSchoolData = schoolGeoDataFromPreferences;
            selectedDistrict = schoolGeoDataFromPreferences.District;
            selectedBlock = schoolGeoDataFromPreferences.Block;
            selectedSchoolName = schoolGeoDataFromPreferences.SchoolName;
        }
        searchPresenter.loadValuesToMemory();
        initSpinners();
        initNextButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
        nextButton.setClickable(true);
        nextButton.setEnabled(true);
        lottie_loader.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten.
     * If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.search_screen_title));
        setSupportActionBar(toolbar);
    }

    private void customizeToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }
    }

    private void initializeKeyboardHandler() {
        keyboardHandler = new KeyboardHandler(false,
                false, null, SearchActivity.this);
    }

    private void initSpinners() {
        addValuesToSpinner(districtSpinner, searchPresenter.getLevel1Values());
        setListenerOnDistrictSpinner();
        setListenerOnBlockSpinner();
        setListenerOnSchoolNameSpinner();
        if (schoolInfoFromPreferences != null && schoolGeoDataFromPreferences != null) {
            nextButton.setEnabled(true);
            blockSpinner.setEnabled(true);
            schoolNameSpinner.setEnabled(true);
            setDataForSpinnersExplicit(schoolGeoDataFromPreferences, false);
        } else {
            nextButton.setEnabled(false);
            blockSpinner.setEnabled(false);
            schoolNameSpinner.setEnabled(true);
        }
    }


    private void setDataForSpinnersExplicit(InstitutionInfo individualStudentData, boolean b) {
        ArrayList<String> districtValues = searchPresenter.getLevel1Values();
        addValuesToSpinner(districtSpinner, districtValues);
        selectedDistrict = individualStudentData.District;
        districtSpinner.setSelection(districtValues.indexOf(individualStudentData.District));

        ArrayList<String> blockValues = searchPresenter.getLevel2ValuesUnderLevel1Set(individualStudentData.District);
        addValuesToSpinner(blockSpinner, blockValues);
        selectedBlock = individualStudentData.Block;
        blockSpinner.setSelection(blockValues.indexOf(individualStudentData.Block));

        ArrayList<String> institutionTypeValues = searchPresenter.getLevel3ValuesUnderLevel1Set(individualStudentData.Block, selectedDistrict);
        addValuesToSpinner(schoolNameSpinner, institutionTypeValues);
        selectedSchoolName = individualStudentData.SchoolName;
        schoolNameSpinner.setSelection(institutionTypeValues.indexOf(individualStudentData.SchoolName));
    }

    private void setListenerOnDistrictSpinner() {
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (schoolInfoFromPreferences == null || schoolInfoFromPreferences.isEmpty() || (!selectedDistrict.equals(parent.getItemAtPosition(position).toString()))) {
                    selectedDistrict = parent.getItemAtPosition(position).toString();
                    blockSpinner.setEnabled(true);
                    blockSpinner.setClickable(true);
                    keyboardHandler.spinner = null;
                    if (count == 1) {
                        if (!schoolInfoFromPreferences.isEmpty()) {
                            schoolInfoFromPreferences = "";
                            schoolGeoDataFromPreferences = null;
                        }
                        count = 0;
                        selectedBlock = two_Spaces + getActivityContext().getResources().getString(R.string.dummy_block);
                        selectedSchoolName = two_Spaces + getActivityContext().getResources().getString(R.string.dummy_gram_panchayat);
                    }
                    addValuesToSpinner(
                            blockSpinner,
                            searchPresenter.getLevel2ValuesUnderLevel1Set(selectedDistrict)
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(blockSpinner);
                makeSpinnerDefault(schoolNameSpinner);

            }
        });
    }


    private void setListenerOnBlockSpinner() {
        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (schoolInfoFromPreferences.isEmpty() || (!selectedBlock.equals(parent.getItemAtPosition(position).toString()))) {
                    selectedBlock = parent.getItemAtPosition(position).toString();
                    schoolNameSpinner.setEnabled(true);
                    schoolNameSpinner.setClickable(true);
                    keyboardHandler.spinner = null;
                    if (count == 1) {
                        if (!schoolInfoFromPreferences.isEmpty()) {
                            schoolInfoFromPreferences = "";
                            schoolGeoDataFromPreferences = null;
                        }
                        count = 0;
                        selectedSchoolName = two_Spaces + getActivityContext().getResources().getString(R.string.dummy_gram_panchayat);
                    }
                    addValuesToSpinner(
                            schoolNameSpinner,
                            searchPresenter.getLevel3ValuesUnderLevel1Set(selectedBlock, selectedDistrict)
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(schoolNameSpinner);
            }
        });
    }


    private void setListenerOnSchoolNameSpinner() {
        schoolNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (schoolInfoFromPreferences.isEmpty() || (!selectedSchoolName.equals(parent.getItemAtPosition(position).toString()))) {
                    keyboardHandler.spinner = null;
                    selectedSchoolName = parent.getItemAtPosition(position).toString();
                    selectedSchoolData = new InstitutionInfo(selectedDistrict, selectedBlock, selectedSchoolName);
                    // Enable the next button
                    nextButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(schoolNameSpinner);
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addValuesToSpinner(Spinner spinner, ArrayList<String> values) {
        String[] val = values.toArray(new String[0]);
        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, val);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnTouchListener((v, event) -> {
            keyboardHandler.spinner = spinner;
            keyboardHandler.isDropDownOpen = true;
            if (keyboardHandler.isUDISEKeyboardShowing) keyboardHandler.closeUDISEKeyboard();
            return false;
        });

    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            if (districtSpinner.getSelectedItem().toString().equals(two_Spaces + getActivityContext().getResources().getString(R.string.dummy_district)) ||
                    blockSpinner.getSelectedItem().toString().equals(two_Spaces + getActivityContext().getResources().getString(R.string.dummy_block)) ||
                    schoolNameSpinner.getSelectedItem().toString().equals(two_Spaces + getActivityContext().getResources().getString(R.string.dummy_gram_panchayat))) {
                SnackbarUtils.showLongSnackbar(rootView, getActivityContext().getResources().getString(R.string.error_message_search));
            } else {
                Grove.d("User selected the data %s %s %s", selectedDistrict, selectedBlock, selectedSchoolName);
                searchLayout.setClickable(false);
                startAnimation();
                updatePreferenceData();
                nextButton.setClickable(false);
                nextButton.setEnabled(false);
                Intent intent = new Intent();
                intent.putExtra("selectedDistrict", selectedDistrict);
                intent.putExtra("selectedBlock", selectedBlock);
                intent.putExtra("selectedSchool", selectedSchoolName);
                Grove.d("Setting result, sending back to Home Screen");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void startAnimation() {
        lottie_loader.setVisibility(View.VISIBLE);
        lottie_loader.setAnimation("loader.json");
        lottie_loader.setRepeatCount(ValueAnimator.INFINITE);
        lottie_loader.playAnimation();
    }

    @SuppressWarnings("PointlessNullCheck")
    private void updatePreferenceData() {
        if (schoolGeoDataFromPreferences == null || !selectedSchoolData.equals(schoolGeoDataFromPreferences)) {
            sharedPreferences.edit().putString("studentGeoData", searchPresenter.generateObjectForStudentData(selectedSchoolData)).apply();
        }
    }

    /**
     * This functions simply shifts a {@link Spinner} to its default state; a state that prevents
     * a user from interacting with the spinner. This is essentially disabling a spinner on the UI.
     *
     * @param spinner - The {@link Spinner} widget to disable
     */
    @Override
    public void makeSpinnerDefault(Spinner spinner) {
        spinner.setEnabled(false);
        spinner.setClickable(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        searchPresenter.onDetach();
    }
}
