package com.psx.odktest.ui.SearchActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.psx.odktest.R;
import com.psx.odktest.base.BaseActivity;
import com.psx.odktest.data.models.School;
import com.psx.odktest.helper.KeyboardHandler;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.utilities.SnackbarUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.psx.odktest.UtilityFunctions.hideKeyboard;

public class SearchActivity extends BaseActivity implements SearchMvpView {

    @BindView(R.id.district_spinner)
    public Spinner districtSpinner;
    @BindView(R.id.block_spinner)
    public Spinner blockSpinner;
    @BindView(R.id.cluster_spinner)
    public Spinner clusterSpinner;
    @BindView(R.id.school_spinner)
    public Spinner schoolSpinner;
    @BindView(R.id.next_button)
    public Button nextButton;
    @BindView(R.id.search_bar)
    public EditText udiseTextBox;
    @BindView(android.R.id.content)
    public FrameLayout rootView;

    private String selectedDistrict, selectedBlock, selectedCluster, selectedSchoolName;
    School selectedSchool;
    private String previousUDISE;
    private KeyboardHandler keyboardHandler;
    public boolean isChangedFromEditText = false;

    @Inject
    SearchPresenter<SearchMvpView, SearchMvpInteractor> searchPresenter;
    private Unbinder unbinder;


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
        searchPresenter.loadValuesToMemory();
        initSpinners();
        initUDISEBox();
        initNextButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customizeToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Only set the title and action bar here; do not make further modifications.
     * Any further modifications done to the toolbar here will be overwritten if you
     * use {@link ODKDriver}. If you wish to prevent modifications
     * from being overwritten, do them after onCreate is complete.
     * This method should be called in onCreate of your activity.
     */
    @Override
    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("School Selection");
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

    @SuppressWarnings("unchecked")
    private void initSpinners() {
        nextButton.setEnabled(false);
        blockSpinner.setEnabled(false);
        clusterSpinner.setEnabled(false);
        schoolSpinner.setEnabled(false);

        ArrayAdapter<String> districtSpinnerAdapter = addValuesToSpinner(districtSpinner, searchPresenter.getDistrictValues());
        setListenerOnDistrictSpinner();
        setListenerOnBlockSpinner();
        setListenerOnClusterSpinner();
        setListenerOnSchoolSpinner();
    }

    private void initUDISEBox() {
        udiseTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Update spinners
                boolean found = false;
                if (searchPresenter.isUDISEValid(s.toString(), previousUDISE)) {
                    for (School school : searchPresenter.getSchoolList()) {
                        if (school.udise.equals(s.toString())) {
                            hideKeyboard(SearchActivity.this);
                            selectedSchool = school;
                            found = true;
                            isChangedFromEditText = true;

                            ArrayList<String> districtValues = searchPresenter.getDistrictValues();
                            addValuesToSpinner(districtSpinner, districtValues);
                            selectedDistrict = school.district;
                            districtSpinner.setSelection(districtValues.indexOf(school.district));

                            ArrayList<String> blockValues = searchPresenter.getBlockValuesForSelectedDistrict(school.district);
                            addValuesToSpinner(blockSpinner, blockValues);
                            selectedBlock = school.block;
                            blockSpinner.setSelection(blockValues.indexOf(school.block));

                            ArrayList<String> clusterValues = searchPresenter.getClusterValuesForSelectedBlock(school.block);
                            addValuesToSpinner(clusterSpinner, clusterValues);
                            selectedCluster = school.cluster;
                            clusterSpinner.setSelection(clusterValues.indexOf(school.cluster));

                            ArrayList<String> schoolValues = searchPresenter.getSchoolValuesForSelectedCluster(school.cluster);
                            addValuesToSpinner(schoolSpinner, schoolValues);
                            selectedSchoolName = school.schoolName;
                            schoolSpinner.setSelection(schoolValues.indexOf(school.schoolName));

                            previousUDISE = s.toString();

                        }
                    }
                    if (!found) {
                        hideKeyboard(SearchActivity.this);
                        SnackbarUtils.showLongSnackbar(rootView, "It seems you have entered a UDISE number not in the database. Please check");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setListenerOnDistrictSpinner() {
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isChangedFromEditText) {
                    selectedDistrict = parent.getItemAtPosition(position).toString();
                    blockSpinner.setEnabled(true);
                    blockSpinner.setClickable(true);
                    keyboardHandler.spinner = null;
                    ArrayAdapter<String> blockSpinnerAdapter = addValuesToSpinner(
                            blockSpinner,
                            searchPresenter.getBlockValuesForSelectedDistrict(selectedDistrict)
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(blockSpinner);
                makeSpinnerDefault(clusterSpinner);
                makeSpinnerDefault(schoolSpinner);
            }
        });
    }

    private void setListenerOnBlockSpinner() {
        blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isChangedFromEditText) {
                    selectedBlock = parent.getItemAtPosition(position).toString();
                    clusterSpinner.setEnabled(true);
                    clusterSpinner.setClickable(true);
                    keyboardHandler.spinner = null;
                    ArrayAdapter<String> clusterSpinnerAdapter = addValuesToSpinner(
                            clusterSpinner,
                            searchPresenter.getClusterValuesForSelectedBlock(selectedBlock)
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(clusterSpinner);
                makeSpinnerDefault(schoolSpinner);
            }
        });
    }

    private void setListenerOnClusterSpinner() {
        clusterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isChangedFromEditText) {
                    selectedCluster = parent.getItemAtPosition(position).toString();
                    schoolSpinner.setEnabled(true);
                    schoolSpinner.setClickable(true);
                    keyboardHandler.spinner = null;
                    ArrayAdapter<String> schoolSpinnerAdapter = addValuesToSpinner(
                            schoolSpinner,
                            searchPresenter.getSchoolValuesForSelectedCluster(selectedCluster)
                    );
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(schoolSpinner);
            }
        });
    }

    private void setListenerOnSchoolSpinner() {
        schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyboardHandler.spinner = null;
                if (!isChangedFromEditText) {
                    selectedSchoolName = parent.getItemAtPosition(position).toString();
                    selectedSchool = searchPresenter.getSchoolObject(selectedDistrict, selectedBlock, selectedCluster, selectedSchoolName);
                }
                if (selectedSchool != null && !isChangedFromEditText && previousUDISE != selectedSchool.udise) {
                    previousUDISE = selectedSchool.udise;
                    udiseTextBox.setText(selectedSchool.udise);
                }
                isChangedFromEditText = false;
                // Enable the next button
                nextButton.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(schoolSpinner);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private ArrayAdapter addValuesToSpinner(Spinner spinner, ArrayList<String> values) {

        String val[] = values.toArray(new String[0]);

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, val);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnTouchListener((v, event) -> {
            keyboardHandler.spinner = spinner;
            keyboardHandler.isDropDownOpen = true;
            if (keyboardHandler.isUDISEKeyboardShowing) keyboardHandler.closeUDISEKeyboard();
            return false;
        });
        return spinnerAdapter;
    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            if (selectedSchool.district.equals("Select District")) {
                SnackbarUtils.showLongSnackbar(rootView, "Please select a school using the dropdown or enter UDISE number.");
            } else {
                searchPresenter.updateStarterFile("Class 1st to 5th CHT School Visit Form", selectedSchool);
                searchPresenter.updateStarterFile("Class 1st to 8th School Visit Form", selectedSchool);
                searchPresenter.updateStarterFile("Class 9th to 12th School Visit Form", selectedSchool);
                Intent i = new Intent(getApplicationContext(),
                        FormChooserList.class);
                startActivity(i);
                finish();
            }
        });
    }

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
