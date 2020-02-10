package com.samagra.odktest.ui.SearchActivity;

import com.samagra.odktest.base.MvpPresenter;
import com.samagra.odktest.data.models.School;
import com.samagra.odktest.helper.KeyboardHandler;

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

    void loadValuesToMemory(int selectedFormID);

    void updateStarterFile(String formName, School selectedSchool);

    void addKeyboardListeners(KeyboardHandler keyboardHandler);

    boolean isUDISEValid(String udise, String previousUdise);

    List<School> getSchoolList();
}
