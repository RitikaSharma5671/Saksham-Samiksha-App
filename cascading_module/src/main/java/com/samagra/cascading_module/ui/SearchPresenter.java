package com.samagra.cascading_module.ui;


import android.app.Activity;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.samagra.cascading_module.CascadingModuleDriver;
import com.samagra.cascading_module.R;
import com.samagra.cascading_module.base.BasePresenter;
import com.samagra.cascading_module.tasks.SearchSchoolTask;
import com.samagra.commons.InstitutionInfo;
import com.samagra.grove.logging.Grove;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
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

    public int fetchSchoolCode(String selectedSchoolName) {
        for (int i = 0; i < listOfHostpitals.size(); i++) {
            if (listOfHostpitals.get(i).getSchoolName().equals(selectedSchoolName)) {
                return  listOfHostpitals.get(i).getSchoolCode();
            }
        }
        return -1;
    }
}