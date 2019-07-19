package com.psx.odktest.ui.SearchActivity;

import com.psx.odktest.base.MvpPresenter;
import com.psx.odktest.data.models.School;
import com.psx.odktest.helper.KeyboardHandler;

import java.util.ArrayList;
import java.util.List;

public interface SearchMvpPresenter<V extends SearchMvpView, I extends SearchMvpInteractor> extends MvpPresenter<V, I> {

    void loadValuesToMemory();

    void updateStarterFile(String formName, School selectedSchool);

    void addKeyboardListeners(KeyboardHandler keyboardHandler);

    boolean isUDISEValid(String udise, String previousUdise);

    ArrayList<String> getDistrictValues();

    ArrayList<String> getBlockValuesForSelectedDistrict(String district);

    ArrayList<String> getClusterValuesForSelectedBlock(String selectedBlock);

    ArrayList<String> getSchoolValuesForSelectedCluster(String selectedCluster);

    School getSchoolObject(String selectedDistrict, String selectedBlock, String selectedCluster, String selectedSchoolName);

    List<School> getSchoolList();
}
