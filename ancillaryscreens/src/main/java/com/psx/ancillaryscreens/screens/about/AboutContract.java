package com.psx.ancillaryscreens.screens.about;

import android.os.Bundle;

import com.psx.ancillaryscreens.base.MvpInteractor;
import com.psx.ancillaryscreens.base.MvpPresenter;
import com.psx.ancillaryscreens.base.MvpView;

public interface AboutContract {
    interface View extends MvpView {
        void initToolbar();

        void setupRecyclerView();

        /**
         * Configures the AboutActivity through config values passed from the app module via {@link Bundle} object
         *
         * @param bundle - The bundle containing the config values.
         * @see AboutBundle
         */
        void configureActivityFromBundle(Bundle bundle);
    }

    interface Interactor extends MvpInteractor {

    }

    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {

    }
}

