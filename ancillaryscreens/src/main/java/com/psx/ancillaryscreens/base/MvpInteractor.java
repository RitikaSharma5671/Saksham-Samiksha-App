package com.psx.ancillaryscreens.base;

import com.psx.ancillaryscreens.data.prefs.CommonsPreferenceHelper;

/**
 * This is the base interface that all 'Interactor Contracts' must extend.
 * Methods may be added as and when required.
 *
 * @author Pranav Sharma
 * @see com.psx.ancillaryscreens.screens.login.LoginContract.Interactor for example
 */
public interface MvpInteractor {
    CommonsPreferenceHelper getPreferenceHelper();
}
