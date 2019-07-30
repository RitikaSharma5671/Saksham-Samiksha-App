package com.psx.odktest.ui.HomeScreen;

import com.psx.odktest.base.MvpView;

public interface HomeMvpView extends MvpView {

    void updateWelcomeText(String text);

    void showLoading(String message);

    void hideLoading();
}
