package com.psx.commons.ui.login;

import com.psx.commons.base.MvpInteractor;
import com.psx.commons.base.MvpPresenter;
import com.psx.commons.base.MvpView;

public interface LoginContract {
    interface View extends MvpView {

    }

    interface Interactor extends MvpInteractor {

    }

    interface Presenter<V extends View, I extends Interactor> extends MvpPresenter<V, I> {

    }
}
