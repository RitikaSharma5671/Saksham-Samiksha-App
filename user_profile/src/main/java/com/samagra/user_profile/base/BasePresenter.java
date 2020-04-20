package com.samagra.user_profile.base;

import com.samagra.user_profile.data.network.BackendCallHelper;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * A class that serves as a base for all the presenters (handles business logic) for the activities (serves as view).
 * The class uses Java Generics. The V and I stands for View and Interactor respectively. Since View and Interactors
 * are different for each activity, Java Generics are used. The class must implement {@link MvpPresenter}.
 *
 * @author Pranav Sharma
 */
public class BasePresenter<V extends MvpView, I extends MvpInteractor> implements MvpPresenter<V, I> {

    private V mvpView;
    private I mvpInteractor;
    private BackendCallHelper apiHelper;
    private CompositeDisposable compositeDisposable;

    @Inject
    public BasePresenter(I mvpInteractor, BackendCallHelper apiHelper, CompositeDisposable compositeDisposable) {
        this.mvpInteractor = mvpInteractor;
        this.apiHelper = apiHelper;
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public V getMvpView() {
        return mvpView;
    }

    @Override
    public I getMvpInteractor() {
        return mvpInteractor;
    }

    @Override
    public BackendCallHelper getApiHelper() {
        return apiHelper;
    }

    @Override
    public CompositeDisposable getCompositeDisposable() {
        return this.compositeDisposable;
    }

    @Override
    public void onAttach(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void onDetach() {
        this.mvpView = null;
        if (this.compositeDisposable != null)
            this.compositeDisposable.dispose();
    }

    @Override
    public boolean isViewAttached() {
        return this.mvpView != null;
    }
}
