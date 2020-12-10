package com.samagra.cascading_module.ui;


import android.app.Activity;

import androidx.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.samagra.cascading_module.CascadingModuleDriver;
import com.samagra.cascading_module.R;
import com.samagra.cascading_module.base.BasePresenter;
import com.samagra.cascading_module.tasks.SearchSchoolTask;
import com.samagra.commons.Constants;
import com.samagra.commons.InstitutionInfo;
import com.samagra.grove.logging.Grove;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.odk.collect.android.forms.Form;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

/**
 * The Presenter class for Search Screen. This class controls interaction between the View and Data.
 * This class <b>must</b> implement the {@link SearchMvpPresenter} and <b>must</b> be a type of {@link BasePresenter}.
 *
 * @author Pranav Sharma
 */
public class SearchPresenter<V extends SearchMvpView, I extends SearchMvpInteractor> extends BasePresenter<V, I> implements SearchMvpPresenter<V, I> {

    private List<InstitutionInfo> listOfHostpitals = new ArrayList<>();
    private String two_Spaces = " ";

    @Inject
    public SearchPresenter(I mvpInteractor) {
        super(mvpInteractor);
    }

    //TODO : Make Asynchronous for less delay in loading
    @Override
    public void loadValuesToMemory() {
        Grove.e("Loading the .json file, conversion to ArrayList Starting...");
        File dataFile = new File(CascadingModuleDriver.FILE_PATH);
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(dataFile));

            Gson gson = new GsonBuilder()
                    .enableComplexMapKeySerialization()
                    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                    .setPrettyPrinting()
                    .setVersion(2.0)
                    .create();

            Type type = new TypeToken<ArrayList<InstitutionInfo>>() {
            }.getType();
            listOfHostpitals = gson.fromJson(jsonReader, type);
            addDummySchoolAtTheStart();
            getMvpView().initSpinners();
            jsonReader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Grove.e("Exception in loading data to memory %s", e.getMessage());
        }
    }

    private void addDummySchoolAtTheStart() {
        InstitutionInfo dummy = new InstitutionInfo(" Select the District Name",
                " Select the Block Name",
                " Select the School Name", 100000);
        listOfHostpitals.add(0, dummy);
    }

    @Override
    public void addKeyboardListeners(KeyboardHandler keyboardHandler) {
        KeyboardVisibilityEvent.setEventListener((Activity) getMvpView().getActivityContext(), isOpen -> {
            keyboardHandler.isUDISEKeyboardShowing = isOpen;
            if (isOpen) keyboardHandler.closeDropDown();
        });
    }

    @Override
    public ArrayList<String> getLevel1Values() {
        ArrayList<String> districtValues = new ArrayList<>();

        for (int i = 1; i < listOfHostpitals.size(); i++) {
            districtValues.add(listOfHostpitals.get(i).getDistrict());
        }
        ArrayList<String> newList = SearchSchoolTask.makeUnique(districtValues);
        newList.add(0, two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_district));
        return newList;
    }

    @Override
    public ArrayList<String> getLevel2ValuesUnderLevel1Set(String district) {
        ArrayList<String> blockValues = new ArrayList<>();
        for (int i = 0; i < listOfHostpitals.size(); i++) {
            if (listOfHostpitals.get(i).getDistrict().equals(district)) {
                blockValues.add(listOfHostpitals.get(i).getBlock());
            }
        }
        ArrayList<String> newList = SearchSchoolTask.makeUnique(blockValues);
        if (newList.size() > 0) {
            if (!newList.get(0).equals(two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_block)))
                newList.add(0, two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_block));
        } else {
            newList.add(0, two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_block));
        }
        return newList;
    }

    @Override
    public ArrayList<String> getLevel3ValuesUnderLevel1Set(String selectedBlock, String selectedDistrict){
        ArrayList<String> gramPanchayatsUnderBlock = new ArrayList<>();
        for (int i = 0; i < listOfHostpitals.size(); i++) {
            if (listOfHostpitals.get(i).getBlock().equals(selectedBlock) && listOfHostpitals.get(i).getDistrict().equals(selectedDistrict)) {
                gramPanchayatsUnderBlock.add(listOfHostpitals.get(i).getSchoolName());
            }
        }
        ArrayList<String> newList = SearchSchoolTask.makeUnique(gramPanchayatsUnderBlock);
        if (newList.size() > 0) {
            if (!newList.get(0).equals(two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_gram_panchayat)))
                newList.add(0, two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_gram_panchayat));
        } else {
            newList.add(0, two_Spaces + getMvpView().getActivityContext().getResources().getString(R.string.dummy_gram_panchayat));
        }
        return newList;
    }

    @Override
    public InstitutionInfo fetchObjectFromPreferenceString(String studentInfoFromPreferences) {
        try {
            Gson gson = new Gson();
            return gson.fromJson(studentInfoFromPreferences, InstitutionInfo.class);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public String generateObjectForStudentData(Object inputObject) {
        try {
            Gson gson = new Gson();
            return gson.toJson(inputObject);
        } catch ( Exception e) {
            return "";
        }
    }

    public int fetchSchoolCode(String schoolName, String selectedDistrict, String block) {
        for (int i = 0; i < listOfHostpitals.size(); i++) {
            if (listOfHostpitals.get(i).getSchoolName().equals(schoolName) &&
                    listOfHostpitals.get(i).getDistrict().equals(selectedDistrict) &&
                    listOfHostpitals.get(i).getBlock().equals(block)) {
                return  listOfHostpitals.get(i).getSchoolCode();
            }
        }
        return -1;
    }

    public void prefillData(InstitutionInfo institutionInfo) {
        Grove.d("Pre-filling data for the Forms downloaded");
        List<Form> forms = CascadingModuleDriver.iFormManagementContract.getDownloadedFormsNamesFromDatabase();
        for (Form form : forms) {
            String formName = form.getDisplayName();
            CascadingModuleDriver.iFormManagementContract.updateFormBasedOnIdentifier(formName, "district", institutionInfo.getDistrict());
            CascadingModuleDriver.iFormManagementContract.updateFormBasedOnIdentifier(formName, "block", institutionInfo.getBlock());
            CascadingModuleDriver.iFormManagementContract.updateFormBasedOnIdentifier(formName, "school", institutionInfo.getSchoolName());
            CascadingModuleDriver.iFormManagementContract.updateFormBasedOnIdentifier(formName, "user_name", getMvpInteractor().getUserName());
            CascadingModuleDriver.iFormManagementContract.updateFormBasedOnIdentifier(formName, "name", getMvpInteractor().getUserFullName());
            CascadingModuleDriver.iFormManagementContract.updateFormBasedOnIdentifier(formName, "designation", getUserRoleFromPref());
        }
    }

    private String getUserRoleFromPref() {
        return getMvpInteractor().getPreferenceHelper().getUserRoleFromPref();

    }

    public void onFillFormsOptionClicked() {
        CascadingModuleDriver.iFormManagementContract.launchFormChooserView(getMvpView().getActivityContext(), generateToolbarModificationObject(true,
                R.drawable.ic_arrow_back_white_24dp, getMvpView().getActivityContext().getResources().getString(R.string.please_select_forms), true));
    }

    private static HashMap<String, Object> generateToolbarModificationObject(boolean navigationIconDisplay,
                                                                                     int navigationIconResId,
                                                                                     @Nullable String title,
                                                                                     boolean goBackOnNavIconPress) {
        return new HashMap<String, Object>() {{
            put(Constants.CUSTOM_TOOLBAR_SHOW_NAVICON, navigationIconDisplay);
            put(Constants.CUSTOM_TOOLBAR_RESID_NAVICON, navigationIconResId);
            put(Constants.CUSTOM_TOOLBAR_BACK_NAVICON_CLICK, goBackOnNavIconPress);
            put(Constants.CUSTOM_TOOLBAR_TITLE, title);
        }};
    }


}