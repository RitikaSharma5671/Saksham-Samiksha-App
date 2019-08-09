package com.psx.ancillaryscreens.screens.about;

import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * The presenter for the Splash Screen. This class controls the interactions between the View and the data.
 * Must implement @{@link com.psx.ancillaryscreens.screens.about.AboutContract.Presenter}
 *
 * @author Pranav Sharma
 */
public class AboutPresenter<V extends AboutContract.View, I extends AboutContract.Interactor> extends BasePresenter<V, I> implements AboutContract.Presenter<V, I> {

    @Inject
    public AboutPresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        super(mvpInteractor, apiHelper, compositeDisposable);
    }
}
