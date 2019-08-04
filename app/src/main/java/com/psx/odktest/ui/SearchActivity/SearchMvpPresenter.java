package com.psx.odktest.ui.SearchActivity;

import com.psx.odktest.base.MvpPresenter;
import com.psx.odktest.data.models.School;
import com.psx.odktest.helper.KeyboardHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The Presenter 'contract' for the SearchScreen. The {@link SearchPresenter} <b>must</b> implement this interface.
 * This interface exposes presenter methods to the view ({@link SearchActivity}) so that the business logic is defined
 * in the presenter, but can be called from the view.
 * This interface should be a type of {@link MvpPresenter}
 *
 * @author Pranav Sharma
 */
//TODO : Document the functions
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
