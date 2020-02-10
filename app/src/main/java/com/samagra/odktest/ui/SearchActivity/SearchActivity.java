package com.samagra.odktest.ui.SearchActivity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

import com.samagra.odktest.R;
import com.samagra.odktest.base.BaseActivity;
import com.samagra.odktest.data.models.School;
import com.samagra.odktest.helper.KeyboardHandler;

import org.odk.collect.android.ODKDriver;
import org.odk.collect.android.activities.FormChooserList;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.dao.FormsDao;
import org.odk.collect.android.dto.Form;
import org.odk.collect.android.provider.FormsProviderAPI;
import org.odk.collect.android.utilities.ApplicationConstants;
import org.odk.collect.android.utilities.SnackbarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.samagra.odktest.UtilityFunctions.hideKeyboard;

/**
 * View part of the Search Screen. This class only handles the UI operations, all the business logic is simply
 * abstracted from this Activity. It <b>must</b> implement the {@link SearchMvpView} and extend the {@link BaseActivity}.
 *
 * @author Pranav Sharma
 * @author Chakshu Gautam
 */
public class SearchActivity extends BaseActivity implements SearchMvpView {

    @BindView(R.id.district_spinner)
    public Spinner districtSpinner;
    @BindView(R.id.block_spinner)
    public Spinner blockSpinner;
    @BindView(R.id.school_spinner)
    public Spinner schoolSpinner;
    @BindView(R.id.next_button)
    public Button nextButton;
    @BindView(android.R.id.content)
    public FrameLayout rootView;

    private String selectedDistrict, selectedBlock, selectedSchoolName;
    School selectedSchool;
    private KeyboardHandler keyboardHandler;
    public boolean isChangedFromEditText = false;
    private Intent intent;

    int selectedFormID;

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
        intent = this.getIntent();
        setupToolbar();
        initializeKeyboardHandler();
        searchPresenter.addKeyboardListeners(keyboardHandler);
        selectedFormID = (int) intent.getExtras().getLong("selectedFormID");
        searchPresenter.loadValuesToMemory(selectedFormID);
        initSpinners();
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
        makeSpinnerDefault(blockSpinner);
        makeSpinnerDefault(schoolSpinner);

        ArrayAdapter<String> districtSpinnerAdapter = addValuesToSpinner(districtSpinner, searchPresenter.getDistrictValues());
        setListenerOnDistrictSpinner();
        setListenerOnBlockSpinner();
        setListenerOnSchoolSpinner();
    }

    private void setListenerOnDistrictSpinner() {
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDistrict = parent.getItemAtPosition(position).toString();
                keyboardHandler.spinner = null;
                ArrayAdapter<String> blockSpinnerAdapter = addValuesToSpinner(
                        blockSpinner,
                        searchPresenter.getBlockValuesForSelectedDistrict(selectedDistrict)
                );
                if(!selectedDistrict.equals(" Select District")) makeSpinnerLive(blockSpinner);
                else {
                    makeSpinnerDefault(blockSpinner);
                    makeSpinnerDefault(schoolSpinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                makeSpinnerDefault(blockSpinner);
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
                    keyboardHandler.spinner = null;
                    ArrayAdapter<String> clusterSpinnerAdapter = addValuesToSpinner(
                            schoolSpinner,
                            searchPresenter.getSchoolValuesForSelectedBlock(selectedBlock, selectedDistrict)
                    );
                    if(!selectedBlock.equals(" Select Block")) makeSpinnerLive(schoolSpinner);
                    else{
                        makeSpinnerDefault(schoolSpinner);
                    }
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
                selectedSchoolName = parent.getItemAtPosition(position).toString();
                selectedSchool = searchPresenter.getSchoolObject(selectedDistrict, selectedBlock, selectedSchoolName);
                isChangedFromEditText = false;
                // Enable the next button
                if(!selectedSchoolName.equals(" Select School")){
                    nextButton.setEnabled(true);
                }else{
                    nextButton.setEnabled(true);
                }

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
            return false;
        });
        return spinnerAdapter;
    }

    private void initNextButton() {
        nextButton.setOnClickListener(v -> {
            if (selectedSchool.district.equals(" Select District")) {
                SnackbarUtils.showLongSnackbar(rootView, "Please select a school using the dropdown.");
            } else {

                Bundle bundle = intent.getExtras();
                HashMap<String, String> forms = (HashMap<String, String>)bundle.getSerializable("forms");

                Iterator it = forms.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    searchPresenter.updateStarterFile((String) pair.getValue(), selectedSchool);
                    it.remove();
                }
                if (Collect.allowClick(getClass().getName())) {
                    // get uri to form
                    Uri formUri = ContentUris.withAppendedId(FormsProviderAPI.FormsColumns.CONTENT_URI, selectedFormID);
                    String action = getIntent().getAction();
                    if (Intent.ACTION_PICK.equals(action)) {
                        // caller is waiting on a picked form
                        setResult(RESULT_OK, new Intent().setData(formUri));
                    } else {
                        // caller wants to view/edit a form, so launch FormEntryActivity
                        Intent intent = new Intent(Intent.ACTION_EDIT, formUri);
                        intent.putExtra(ApplicationConstants.BundleKeys.FORM_MODE, ApplicationConstants.FormModes.EDIT_SAVED);
                        startActivity(intent);
                    }

                    finish();
                }
            }
        });
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

    public void makeSpinnerLive(Spinner spinner){
        spinner.setEnabled(true);
        spinner.setClickable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        searchPresenter.onDetach();
    }
}
