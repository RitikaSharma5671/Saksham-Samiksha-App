package com.psx.ancillaryscreens.screens.profile;

import androidx.annotation.NonNull;

import com.psx.ancillaryscreens.base.BasePresenter;
import com.psx.ancillaryscreens.base.MvpInteractor;
import com.psx.ancillaryscreens.data.network.BackendCallHelper;
import com.psx.ancillaryscreens.data.network.BackendCallHelperImpl;
import com.psx.ancillaryscreens.di.modules.CommonsActivityAbstractProviders;
import com.psx.ancillaryscreens.di.modules.CommonsActivityModule;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ProfilePresenter<V extends ProfileContract.View, I extends ProfileContract.Interactor> extends BasePresenter<V, I> implements ProfileContract.Presenter<V, I> {
    /**
     * These dependencies are provided by the {@link CommonsActivityModule} and
     * {@link CommonsActivityAbstractProviders} and are required by the Presenter
     * to carry out business logic tasks related to database access and/or netwrok access.
     *
     * @param mvpInteractor       - The interactor for the Activity. Must implement {@link MvpInteractor}
     * @param apiHelper           - It is the {@link BackendCallHelperImpl} singleton instance
     *                            required for performing N/W calls.
     * @param compositeDisposable - A {@link CompositeDisposable} object that keeps a track of all the API calls being
     *                            made in the current activity context. This object allows you to dispose all the calls
     */
    @Inject
    public ProfilePresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        super(mvpInteractor, apiHelper, compositeDisposable);
    }

    /**
     * Initiates the SendOtpTask that sends an OTP on the user phone number.
     *
     * @param userPhone - The mobile number of the user on which the OTP needs to be send.
     */
    @Override
    public void startSendOTPTask(@NonNull String userPhone) {

    }

    @Override
    public void updateUserProfile() {

    }
}
